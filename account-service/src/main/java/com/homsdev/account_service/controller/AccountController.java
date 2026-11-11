package com.homsdev.account_service.controller;

import com.homsdev.account_service.dto.AccountResponse;
import com.homsdev.account_service.dto.CreateAccountRequest;
import com.homsdev.account_service.dto.UpdateBalanceRequest;
import com.homsdev.account_service.service.AccountService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Slf4j
@OpenAPIDefinition(
        info = @Info(
                title = "Account Service API",
                version = "v1",
                description = "REST API for managing accounts and their balances."
        )
)
@Tag(name = "Accounts", description = "Account management operations")
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "List accounts", description = "Returns all accounts.")
    @ApiResponse(responseCode = "200", description = "Accounts retrieved successfully")
    List<AccountResponse> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get an account", description = "Returns an account by its identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    AccountResponse getAccountById(@PathVariable String id) {
        return accountService.getAccountById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create an account", description = "Creates a new account with an initial balance.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Account created successfully"),
            @ApiResponse(responseCode = "400", description = "Request validation failed")
    })
    AccountResponse createAccount(@RequestBody @Valid CreateAccountRequest request) {
        return accountService.createAccount(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an account", description = "Deletes an account by its identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Account deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    void deleteAccount(@PathVariable String id) {
        accountService.deleteAccount(id);
    }

    @PatchMapping("/{id}/balance")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update an account balance", description = "Updates an account balance by the requested amount.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Balance updated successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    ResponseEntity<BigDecimal> updateAccountBalance(@PathVariable String id, @RequestBody UpdateBalanceRequest request) {
        log.info("Updating account balance for account: {}", id);
        BigDecimal updatedBalance = accountService.updateAccountBalance(id, request);
        return ResponseEntity.ok(updatedBalance);
    }
}
