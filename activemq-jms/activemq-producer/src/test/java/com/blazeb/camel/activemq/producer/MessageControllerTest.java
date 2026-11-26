package com.blazeb.camel.activemq.producer;

import jakarta.jms.Queue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.web.servlet.MockMvc;

import com.blazeb.camel.activemq.producer.controller.MessageController;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MessageController.class)
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JmsTemplate jmsTemplate;

    @MockBean
    private Queue queue;

    @Test
    void publishesPathMessage() throws Exception {
        mockMvc.perform(get("/api/message/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("hello"));
        verify(jmsTemplate).convertAndSend(eq(queue), eq("hello"));
    }
}
