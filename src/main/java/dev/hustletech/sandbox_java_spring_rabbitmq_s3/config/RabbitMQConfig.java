package dev.hustletech.sandbox_java_spring_rabbitmq_s3.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConfig.class);

    @Value("${RABBITMQ_HOST:sandbox-sinqia-rabbitmq}")
    private String rabbitmqHost;

    @Value("${RABBITMQ_PORT_AMQP:5672}")
    private int rabbitmqPort;

    @Value("${RABBITMQ_USERNAME:guest}")
    private String rabbitmqUsername;

    @Value("${RABBITMQ_PASSWORD:guest}")
    private String rabbitmqPassword;

    @Value("${RABBITMQ_QUEUE_COLORED:images-colored}")
    private String coloredQueue;

    @Value("${RABBITMQ_QUEUE_BW:images-bw}")
    private String bwQueue;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitmqHost, rabbitmqPort);
        connectionFactory.setUsername(rabbitmqUsername);
        connectionFactory.setPassword(rabbitmqPassword);
        logger.info("RabbitMQ connection created. Host: {}, Port: {}", rabbitmqHost, rabbitmqPort);
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    public Queue testQueue() {
        logger.info("Declaring 'test.queue'");
        return new Queue("test.queue", true);
    }

    @Bean
    public Queue imagesColoredQueue() {
        logger.info("Declaring 'images-colored' queue");
        return new Queue(coloredQueue, true);
    }

    @Bean
    public Queue imagesBWQueue() {
        logger.info("Declaring 'images-b&w' queue");
        return new Queue(bwQueue, true);
    }
}