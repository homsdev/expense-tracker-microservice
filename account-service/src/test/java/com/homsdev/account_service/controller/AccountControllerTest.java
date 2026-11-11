
package com.homsdev.account_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homsdev.account_service.dto.AccountResponse;
import com.homsdev.account_service.dto.CreateAccountRequest;
import com.homsdev.account_service.dto.UpdateBalanceRequest;
import com.homsdev.account_service.exception.ResourceNotFoundException;
import com.homsdev.account_service.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@DisplayName("Account Controller Tests")
@Slf4j
@TestPropertySource(properties = {
        "eureka.client.enabled=false",
        "spring.cloud.config.enabled=false"
})
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AccountService accountService;

    private AccountResponse testAccountResponse;
    private CreateAccountRequest createAccountRequest;
    private UpdateBalanceRequest updateBalanceRequest;

    @BeforeEach
    void setUp() {
        testAccountResponse = AccountResponse.builder()
                .id("account-123")
                .name("Test Account")
                .currency("MXN")
                .balance(BigDecimal.valueOf(1000.00))
                .build();

        createAccountRequest = CreateAccountRequest.builder()
                .name("New Account")
                .currency("MXN")
                .balance(BigDecimal.valueOf(500.00))
                .build();

        updateBalanceRequest = UpdateBalanceRequest.builder()
                .amount(BigDecimal.valueOf(100.00))
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/accounts - Should handle unexpected error")
    void testUnexpectedError() throws Exception{
        doThrow(new RuntimeException("Unexpected error")).when(accountService).getAllAccounts();
        mockMvc.perform(get("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("GET /api/v1/accounts - Should return all accounts")
    void testGetAllAccounts() throws Exception {
        // Arrange
        List<AccountResponse> accounts = List.of(testAccountResponse);
        when(accountService.getAllAccounts()).thenReturn(accounts);

        // Act & Assert
        mockMvc.perform(get("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("account-123")))
                .andExpect(jsonPath("$[0].name", is("Test Account")))
                .andExpect(jsonPath("$[0].balance", is(1000.00)));

        // Verify
        verify(accountService, times(1)).getAllAccounts();
    }

    @Test
    @DisplayName("GET /api/v1/accounts - Should return empty list when no accounts exist")
    void testGetAllAccountsEmpty() throws Exception {
        // Arrange
        when(accountService.getAllAccounts()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(accountService, times(1)).getAllAccounts();
    }

    @Test
    @DisplayName("GET /api/v1/accounts/{id} - Should return account by id")
    void testGetAccountById() throws Exception {
        // Arrange
        when(accountService.getAccountById("account-123")).thenReturn(testAccountResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/accounts/account-123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("account-123")))
                .andExpect(jsonPath("$.name", is("Test Account")))
                .andExpect(jsonPath("$.currency", is("MXN")))
                .andExpect(jsonPath("$.balance", is(1000.00)));

        verify(accountService, times(1)).getAccountById("account-123");
    }

    @Test
    @DisplayName("GET /api/v1/accounts/{id} - Should return 404 when account not found")
    void testGetAccountByIdNotFound() throws Exception {
        // Arrange
        when(accountService.getAccountById("invalid-id"))
                .thenThrow(new ResourceNotFoundException("Account", "invalid-id"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/accounts/invalid-id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(accountService, times(1)).getAccountById("invalid-id");
    }

    @Test
    @DisplayName("POST /api/v1/accounts - Should create new account")
    void testCreateAccount() throws Exception {
        // Arrange
        AccountResponse createdAccount = AccountResponse.builder()
                .id("account-456")
                .name("New Account")
                .currency("MXN")
                .balance(BigDecimal.valueOf(500.00))
                .build();
        when(accountService.createAccount(any(CreateAccountRequest.class)))
                .thenReturn(createdAccount);

        // Act & Assert
        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createAccountRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("account-456")))
                .andExpect(jsonPath("$.name", is("New Account")))
                .andExpect(jsonPath("$.balance", is(500.00)));

        verify(accountService, times(1)).createAccount(any(CreateAccountRequest.class));
    }

    @Test
    @DisplayName("POST /api/v1/accounts - Should return 400 for invalid request body")
    void testCreateAccountInvalidRequest() throws Exception {
        // Arrange
        String invalidJson = "{ \"invalid\": \"data\" }";

        // Act & Assert
        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(accountService, never()).createAccount(any());
    }

    @Test
    @DisplayName("DELETE /api/v1/accounts/{id} - Should delete account")
    void testDeleteAccount() throws Exception {
        // Arrange
        doNothing().when(accountService).deleteAccount("account-123");

        // Act & Assert
        mockMvc.perform(delete("/api/v1/accounts/account-123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(accountService, times(1)).deleteAccount("account-123");
    }

    @Test
    @DisplayName("PATCH /api/v1/accounts/{id}/balance - Should update account balance")
    void testUpdateAccountBalance() throws Exception {
        // Arrange
        BigDecimal updatedBalance = BigDecimal.valueOf(1100.00);
        when(accountService.updateAccountBalance("account-123", updateBalanceRequest))
                .thenReturn(updatedBalance);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/accounts/account-123/balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateBalanceRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("1100.0"));

        verify(accountService, times(1))
                .updateAccountBalance("account-123", updateBalanceRequest);
    }

    @Test
    @DisplayName("PATCH /api/v1/accounts/{id}/balance - Should return 404 when account not found")
    void testUpdateAccountBalanceNotFound() throws Exception {
        // Arrange
        when(accountService.updateAccountBalance("invalid-id", updateBalanceRequest))
                .thenThrow(new ResourceNotFoundException("Account", "invalid-id"));

        // Act & Assert
        mockMvc.perform(patch("/api/v1/accounts/invalid-id/balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateBalanceRequest)))
                .andExpect(status().isNotFound());

        verify(accountService, times(1))
                .updateAccountBalance("invalid-id", updateBalanceRequest);
    }
}