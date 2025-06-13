package com.sahil.matcher.rules;

import com.sahil.matcher.message.FieldPath;

import java.util.List;
import java.util.stream.Stream;

public final class ConditionalFilterRule implements FilterRule {

    private final Condition condition;

    private final boolean isNotRule;

    private final FilterRule[] filterRules;

    private final int filterRulesSize;

    public ConditionalFilterRule(final Condition condition, final boolean isNotRule, final List<FilterRule> filterRules) {
        this.condition = condition;
        this.isNotRule = isNotRule;
        this.filterRules = filterRules.toArray(new FilterRule[0]);
        this.filterRulesSize = filterRules.size();
    }

    @Override
    public boolean matches(final Object[] fieldValues) {
        final boolean isAnd = condition == Condition.AND;
        for (int i = 0; i < filterRulesSize; i++) {
            final boolean ruleMatches = filterRules[i].matches(fieldValues);
            if (isAnd) {
                if (!ruleMatches) return isNotRule;
            } else {
                if (ruleMatches) return isNotRule;
            }
        }
        return isAnd != isNotRule;
    }

    @Override
    public List<FieldPath> getFieldPath() {
        return Stream.of(filterRules).flatMap(rule -> rule.getFieldPath().stream()).distinct().toList();
    }
}
