package com.snapmocklib.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class JsonUtils {

    private JsonUtils() {}

    public static final ObjectMapper mapper = new ObjectMapper();

    public static String toString(JsonNode jsonNode) throws JsonProcessingException {
        final Object obj = mapper.treeToValue(jsonNode, Object.class);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }

    public static JsonNode prepareJsonNodeWithReplace(String value,
                                                Map<String, Pattern> fieldsToReplace) throws JsonProcessingException {
        JsonNode valueNode = mapper.readTree(value);
        JsonNode valueWithReplaceFields = replaceFields(valueNode, fieldsToReplace);
        return sortArrays(valueWithReplaceFields);
    }

    public static JsonNode replaceFields(JsonNode currentNode, Map<String, Pattern> fieldsToReplace) {
        if (currentNode.isArray()) {
            currentNode.forEach(jsonNode -> replaceFields(jsonNode, fieldsToReplace));
        } else if (currentNode.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> it = currentNode.fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> currentField = it.next();
                Pattern pattern = fieldsToReplace.get(currentField.getKey());
                if (pattern != null) {
                    if (pattern.matcher(currentField.getValue().asText()).matches()) {
                        replaceValue((ObjectNode) currentNode, currentField);
                    }
                } else {
                    replaceFields(currentField.getValue(), fieldsToReplace);
                }
            }
        }

        return currentNode;
    }

    public static Object deepCopy(Object object, Map<String, Pattern> fieldsToReplace, String className) {
        if (object == null) {
            return null;
        }
        try {
            return fieldsToReplace.isEmpty()
              ? mapper.readValue(mapper.writeValueAsString(object), Class.forName(className))
              : mapper.treeToValue(JsonUtils.replaceFields(mapper.valueToTree(object), fieldsToReplace), Class.forName(className));
        } catch (Exception e) {
            LogUtils.warn(String.format("Cannot deepCopy: [object=%s, fieldsToReplace=%s, className=%s], error: %s",
              object,
              fieldsToReplace,
              className,
              e.getMessage()));
            return object;
        }
    }

    private static void replaceValue(ObjectNode currentNode, Map.Entry<String, JsonNode> currentField) {
        try {
            currentNode.replace(currentField.getKey(), mapper.readTree(mapper.writeValueAsString(null)));
        } catch (Exception e) {
            // Do Nothing
        }
    }

    private static JsonNode sortArrays(JsonNode currentNode) {
        if (currentNode.isArray()) {
            for (JsonNode jsonNode : currentNode) {
                sortArrays(jsonNode);
            }
            ArrayNode sortedArray = sort((ArrayNode) currentNode);
            ((ArrayNode) currentNode).removeAll().addAll(sortedArray);
        } else if (currentNode.isObject()) {
            currentNode.fields().forEachRemaining(currentField -> sortArrays(currentField.getValue()));
        }

        return currentNode;
    }

    private static ArrayNode sort(ArrayNode arrayNode) {
        List<JsonNode> jsonNodeList = StreamSupport.stream(arrayNode.spliterator(), false)
                .sorted(new JsonNodeComparator()).collect(Collectors.toList());
        ArrayNode sortedArray = mapper.createArrayNode();
        return sortedArray.addAll(jsonNodeList);
    }

    static class JsonNodeComparator implements Comparator<JsonNode> {

        @Override
        public int compare(JsonNode prop1, JsonNode prop2) {
            try {
                return mapper.writeValueAsString(prop1).compareTo(mapper.writeValueAsString(prop2));
            } catch (JsonProcessingException e) {
                return prop1.asText().compareTo(prop2.asText());
            }
        }
    }
}
