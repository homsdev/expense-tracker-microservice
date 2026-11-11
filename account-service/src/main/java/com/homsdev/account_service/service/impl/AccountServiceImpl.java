package com.homsdev.account_service.service.impl;

import com.homsdev.account_service.dto.AccountResponse;
import com.homsdev.account_service.dto.CreateAccountRequest;
import com.homsdev.account_service.dto.UpdateBalanceRequest;
import com.homsdev.account_service.exception.ResourceNotFoundException;
import com.homsdev.account_service.mapper.AccountMapper;
import com.homsdev.account_service.model.Account;
import com.homsdev.account_service.repository.AccountRepository;
import com.homsdev.account_service.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(accountMapper::toAccountResponse)
                .toList();
    }

    @Override
    public AccountResponse getAccountById(String id) {
        Account account = accountRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Account", id)
        );
        return accountMapper.toAccountResponse(account);
    }

    @Override
    public AccountResponse createAccount(CreateAccountRequest request) {
        Account account = accountMapper.toAccount(request);
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toAccountResponse(savedAccount);
    }

    @Override
    public void deleteAccount(String id) {
        accountRepository.deleteById(id);
    }

    @Override
    public BigDecimal updateAccountBalance(String id, UpdateBalanceRequest request) {
        Account account = accountRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Account", id)
        );
        BigDecimal newBalance = account.getBalance().add(request.getAmount());
        account.setBalance(newBalance);
        accountRepository.save(account);
        return newBalance;
    }
}
