package com.snapmocklib.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JsonConverter implements JsonConverterPort {

    private final ObjectMapper objectMapper;

    public JsonConverter() {
        this.objectMapper = new ObjectMapper();
        init();
    }

    public JsonConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        init();
    }

    private void init() {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ISO_DATE_TIME));
        objectMapper.registerModule(javaTimeModule);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        objectMapper.setVisibility(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE)
                .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE));
    }

    @Override
    public String toJson(Object obj) {
        String result;
        try {
            result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(
                    String.format(
                            "Can't serialize object %s to JSON string",
                            obj
                    ), e
            );
        }
        return result;
    }

    @Override
    public <T> T fromJson(String json, String className) throws ClassNotFoundException, JsonProcessingException {
        Class<T> clz = (Class<T>) Class.forName(className);
        return objectMapper.readValue(json, clz);
    }

    @Override
    public <T> T fromJson(String value, Class<T> valueType) throws JsonProcessingException {
        return objectMapper.readValue(value, valueType);
    }
}
