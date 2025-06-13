package com.sahil.matcher.message.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sahil.matcher.message.FieldPath;
import com.sahil.matcher.message.MessageProcessor;

import java.util.List;

public final class JsonMessageProcessor implements MessageProcessor<String, JsonNode> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public Object[] getProcessedFields(final JsonNode message, final List<FieldPath> fieldPaths) {
        final Object[] fieldValues = new Object[fieldPaths.size()];
        for (final FieldPath fieldPath : fieldPaths) {
            fieldValues[fieldPath.valueIndex] = fieldPath.getValue(message);
        }
        return fieldValues;
    }

    @Override
    public JsonNode deserialize(final String message) throws JsonProcessingException {
        return OBJECT_MAPPER.readTree(message);
    }
}
