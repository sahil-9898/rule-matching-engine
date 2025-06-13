package com.sahil.matcher.rules;

import com.sahil.matcher.message.FieldPath;

import java.util.List;

public final class RuleSet {
    private final FilterRule[] filterRules;
    private final int filterRulesSize;
    public final List<FieldPath> fieldPaths;

    public RuleSet(final List<FilterRule> filterRules) {
        this.filterRules = filterRules.toArray(new FilterRule[0]);
        this.filterRulesSize = filterRules.size();
        this.fieldPaths = filterRules.stream().flatMap(rule -> rule.getFieldPath().stream()).distinct().toList();
    }

    public void matches(final Object[] fieldValues) {
        for (int i = 0; i < filterRulesSize; i++) {
            filterRules[i].matches(fieldValues);
        }
    }
}
