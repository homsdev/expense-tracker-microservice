package com.homsdev.account_service.utils;

import com.homsdev.account_service.dto.AccountResponse;
import com.homsdev.account_service.dto.CreateAccountRequest;
import com.homsdev.account_service.model.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class AccountMockFactory {

    public static Account createAccountWithName(String name) {
        return Account.builder()
                .id(UUID.randomUUID().toString())
                .name(name)
                .currency("MXN")
                .balance(BigDecimal.valueOf(1_000))
                .creationDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();
    }

    public static AccountResponse createWithName(String name) {
        return AccountResponse.builder()
                .id(UUID.randomUUID().toString())
                .name(name)
                .currency("MXN")
                .balance(BigDecimal.valueOf(1_000))
                .build();
    }

}
