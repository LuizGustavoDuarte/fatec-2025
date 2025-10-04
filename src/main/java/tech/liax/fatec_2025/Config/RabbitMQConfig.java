package tech.liax.fatec_2025.Config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Value("${spring.rabbitmq.queueName}")
    public static final String QUEUE_NAME = "IMAGE-PROCESSING-QUEUE";
    @Value("${spring.rabbitmq.exchangeName}")
    public static final String EXCHANGE_NAME = "IMAGE-PROCESSING-EXCHANGE";
    @Value("${spring.rabbitmq.routingKey}")
    public static final String ROUTING_KEY = "process.image";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with(ROUTING_KEY);
    }
}
