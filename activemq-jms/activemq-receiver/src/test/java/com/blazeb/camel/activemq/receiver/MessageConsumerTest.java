package com.blazeb.camel.activemq.receiver;

import com.blazeb.camel.activemq.receiver.consumer.MessageConsumer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MessageConsumerTest {

    @Test
    void acceptsMessageWithoutThrowing() {
        MessageConsumer consumer = new MessageConsumer();
        assertDoesNotThrow(() -> consumer.onMessage("hello-from-test"));
    }
}
