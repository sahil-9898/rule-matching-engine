package com.sahil.matcher.message;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface MessageProcessor<MessageType, DeserializedMessageType> {
    Object[] getProcessedFields(DeserializedMessageType message, final List<FieldPath> fieldPaths);

    DeserializedMessageType deserialize(MessageType message) throws JsonProcessingException;
}
