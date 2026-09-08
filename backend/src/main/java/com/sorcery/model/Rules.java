package com.sorcery.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class Rules {
    private String year;
    private ApplicationDates applicationDates;
    private AgeRange ageRange;
    private int places;
    private List<String> bannedFamilies;
    private List<String> unacceptableWeaknesses;
    private List<String> invitations;
    private Points points;
    private List<House> houses;


    @Data
    public static class ApplicationDates {
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate from;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate to;
    }

    @Data
    public static class AgeRange {
        private int min;
        private int max;
    }

    @Data
    public static class Points {
        private Map<String, Integer> virtue;
        private Map<String, Integer> family;
        private Map<String, Integer> weakness;
        private List<AgePoints> age;
    }

    @Data
    public static class AgePoints {
        private int from;
        private int to;
        private int points;
    }

    @Data
    public static class House {
        private String name;
        private HousePoints points;
    }

    @Data
    public static class HousePoints {
        private Map<String, Integer> virtue;
        private Map<String, Integer> weakness;
        private Map<String, Integer> family;
    }
}