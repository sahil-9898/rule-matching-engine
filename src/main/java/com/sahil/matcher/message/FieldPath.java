package com.sahil.matcher.message;

import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public final class FieldPath {
    private static final Map<String, FieldPath> FIELD_PATH_CACHE = new ConcurrentHashMap<>();

    private static final Pattern SPLIT_REGEX = Pattern.compile("\\.");

    private final String raw;

    private final String[] parts;

    private final Class<?> fieldType;

    public final int valueIndex;

    private FieldPath(final String raw, final Class<?> fieldType, final int valueIndex) {
        this.raw = raw;
        this.parts = SPLIT_REGEX.split(raw);
        this.valueIndex = valueIndex;
        this.fieldType = fieldType;
    }

    public static FieldPath getOrCreateFieldPath(final String field, final Class<?> fieldType) {
        final String key = field + "#" + fieldType.getName();
        return FIELD_PATH_CACHE.computeIfAbsent(key, k -> new FieldPath(field, fieldType, FIELD_PATH_CACHE.size()));
    }

    public Object getValue(final JsonNode message) {
        JsonNode current = message;
        for (final String part : parts) {
            if (current == null) return null;
            current = current.get(part);
        }
        if (current == null || current.isNull()) {
            return null;
        }
        if (fieldType == String.class || fieldType == Boolean.class) {
            return current.asText();
        }
        if (fieldType == BigDecimal.class) {
            return current.decimalValue();
        }
        return null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o instanceof FieldPath that) {
            return Objects.equals(this.raw, that.raw) && Objects.equals(this.fieldType, that.fieldType);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(raw, fieldType);
    }
}
