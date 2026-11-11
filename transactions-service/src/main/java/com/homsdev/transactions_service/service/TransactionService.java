package com.homsdev.transactions_service.service;

import com.homsdev.transactions_service.dto.CreateTransactionRequest;
import com.homsdev.transactions_service.dto.TransactionResponse;

import java.util.List;

public interface TransactionService {

    TransactionResponse createTransaction(CreateTransactionRequest request);

    TransactionResponse getTransactionById(String id);

    List<TransactionResponse> getAllTransactions();

    TransactionResponse updateTransaction(String id, CreateTransactionRequest request);

    void deleteTransaction(String id);
}
