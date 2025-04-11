package com.example.ecbackend.domain;

import org.seasar.doma.Domain;

@Domain(valueType = String.class)
public class CsvString {
    private final String value;

    public CsvString(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
} 