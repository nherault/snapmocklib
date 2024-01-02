package com.snapmocklib.api;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface JsonConverterPort {

    String toJson(Object obj);

    <T> T fromJson(String json, String className) throws ClassNotFoundException, JsonProcessingException;

    <T> T fromJson(String value, Class<T> valueType) throws JsonProcessingException;
}
