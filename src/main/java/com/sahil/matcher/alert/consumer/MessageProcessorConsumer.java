package com.sahil.matcher.alert.consumer;

import com.lmax.disruptor.EventHandler;
import com.sahil.matcher.alert.DisruptorEvent;
import com.sahil.matcher.message.MessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MessageProcessorConsumer<MessageType, DeserializedMessageType> implements EventHandler<DisruptorEvent<MessageType>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageProcessorConsumer.class);

    private final MessageProcessor<MessageType, DeserializedMessageType> messageProcessor;

    public MessageProcessorConsumer(final MessageProcessor<MessageType, DeserializedMessageType> messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    @Override
    public void onEvent(final DisruptorEvent<MessageType> event, final long l, final boolean b) {
        try {
            event.fieldValues = messageProcessor.getProcessedFields(messageProcessor.deserialize(event.message), event.alertRuleIndex.fieldPaths);
        } catch (final Exception e) {
            LOGGER.error("Error while processing message", e);
        }
    }
}
