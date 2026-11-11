
package com.homsdev.account_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ Configuration for the Account Service.
 * <p>
 * This configuration class sets up the messaging infrastructure for consuming
 * transaction events from the RabbitMQ broker. It defines the necessary beans
 * for message conversion, queue creation, exchange setup, and queue-to-exchange
 * binding with routing rules.
 *
 * <h2>Messaging Flow:</h2>
 * <ol>
 *   <li>Transactions Service publishes events to the {@code transaction-exchange}</li>
 *   <li>Events are routed based on routing keys (e.g., "transaction.created")</li>
 *   <li>Account Service consumes events from {@code account-queue}</li>
 *   <li>Updates account balance based on transaction events</li>
 * </ol>
 *
 * <h2>Dependencies:</h2>
 * Requires: spring-boot-starter-amqp
 *
 * @author homsdev
 * @version 1.0
 * @see org.springframework.amqp.core.TopicExchange
 * @see org.springframework.amqp.rabbit.annotation.RabbitListener
 */
@Configuration
public class RabbitMQConfig {

    public static final String TRANSACTION_EXCHANGE = "transaction-exchange";
    public static final String ACCOUNT_QUEUE = "account-queue";
    public static final String ROUTING_KEY_TRANSACTION_CREATED = "transaction.created";

    public static final String COMPENSATION_EXCHANGE = "compensation-exchange";
    public static final String COMPENSATION_QUEUE = "transaction-compensation-queue";
    public static final String ROUTING_KEY_TRANSACTION_COMPENSATE = "transaction.compensate";

    public static final String DLX_EXCHANGE = "dlx";
    public static final String DLX_QUEUE = "account-queue-dlq";
    public static final String DLX_ROUTING_KEY = "account.dlq";

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    // ============ TRANSACTION EVENTS ============
    @Bean
    public Queue accountQueue() {
        return QueueBuilder.durable(ACCOUNT_QUEUE)
                .deadLetterExchange(DLX_EXCHANGE)
                .deadLetterRoutingKey(DLX_ROUTING_KEY)
                .build();
    }

    @Bean
    public TopicExchange transactionExchange() {
        return new TopicExchange(TRANSACTION_EXCHANGE);
    }

    @Bean
    public Binding binding(Queue accountQueue, TopicExchange transactionExchange) {
        return BindingBuilder.bind(accountQueue)
                .to(transactionExchange)
                .with(ROUTING_KEY_TRANSACTION_CREATED);
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

    // ============ DEAD LETTER EXCHANGE ============

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLX_QUEUE).build();
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange(DLX_EXCHANGE);
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, TopicExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with(DLX_ROUTING_KEY);
    }
}