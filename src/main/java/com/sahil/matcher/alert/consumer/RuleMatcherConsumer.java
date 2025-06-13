package com.sahil.matcher.alert.consumer;

import com.lmax.disruptor.EventHandler;
import com.sahil.matcher.alert.DisruptorEvent;
import com.sahil.matcher.rules.RuleSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleMatcherConsumer<MessageType> implements EventHandler<DisruptorEvent<MessageType>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleMatcherConsumer.class);

    private final RuleSet ruleSet;

    private final long ordinal;

    private final long numConsumers;

    public RuleMatcherConsumer(final RuleSet ruleSet, final long ordinal, final long numConsumers) {
        this.ruleSet = ruleSet;
        this.ordinal = ordinal;
        this.numConsumers = numConsumers;
    }

    @Override
    public void onEvent(final DisruptorEvent<MessageType> event, final long seq, final boolean b) {
        if ((seq % numConsumers) != ordinal) return;
        try {
            ruleSet.matches(event.fieldValues);
        } catch (final Exception e) {
            LOGGER.error("Error while matching message", e);
        } finally {
            event.clear();
        }
    }

}
