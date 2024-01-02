package com.snapmocklib.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Assertions;

import java.util.Map;
import java.util.regex.Pattern;

public final class JsonAssertUtils {

    public void assertJson(String expected, String actual, Map<String, Pattern> fieldsToReplace) throws JsonProcessingException {
        JsonNode jsonNodeExpected = JsonUtils.prepareJsonNodeWithReplace(expected, fieldsToReplace);
        JsonNode jsonNodeActual = JsonUtils.prepareJsonNodeWithReplace(actual, fieldsToReplace);
        Assertions.assertEquals(JsonUtils.toString(jsonNodeExpected), JsonUtils.toString(jsonNodeActual));
    }
}
