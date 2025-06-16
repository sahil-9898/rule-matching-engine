package com.sahil.matcher.rules;

public final class AlertRule {
    public final long alertId;
    public final String userId;
    public final FilterRule filterRule;

    public AlertRule(final long alertId, final String userId, final FilterRule filterRule) {
        this.alertId = alertId;
        this.userId = userId;
        this.filterRule = filterRule;
    }
}

/**
 * userAlertMap -> Map<UserId, List<AlertRule>>
 * For each user, check for userAccessFilter. If user has access, match all user alert rules.
 * Get the list of alerts for all matched user alert rules
 */
