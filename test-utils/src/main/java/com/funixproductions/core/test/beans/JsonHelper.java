package com.funixproductions.core.test.beans;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Component
@RequiredArgsConstructor
public class JsonHelper {

    private final ObjectMapper objectMapper;

    public <T> String toJson(T object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    public <T> T fromJson(String payload, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(payload, clazz);
    }

    public <T> T fromJson(String payload, TypeReference<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(payload, clazz);
    }

    public <T> T fromJson(String payload, Type type) {
        final Gson gson = new Gson();

        return gson.fromJson(payload, type);
    }

}
