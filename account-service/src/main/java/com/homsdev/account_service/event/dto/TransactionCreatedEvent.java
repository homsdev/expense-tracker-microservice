package com.homsdev.account_service.event;

import java.math.BigDecimal;

public record TransactionCreatedEvent(String accountId, BigDecimal amount) {
}
