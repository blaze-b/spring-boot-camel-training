package com.blazeb.camel.activemq.producer.controller;

import jakarta.jms.Queue;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MessageController {

    private final Queue queue;
    private final JmsTemplate jmsTemplate;

    public MessageController(Queue queue, JmsTemplate jmsTemplate) {
        this.queue = queue;
        this.jmsTemplate = jmsTemplate;
    }

    @GetMapping("/message/{message}")
    public ResponseEntity<String> publish(@PathVariable("message") String message) {
        jmsTemplate.convertAndSend(queue, message);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/messages")
    public ResponseEntity<String> publishDefault() {
        String message = "Test Messages";
        jmsTemplate.convertAndSend(queue, message);
        return ResponseEntity.ok(message);
    }
}
