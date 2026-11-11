package com.homsdev.account_service.event.listener;

import com.homsdev.account_service.config.RabbitMQConfig;
import com.homsdev.account_service.dto.UpdateBalanceRequest;
import com.homsdev.account_service.event.dto.TransactionCompensationEvent;
import com.homsdev.account_service.event.dto.TransactionCreatedEvent;
import com.homsdev.account_service.exception.ResourceNotFoundException;
import com.homsdev.account_service.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionEventsListener {

    private final AccountService accountService;
    private final RabbitTemplate rabbitTemplate;

    /**
     * Handles the event when a new transaction is created.
     * This method listens to messages from the RabbitMQ queue named "account-queue".
     * Upon receiving a {@link TransactionCreatedEvent}, it logs the transaction details
     * and processes the account balance update through the {@code accountService}.
     *
     * @param event the event containing details of the transaction, including the account ID and amount to update the balance with
     */
    @RabbitListener(queues = "account-queue")
    public void handleTransactionCreatedEvent(TransactionCreatedEvent event) {
        log.info("Transaction created event received: {}", event);

        try {
            UpdateBalanceRequest request = UpdateBalanceRequest.builder()
                    .amount(event.amount())
                    .build();

            accountService.updateAccountBalance(event.accountId(), request);

            log.info("Account balance updated for account: {}", event.accountId());
        } catch (ResourceNotFoundException ex) {
            log.error("Account not found for ID: {}, initiating saga compensation", event.accountId(), ex);
            publishCompensationEvent(event, "ACCOUNT_NOT_FOUND");
        } catch (Exception ex) {
            log.error("Unexpected error processing transaction event: {}, sending to DLQ", event, ex);
            // Rethrow to let RabbitMQ send to DLX
            throw new RuntimeException("Failed to process transaction event", ex);
        }

    }

    @RabbitListener(queues = RabbitMQConfig.DLX_QUEUE)
    public void handleCompensationEvent(Message message) {
        log.error("Message received in DLQ: {}", new String(message.getBody()));
        log.error("Headers: {}", message.getMessageProperties().getHeaders());
    }

    private void publishCompensationEvent(TransactionCreatedEvent event, String reason) {
        TransactionCompensationEvent compensationEvent = new TransactionCompensationEvent(
                event.transactionId(), // transactionId - extract from event if available
                event.accountId(),
                event.amount(),
                reason
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.COMPENSATION_EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_TRANSACTION_COMPENSATE,
                compensationEvent
        );

        log.info("Compensation event published for account: {}", event.accountId());
    }

}
