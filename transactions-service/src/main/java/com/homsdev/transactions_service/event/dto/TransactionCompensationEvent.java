package com.homsdev.transactions_service.event.dto;

import java.math.BigDecimal;

public record TransactionCompensationEvent(
        String transactionId,
        String accountId,
        BigDecimal amount,
        String reason
) {
}
