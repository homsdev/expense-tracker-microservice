package com.homsdev.transactions_service.event;

import java.math.BigDecimal;

public record TransactionCreatedEvent(
        String transactionId,
        String accountId,
        BigDecimal amount) {
}
