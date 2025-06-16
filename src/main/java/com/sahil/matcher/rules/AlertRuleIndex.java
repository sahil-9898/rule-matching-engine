package com.sahil.matcher.rules;

import com.sahil.matcher.access.UserAccessFilter;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class AlertRuleIndex {

    // Deduplicated filter rules to avoid redundant matching
    private final Map<FilterRule, Set<AlertRule>> filterRuleToAlertRules;

    // Deduplicated user access filters to avoid redundant access checks
    private final Map<FilterRule, Set<String>> accessFilterRuleToUserIds;

    // Quick lookup: userId -> user's alert rules
    private final Map<String, List<AlertRule>> userIdToAlertRules;

    // All unique filter rules (both access and alert rules)
    private final Set<FilterRule> allUniqueFilterRules;

    // All unique user access filter rules
    private final Set<FilterRule> allUniqueAccessFilterRules;

    public AlertRuleIndex(List<AlertRule> alertRules, Map<String, UserAccessFilter> userAccessFilters) {
        this.filterRuleToAlertRules = new Object2ObjectOpenHashMap<>();
        this.accessFilterRuleToUserIds = new Object2ObjectOpenHashMap<>();
        this.userIdToAlertRules = new Object2ObjectOpenHashMap<>();
        this.allUniqueFilterRules = new ObjectOpenHashSet<>();
        this.allUniqueAccessFilterRules = new ObjectOpenHashSet<>();

        buildIndex(alertRules, userAccessFilters);
    }

    private void buildIndex(List<AlertRule> alertRules, Map<String, UserAccessFilter> userAccessFilters) {
        // Index alert rules by their filter rules (deduplication)
        for (AlertRule alertRule : alertRules) {
            FilterRule filterRule = alertRule.filterRule;

            filterRuleToAlertRules.computeIfAbsent(filterRule, k -> new ObjectOpenHashSet<>())
                    .add(alertRule);

            userIdToAlertRules.computeIfAbsent(alertRule.userId, k -> new ArrayList<>())
                    .add(alertRule);

            allUniqueFilterRules.add(filterRule);
        }

        // Index user access filters by their filter rules (deduplication)
        for (UserAccessFilter userAccessFilter : userAccessFilters.values()) {
            FilterRule accessFilterRule = userAccessFilter.accessRule();

            accessFilterRuleToUserIds.computeIfAbsent(accessFilterRule, k -> new ObjectOpenHashSet<>())
                    .add(userAccessFilter.userId());

            allUniqueAccessFilterRules.add(accessFilterRule);
            allUniqueFilterRules.add(accessFilterRule);
        }
    }

    public Set<FilterRule> getAllUniqueFilterRules() {
        return allUniqueFilterRules;
    }

    public Set<FilterRule> getAllUniqueAccessFilterRules() {
        return allUniqueAccessFilterRules;
    }

    public Set<AlertRule> getAlertRulesForFilterRule(FilterRule filterRule) {
        return filterRuleToAlertRules.getOrDefault(filterRule, Set.of());
    }

    public Set<String> getUsersForAccessFilterRule(FilterRule accessFilterRule) {
        return accessFilterRuleToUserIds.getOrDefault(accessFilterRule, Set.of());
    }

    public List<AlertRule> getAlertRulesForUser(String userId) {
        return userIdToAlertRules.getOrDefault(userId, List.of());
    }
}