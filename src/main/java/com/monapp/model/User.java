package com.monapp.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public record User(String id, String name, String email) {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public Map<String, String> toMap() {
        return OBJECT_MAPPER
                .convertValue(this, new TypeReference<>() {
                });
    }

}
