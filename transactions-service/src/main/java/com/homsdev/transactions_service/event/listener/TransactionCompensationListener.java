package com.homsdev.transactions_service.event.listener;

import com.homsdev.transactions_service.config.RabbitMQConfig;
import com.homsdev.transactions_service.event.dto.TransactionCompensationEvent;
import com.homsdev.transactions_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionCompensationListener {

    private final TransactionService transactionService;

    @RabbitListener(queues = RabbitMQConfig.COMPENSATION_QUEUE)
    public void handleCompensation(TransactionCompensationEvent event) {
        log.info("Compensation event received for transaction, reason: {}", event.reason());

        try {
            log.info("Compensating transaction with id: {}", event.transactionId());
            // Delete or mark transaction as failed
            transactionService.deleteTransaction(event.transactionId());

            log.info("Transaction {} compensated successfully", event.transactionId());

        } catch (Exception ex) {
            log.error("Failed to compensate transaction: {}", event.transactionId(), ex);
            throw ex; // Will be sent to DLQ
        }
    }

}
