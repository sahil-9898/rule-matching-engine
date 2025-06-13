package com.sahil.matcher.rules;

import jakarta.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

public enum Operator {

    CONTAINS {
        @Override
        public boolean evaluate(
                final @Nullable Object fieldValue,
                final Object value,
                final Object toValue,
                final boolean isCaseSensitive) {
            if (fieldValue == null) return false;
            final String fieldStr = fieldValue.toString();
            final String valueStr = (String) value;
            if (isCaseSensitive) {
                return fieldStr.contains(valueStr);
            }
            return StringUtils.containsIgnoreCase(fieldStr, valueStr);
        }
    },
    NOT_CONTAINS {
        @Override
        public boolean evaluate(
                final @Nullable Object fieldValue,
                final Object value,
                final Object toValue,
                final boolean isCaseSensitive) {
            return !CONTAINS.evaluate(fieldValue, value, toValue, isCaseSensitive);
        }
    },
    STARTSWITH {
        @Override
        public boolean evaluate(
                final @Nullable Object fieldValue,
                final Object value,
                final Object toValue,
                final boolean isCaseSensitive) {
            if (fieldValue == null) return false;
            final String fieldStr = fieldValue.toString();
            final String valueStr = (String) value;
            if (isCaseSensitive) {
                return fieldStr.startsWith(valueStr);
            }
            return StringUtils.startsWithIgnoreCase(fieldStr, valueStr);
        }
    },
    ENDSWITH {
        @Override
        public boolean evaluate(
                final @Nullable Object fieldValue,
                final Object value,
                final Object toValue,
                final boolean isCaseSensitive) {
            if (fieldValue == null) return false;
            final String fieldStr = fieldValue.toString();
            final String valueStr = (String) value;
            if (isCaseSensitive) {
                return fieldStr.endsWith(valueStr);
            }
            return StringUtils.endsWithIgnoreCase(fieldStr, valueStr);
        }
    },
    EQUALS {
        @Override
        public boolean evaluate(
                final @Nullable Object fieldValue,
                final Object value,
                final Object toValue,
                final boolean isCaseSensitive) {
            if (fieldValue == null) return false;
            if (value instanceof final String valueStr) {
                final String fieldStr = fieldValue.toString();
                return isCaseSensitive ? fieldStr.equals(valueStr) : fieldStr.equalsIgnoreCase(valueStr);
            } else if (value instanceof final BigDecimal decimalValue) {
                final BigDecimal fieldDecimalValue = (BigDecimal) fieldValue;
                return fieldDecimalValue.compareTo(decimalValue) == 0;
            }
            return false;
        }
    },
    NOT_EQUALS {
        @Override
        public boolean evaluate(
                final @Nullable Object fieldValue,
                final Object value,
                final Object toValue,
                final boolean isCaseSensitive) {
            return !EQUALS.evaluate(fieldValue, value, toValue, isCaseSensitive);
        }
    },
    LESSTHAN {
        @Override
        public boolean evaluate(
                final @Nullable Object fieldValue,
                final Object value,
                final Object toValue,
                final boolean isCaseSensitive) {
            if (fieldValue == null) return false;
            final BigDecimal fieldDecimalValue = (BigDecimal) fieldValue;
            final BigDecimal valueDecimalValue = (BigDecimal) value;
            return fieldDecimalValue.compareTo(valueDecimalValue) < 0;
        }
    },
    LESSTHANEQUALS {
        @Override
        public boolean evaluate(
                final @Nullable Object fieldValue,
                final Object value,
                final Object toValue,
                final boolean isCaseSensitive) {
            if (fieldValue == null) return false;
            final BigDecimal fieldDecimalValue = (BigDecimal) fieldValue;
            final BigDecimal valueDecimalValue = (BigDecimal) value;
            return fieldDecimalValue.compareTo(valueDecimalValue) <= 0;
        }
    },
    GREATERTHAN {
        @Override
        public boolean evaluate(
                final @Nullable Object fieldValue,
                final Object value,
                final Object toValue,
                final boolean isCaseSensitive) {
            if (fieldValue == null) return false;
            final BigDecimal fieldDecimalValue = (BigDecimal) fieldValue;
            final BigDecimal valueDecimalValue = (BigDecimal) value;
            return fieldDecimalValue.compareTo(valueDecimalValue) > 0;
        }
    },
    GREATERTHANEQUALS {
        @Override
        public boolean evaluate(
                final @Nullable Object fieldValue,
                final Object value,
                final Object toValue,
                final boolean isCaseSensitive) {
            if (fieldValue == null) return false;
            final BigDecimal fieldDecimalValue = (BigDecimal) fieldValue;
            final BigDecimal valueDecimalValue = (BigDecimal) value;
            return fieldDecimalValue.compareTo(valueDecimalValue) >= 0;
        }
    },
    BETWEEN {
        @Override
        public boolean evaluate(
                final @Nullable Object fieldValue,
                final Object value,
                final Object toValue,
                final boolean isCaseSensitive) {
            if (fieldValue == null) return false;
            final BigDecimal fieldDecimalValue = (BigDecimal) fieldValue;
            final BigDecimal decimalValue = (BigDecimal) value;
            final BigDecimal toDecimalValue = (BigDecimal) toValue;
            return fieldDecimalValue.compareTo(decimalValue) >= 0 && fieldDecimalValue.compareTo(toDecimalValue) <= 0;
        }
    },
    ISBLANK {
        @Override
        public boolean evaluate(
                final @Nullable Object fieldValue,
                final Object value,
                final Object toValue,
                final boolean isCaseSensitive) {
            return (fieldValue == null || fieldValue.toString().isBlank());
        }
    },
    ISNOTBLANK {
        @Override
        public boolean evaluate(
                final @Nullable Object fieldValue,
                final Object value,
                final Object toValue,
                final boolean isCaseSensitive) {
            return !ISBLANK.evaluate(fieldValue, value, toValue, isCaseSensitive);
        }
    };

    public abstract boolean evaluate(
            final @Nullable Object fieldValue,
            final Object value,
            final Object toValue,
            final boolean isCaseSensitive);
}
