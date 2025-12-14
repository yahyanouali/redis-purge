package com.monapp.redis.lowlevel.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JsonCodec {

    private final ObjectMapper mapper;

    public JsonCodec() {
        this.mapper = new ObjectMapper();
    }

    public String encode(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Encoding error", e);
        }
    }

    public <T> T decode(String json, Class<T> targetClass) {
        try {
            return mapper.readValue(json, targetClass);
        } catch (Exception e) {
            throw new RuntimeException("Decoding error", e);
        }
    }
}