package com.homsdev.account_service.event.dto;

import java.math.BigDecimal;

public record TransactionCompesationEvent(
        String transactionId,
        String accountId,
        BigDecimal amount,
        String reason
) {
}
