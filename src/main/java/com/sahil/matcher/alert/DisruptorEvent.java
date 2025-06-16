package com.sahil.matcher.alert;

import com.sahil.matcher.rules.AlertRuleIndex;

public final class DisruptorEvent<MessageType> {
    public MessageType message;
    public Object[] fieldValues;
    public AlertRuleIndex alertRuleIndex;

    public void messageReceived(final MessageType message, final AlertRuleIndex alertRuleIndex) {
        this.message = message;
        this.alertRuleIndex = alertRuleIndex;
    }

    public void clear() {
        this.message = null;
        this.fieldValues = null;
        this.alertRuleIndex = null;
    }
}
