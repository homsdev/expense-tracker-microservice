package com.homsdev.account_service.repository;

import com.homsdev.account_service.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Account Repository Unit Test")
class AccountRepositoryTest {

    @Mock
    private AccountRepository accountRepository;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = Account.builder()
                .id("account-123")
                .name("Test Account")
                .currency("MXN")
                .balance(BigDecimal.valueOf(1000.00))
                .build();
    }

    @Test
    @DisplayName("Should find all accounts")
    void testFindAll() {
        when(accountRepository.findAll())
                .thenReturn(List.of(testAccount));

        List<Account> all = accountRepository.findAll();

        assertNotNull(all);
        assertEquals(1, all.size());
    }

    @Test
    @DisplayName("Should save account and return saved instance")
    void testSaveAccount() {
        when(accountRepository.save(any(Account.class)))
                .thenReturn(testAccount);

        Account savedAccount = accountRepository.save(testAccount);
        assertNotNull(savedAccount);
    }
}