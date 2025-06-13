package com.sahil.matcher.alert;

import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import com.sahil.matcher.alert.consumer.MessageProcessorConsumer;
import com.sahil.matcher.alert.consumer.RuleMatcherConsumer;
import com.sahil.matcher.config.AlertGeneratorConfig;
import com.sahil.matcher.message.MessageProcessor;
import com.sahil.matcher.rules.RuleSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class AlertGenerator<MessageType, DeserializedMessageType> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlertGenerator.class);

    private final AlertGeneratorConfig alertGeneratorConfig;

    private final Disruptor<DisruptorEvent<MessageType>> disruptor;

    public AlertGenerator(
            final AlertGeneratorConfig alertGeneratorConfig,
            final MessageProcessor<MessageType, DeserializedMessageType> messageProcessor) {
        this.alertGeneratorConfig = alertGeneratorConfig;
        final RuleSet ruleSet = new RuleSet(List.of());
        final int numberOfThreads = alertGeneratorConfig.disruptorConfig().ruleMatcherConsumers();
        final RuleMatcherConsumer<MessageType>[] ruleMatcherConsumers = new RuleMatcherConsumer[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            ruleMatcherConsumers[i] = new RuleMatcherConsumer<>(ruleSet, i, numberOfThreads);
        }
        final MessageProcessorConsumer<MessageType, DeserializedMessageType> messageProcessorConsumer = new MessageProcessorConsumer<>(messageProcessor, ruleSet);
        this.disruptor = new Disruptor<>(DisruptorEvent::new, alertGeneratorConfig.disruptorConfig().ringBufferSize(), DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, alertGeneratorConfig.disruptorConfig().waitStrategy().create());
        this.disruptor.handleEventsWith(messageProcessorConsumer).then(ruleMatcherConsumers);
    }

    public void start() {
        LOGGER.info("Started alert generator for {}", alertGeneratorConfig.messageType());
    }

    public void startDisruptor() {
        this.disruptor.start();
    }
}
