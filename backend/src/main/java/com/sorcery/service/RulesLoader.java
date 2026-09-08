package com.sorcery.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sorcery.model.Rules;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class RulesLoader {

    private Rules rules;

    @PostConstruct
    public void load() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        InputStream input = new ClassPathResource("rules.json").getInputStream();
        rules = mapper.readValue(input, Rules.class);
    }

    public Rules getRules() {
        return rules;
    }
}