package com.homsdev.account_service.mapper;

import com.homsdev.account_service.dto.AccountResponse;
import com.homsdev.account_service.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AccountMapperTest")
class AccountMapperTest {

    private AccountMapper accountMapper;

    @BeforeEach
    void setUp() {
        accountMapper = Mappers.getMapper(AccountMapper.class);
    }

    @Test
    @DisplayName("toAccount should handle null values")
    void toAccountWithNull() {
        Account result = accountMapper.toAccount(null);
        assertNull(result);
    }

    @Test
    @DisplayName("toAccountResponse should handle null values")
    void toAccountResponseWithNull() {
        AccountResponse result = accountMapper.toAccountResponse(null);
        assertNull(result);
    }


}