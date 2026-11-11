package com.homsdev.account_service.event;

import com.homsdev.account_service.dto.UpdateBalanceRequest;
import com.homsdev.account_service.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionEventsListener {

    private final AccountService accountService;

    @RabbitListener(queues = "account-queue")
    public void handleTransactionCreatedEvent(TransactionCreatedEvent event) {
        log.info("Transaction created event received: {}", event);
        UpdateBalanceRequest request = new UpdateBalanceRequest(event.amount());
        accountService.updateAccountBalance(event.accountId(), request);
        log.info("Account balance updated for account: {}", event.accountId());
    }
}
