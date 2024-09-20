package dev.hustletech.sandbox_java_spring_rabbitmq_s3.adapter.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQService {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQService.class);

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitMQService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public String sendPing() {
        String message = "ping";
        String routingKey = "test.queue";
        try {
            rabbitTemplate.convertAndSend(routingKey, message);
            logger.info("Sent 'ping' message to queue: {}", routingKey);
            return "Sent 'ping' message to RabbitMQ";
        } catch (Exception e) {
            logger.error("Failed to send message to RabbitMQ: {}", e.getMessage());
            return "Failed to send 'ping' message to RabbitMQ";
        }
    }
}
