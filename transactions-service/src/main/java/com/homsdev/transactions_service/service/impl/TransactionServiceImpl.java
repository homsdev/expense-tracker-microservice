package com.homsdev.transactions_service.service.impl;

import com.homsdev.transactions_service.dto.CreateTransactionRequest;
import com.homsdev.transactions_service.dto.TransactionResponse;
import com.homsdev.transactions_service.event.TransactionCreatedEvent;
import com.homsdev.transactions_service.exception.ResourceNotFoundException;
import com.homsdev.transactions_service.mapper.TransactionMapper;
import com.homsdev.transactions_service.model.Category;
import com.homsdev.transactions_service.model.Transaction;
import com.homsdev.transactions_service.repository.CategoryRepository;
import com.homsdev.transactions_service.repository.TransactionRepository;
import com.homsdev.transactions_service.service.TransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionMapper transactionMapper;
    private final RabbitTemplate rabbitTemplate;
    @Override
    @Transactional
    public TransactionResponse createTransaction(CreateTransactionRequest request) {
        Transaction transaction = transactionMapper.toTransaction(request);

        String categoryId = request.getCategoryId();
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new ResourceNotFoundException("Category", categoryId)
        );

        transaction.setCategory(category);

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Transaction saved: {}", savedTransaction);

        TransactionCreatedEvent event = new TransactionCreatedEvent(
                savedTransaction.getId(), request.getAccountId(), request.getAmount()
        );

        rabbitTemplate.convertAndSend("transaction-exchange", "transaction.created", event);
        log.info("Transaction event sent: {}", event);

        return transactionMapper.toTransactionResponse(savedTransaction);
    }

    @Override
    public TransactionResponse getTransactionById(String id) {

        Transaction transaction = transactionRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Transaction", id)
        );

        return transactionMapper.toTransactionResponse(transaction);
    }

    @Override
    public List<TransactionResponse> getAllTransactions() {
        log.info("Executing getAllTransactions");

        List<Transaction> all = transactionRepository.findAll();

        return all.stream().map(transactionMapper::toTransactionResponse).toList();
    }

    @Override
    public TransactionResponse updateTransaction(String id, CreateTransactionRequest request) {
        return null;
    }

    @Override
    public void deleteTransaction(String id) {
        if (!transactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Transaction", id);
        }
        transactionRepository.deleteById(id);
    }
}
