package com.homsdev.account_service.service.impl;

import com.homsdev.account_service.dto.AccountResponse;
import com.homsdev.account_service.dto.CreateAccountRequest;
import com.homsdev.account_service.dto.UpdateBalanceRequest;
import com.homsdev.account_service.exception.ResourceNotFoundException;
import com.homsdev.account_service.mapper.AccountMapper;
import com.homsdev.account_service.model.Account;
import com.homsdev.account_service.repository.AccountRepository;
import com.homsdev.account_service.service.AccountService;
import com.homsdev.account_service.utils.AccountMockFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Account Service Unit Test")
@Slf4j
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    private AccountService accountService;

    private List<Account> accounts;

    @BeforeEach
    void setUp() {
        AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);

        accountService = new AccountServiceImpl(accountRepository, accountMapper);

        accounts = List.of(
                AccountMockFactory.createAccountWithName("Main"),
                AccountMockFactory.createAccountWithName("Savings"),
                AccountMockFactory.createAccountWithName("Checking")
        );
    }

    @Test
    @DisplayName("Should return all accounts")
    void getAllAccounts() {
        when(accountRepository.findAll()).thenReturn(accounts);

        List<AccountResponse> result = accountService.getAllAccounts();
        log.info("Result: {}", result);
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("Should return empty list when no accounts found")
    void getAllAccountsEmpty() {
        when(accountRepository.findAll()).thenReturn(Collections.emptyList());

        List<AccountResponse> result = accountService.getAllAccounts();
        log.info("Result: {}", result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should return account by id")
    void getAccountById() {
        when(accountRepository.findById(anyString()))
                .thenReturn(accounts.stream().findFirst());

        AccountResponse result = accountService.getAccountById("account-123");
        log.info("Result: {}", result);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when account not found")
    void testGetAccountByIdNotFound() {
        when(accountRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> accountService.getAccountById("account-123"));
    }

    @Test
    @DisplayName("Should create account")
    void testCreateAccount() {
        Account account = AccountMockFactory.createAccountWithName("Test Account");
        CreateAccountRequest req = CreateAccountRequest.builder()
                .name("Test Account")
                .currency("MXN")
                .balance(BigDecimal.valueOf(1_000)).build();

        when(accountRepository.save(any(Account.class))).thenReturn(account);

        AccountResponse result = accountService.createAccount(req);
        log.info("Result: {}", result);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Should call delete account by id 1 time")
    void testDeleteAccount() {
        accountService.deleteAccount("account-123");
        verify(accountRepository, times(1)).deleteById(anyString());
    }

    @Test
    @DisplayName("Should update account balance")
    void updateAccountBalance() {
        UpdateBalanceRequest request = UpdateBalanceRequest.builder()
                .amount(BigDecimal.valueOf(-100.00))
                .build();
        Account account = AccountMockFactory.createAccountWithName("Test Account");

        when(accountRepository.findById(anyString())).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BigDecimal result = accountService.updateAccountBalance("account-123", request);

        assertEquals(BigDecimal.valueOf(900.0), result);

    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when account not found while updating balance")
    void updateAccountBalanceNotFound() {
        UpdateBalanceRequest request = UpdateBalanceRequest.builder()
                .amount(BigDecimal.valueOf(-100.00))
                .build();

        assertThrows(ResourceNotFoundException.class,
                () -> accountService.updateAccountBalance("account-123", request));
    }


}