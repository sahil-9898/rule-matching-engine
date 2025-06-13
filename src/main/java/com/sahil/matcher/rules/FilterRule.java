package com.sahil.matcher.rules;

import com.sahil.matcher.message.FieldPath;

import java.util.List;

public interface FilterRule {
    boolean matches(Object[] fieldValues);

    List<FieldPath> getFieldPath();
}
