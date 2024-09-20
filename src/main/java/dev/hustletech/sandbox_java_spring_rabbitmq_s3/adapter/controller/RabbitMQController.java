package dev.hustletech.sandbox_java_spring_rabbitmq_s3.adapter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.hustletech.sandbox_java_spring_rabbitmq_s3.adapter.service.RabbitMQService;

@RestController
@RequestMapping("/api/rabbitmq")
public class RabbitMQController {

    private final RabbitMQService rabbitMQService;

    @Autowired
    public RabbitMQController(RabbitMQService rabbitMQService) {
        this.rabbitMQService = rabbitMQService;
    }

    @GetMapping("/ping")
    public String sendPing() {
        return rabbitMQService.sendPing();
    }
}