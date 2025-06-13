package com.sahil.matcher.alert;

public final class DisruptorEvent<MessageType> {
    public MessageType message;
    public Object[] fieldValues;

    public void messageReceived(final MessageType message) {
        this.message = message;
    }

    public void clear() {
        this.message = null;
        this.fieldValues = null;
    }
}
