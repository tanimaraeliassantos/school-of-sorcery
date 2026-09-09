package com.sorcery.service;

import com.sorcery.model.Application;
import com.sorcery.model.Rules;
import com.sorcery.model.RejectionReason;
import com.sorcery.model.AdmissionResult;

import java.util.Optional;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

import org.springframework.stereotype.Service;

@Service
public class AdmissionService {
    // Invitaciones
    private boolean isInvited(Application app, Rules rules) {
        String fullName = app.getFirstName() + " " + app.getFamilyName();
        return rules.getInvitations().contains(fullName);
    }

    // Vetos
    private Optional<RejectionReason> applyVetoes(Application app, Rules rules) {
        // Familia prohibida - prioridad 1
        if (rules.getBannedFamilies().contains(app.getFamilyName())) {
            return Optional.of(RejectionReason.BANNED_FAMILY);
        }
        // Edad fuera de rango - prioridad 2
        if (app.getAge() < rules.getAgeRange().getMin() ||
                app.getAge() > rules.getAgeRange().getMax()) {
            return Optional.of(RejectionReason.OUT_OF_AGE);
        }
        // Debilidad Inaceptable - prioridad 3
        if (rules.getUnacceptableWeaknesses().contains(app.getWeakness())) {
            return Optional.of(RejectionReason.UNACCEPTABLE_WEAKNESS);
        }

        // Fecha fuera del periodo - prioridad 4
        if (app.getApplicationDate().isBefore(rules.getApplicationDates().getFrom()) ||
                app.getApplicationDate().isAfter(rules.getApplicationDates().getTo())) {
            return Optional.of(RejectionReason.OUT_OF_DATE);
        }

        return Optional.empty();
    }

    // Puntuación
    public int calculateScore(Application app, Rules rules) {
        int score = 0;

        // Virtud
        score += rules.getPoints().getVirtue()
                .getOrDefault(app.getVirtue(), 0);

        // Familia
        score += rules.getPoints().getFamily()
                .getOrDefault(app.getFamilyName(), 0);

        // Debilidad
        score += rules.getPoints().getWeakness()
                .getOrDefault(app.getWeakness(), 0);

        // Edad
        for (Rules.AgePoints agePoints : rules.getPoints().getAge()) {
            if (app.getAge() >= agePoints.getFrom() &&
                    app.getAge() <= agePoints.getTo()) {
                score += agePoints.getPoints();
                break;
            }
        }
        return score;
    }

    // Ordenación
    private List<Application> sort(List<Application> apps, Rules rules) {
        return apps.stream()
                .sorted(Comparator
                        .comparingInt((Application a) -> calculateScore(a, rules))
                        .reversed()
                        .thenComparingInt(Application::getAge)
                        .thenComparing(Application::getFamilyName)
                        .thenComparing(Application::getFirstName))
                .toList();

    }

    // PROCESO ENTERO
    public List<AdmissionResult> processAdmissions(
            List<Application> applications, Rules rules) {
        List<AdmissionResult> results = new ArrayList<>();
        List<Application> eligible = new ArrayList<>();

        for (Application app : applications) {

            if (isInvited(app, rules)) {
                results.add(AdmissionResult.accepted(
                        app, 0, 0, null, true));
                continue;
            }

            Optional<RejectionReason> veto = applyVetoes(app, rules);
            if (veto.isPresent()) {
                String detail = buildVetoDetail(veto.get(), app);
                results.add(AdmissionResult.rejected(
                        app, 0, null, veto.get(), detail));
                continue;
            }
            eligible.add(app);
        }

        int placesUsedByInvitations = (int) results.stream()
                .filter(r -> r.isInvitedByHeadmaster()).count();
        int availablePlaces = rules.getPlaces() - placesUsedByInvitations;

        List<Application> sorted = sort(eligible, rules);

        for (int i = 0; i < sorted.size(); i++) {
            Application app = sorted.get(i);
            int score = calculateScore(app, rules);
            int position = i + 1;

            if (i < availablePlaces) {
                results.add(AdmissionResult.accepted(
                        app, score, position, null, false));
            } else {
                results.add(AdmissionResult.rejected(
                        app, score, position, RejectionReason.NO_PLACE,
                        "Ranked " + position + " — no places remaining"));
            }
        }

        return results;
    }

    private String buildVetoDetail(RejectionReason reason, Application app) {
        return switch (reason) {
            case BANNED_FAMILY ->
                "Family " + app.getFamilyName() + " is banned";
            case OUT_OF_AGE ->
                "Age " + app.getAge() + " is outside the accepted range";
            case UNACCEPTABLE_WEAKNESS ->
                "Weakness " + app.getWeakness() + " is not accepted.";
            case OUT_OF_DATE ->
                "Application date " + app.getApplicationDate() +
                        " is outside the application period";
            default -> " ";
        };
    }

}
