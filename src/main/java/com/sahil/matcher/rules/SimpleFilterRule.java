package com.sahil.matcher.rules;

import com.sahil.matcher.message.FieldPath;

import java.math.BigDecimal;
import java.util.List;

public final class SimpleFilterRule implements FilterRule {

    private final FieldPath fieldPath;

    private final Object value;

    private final Object toValue;

    private final Operator operator;

    private final boolean isNotRule;

    private final boolean isCaseSensitive;

    public SimpleFilterRule(
            final String field,
            final DataType dataType,
            final Object value,
            final Object toValue,
            final Operator operator,
            final boolean isNotRule,
            final boolean isCaseSensitive) {
        this.fieldPath = FieldPath.getOrCreateFieldPath(field, getFieldType(dataType));
        this.value = normalizeValue(value, dataType);
        this.toValue = normalizeValue(toValue, dataType);
        this.operator = operator;
        this.isNotRule = isNotRule;
        this.isCaseSensitive = isCaseSensitive;
    }

    @Override
    public boolean matches(final Object[] fieldValues) {
        return true;
    }

    @Override
    public List<FieldPath> getFieldPath() {
        return List.of(this.fieldPath);
    }

    private static Class<?> getFieldType(final DataType dataType) {
        return switch (dataType) {
            case NUMBER -> BigDecimal.class;
            case STRING, BOOLEAN -> String.class;
        };
    }

    private static Object normalizeValue(final Object value, final DataType dataType) {
        if (value == null) return null;
        return switch (dataType) {
            case STRING, BOOLEAN -> value.toString();
            case NUMBER -> new BigDecimal(value.toString());
        };
    }
}
