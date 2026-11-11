package com.homsdev.transactions_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String TRANSACTION_EXCHANGE = "transaction-exchange";

    public static final String COMPENSATION_EXCHANGE = "compensation-exchange";
    public static final String COMPENSATION_QUEUE = "transaction-compensation-queue";
    public static final String ROUTING_KEY_TRANSACTION_COMPENSATE = "transaction.compensate";

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public TopicExchange transactionEventsExchange() {
        return new TopicExchange(TRANSACTION_EXCHANGE);
    }

    // ============ COMPENSATION EVENTS (SAGA ROLLBACK) ============

    @Bean
    public Queue compensationQueue() {
        return QueueBuilder.durable(COMPENSATION_QUEUE).build();
    }

    @Bean
    public TopicExchange compensationExchange() {
        return new TopicExchange(COMPENSATION_EXCHANGE);
    }

    @Bean
    public Binding compensationBinding(Queue compensationQueue, TopicExchange compensationExchange) {
        return BindingBuilder.bind(compensationQueue)
                .to(compensationExchange)
                .with(ROUTING_KEY_TRANSACTION_COMPENSATE);
    }

}
