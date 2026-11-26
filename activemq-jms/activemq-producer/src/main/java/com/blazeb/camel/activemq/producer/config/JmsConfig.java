package com.blazeb.camel.activemq.producer.config;

import jakarta.jms.Queue;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;

@Configuration
@EnableJms
public class JmsConfig {

    @Bean
    public Queue demoQueue(@Value("${activemq.queue.name}") String queueName) {
        return new ActiveMQQueue(queueName);
    }
}
