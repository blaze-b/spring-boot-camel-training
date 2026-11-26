package com.blazeb.camel.activemq.receiver.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

    private static final Logger log = LoggerFactory.getLogger(MessageConsumer.class);

    @JmsListener(destination = "${activemq.queue.name}")
    public void onMessage(String message) {
        log.info("Message received: {}", message);
    }
}
