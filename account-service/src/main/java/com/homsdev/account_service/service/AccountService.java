package com.homsdev.account_service.service;

import com.homsdev.account_service.dto.AccountResponse;
import com.homsdev.account_service.dto.CreateAccountRequest;
import com.homsdev.account_service.dto.UpdateBalanceRequest;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    List<AccountResponse> getAllAccounts();

    AccountResponse getAccountById(String id);

    AccountResponse createAccount(CreateAccountRequest request);

    void deleteAccount(String id);

    BigDecimal updateAccountBalance(String id, UpdateBalanceRequest request);
}
