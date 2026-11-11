package com.homsdev.account_service.event.dto;

import java.math.BigDecimal;

public record TransactionCreatedEvent(
        String transactionId,
        String accountId,
        BigDecimal amount) {
}
