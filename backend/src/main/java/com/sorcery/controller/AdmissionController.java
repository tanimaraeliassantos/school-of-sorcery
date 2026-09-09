package com.sorcery.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sorcery.model.AdmissionResult;
import com.sorcery.model.Application;
import com.sorcery.model.Rules;
import com.sorcery.service.AdmissionService;
import com.sorcery.service.HouseService;
import com.sorcery.service.RulesLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class AdmissionController {

    private final RulesLoader rulesLoader;
    private final AdmissionService admissionService;
    private final HouseService houseService;

    public AdmissionController(RulesLoader rulesLoader,
            AdmissionService admissionService,
            HouseService houseService) {
        this.rulesLoader = rulesLoader;
        this.admissionService = admissionService;
        this.houseService = houseService;
    }

    @PostMapping("/admissions")
    public ResponseEntity<?> processAdmissions(
            @RequestParam("file") MultipartFile file) {

        try {
            // Parsear el JSON
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            ApplicationsWrapper wrapper = mapper.readValue(
                    file.getInputStream(), ApplicationsWrapper.class);

            List<Application> applications = wrapper.getApplications();
            Rules rules = rulesLoader.getRules();

            // Procesar admisiones
            List<AdmissionResult> results = admissionService.processAdmissions(applications, rules);

            // Asignar casas a los aceptados
            results.stream()
                    .filter(r -> "ACCEPTED".equals(r.getStatus())
                            && !r.isInvitedByHeadmaster())
                    .forEach(r -> r.setHouse(
                            houseService.assignHouse(r.getApplication(), rules)));

            // Asignar casa al invitado
            results.stream()
                    .filter(AdmissionResult::isInvitedByHeadmaster)
                    .forEach(r -> r.setHouse(
                            houseService.assignHouse(r.getApplication(), rules)));

            // Separar aceptados y rechazados
            Map<String, List<AdmissionResult>> response = new HashMap<>();
            response.put("accepted", results.stream()
                    .filter(r -> "ACCEPTED".equals(r.getStatus()))
                    .toList());
            response.put("rejected", results.stream()
                    .filter(r -> "REJECTED".equals(r.getStatus()))
                    .toList());

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            return ResponseEntity.badRequest()
                    .body("Invalid file: " + e.getMessage());
        }
    }

    // Clase auxiliar para deserializar el JSON
    @lombok.Data
    static class ApplicationsWrapper {
        private List<Application> applications;
    }
}
