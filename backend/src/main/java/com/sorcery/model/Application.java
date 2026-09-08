package com.sorcery.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class Application {
    private String id;
    private String firstName;
    private String familyName;
    private int age;
    private String virtue;
    private String weakness;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate applicationDate;
}
