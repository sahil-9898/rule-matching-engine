package com.sahil.matcher.rules;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Component
public final class AlertRuleIndexManager {

    private final AtomicReference<AlertRuleIndex> alertRuleIndex = new AtomicReference<>();

    private final AlertRuleService alertRuleService;

    public AlertRuleIndexManager(final AlertRuleService alertRuleService) {
        this.alertRuleService = alertRuleService;
        this.reloadAlertRules();
    }

    public void reloadAlertRules() {
        final List<AlertRule> alertRules = alertRuleService.getAllAlertRules();
        final AlertRuleIndex index = new AlertRuleIndex(alertRules);
        this.alertRuleIndex.set(index);
    }

    public AlertRuleIndex getCurrentIndex() {
        return alertRuleIndex.get();
    }
}
