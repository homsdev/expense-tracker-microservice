package com.homsdev.transactions_service.service.impl;

import com.homsdev.transactions_service.dto.CreateTransactionRequest;
import com.homsdev.transactions_service.dto.TransactionResponse;
import com.homsdev.transactions_service.enums.TransactionType;
import com.homsdev.transactions_service.event.TransactionCreatedEvent;
import com.homsdev.transactions_service.exception.ResourceNotFoundException;
import com.homsdev.transactions_service.mapper.TransactionMapper;
import com.homsdev.transactions_service.model.Category;
import com.homsdev.transactions_service.model.Transaction;
import com.homsdev.transactions_service.repository.CategoryRepository;
import com.homsdev.transactions_service.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    void createTransactionSavesWithCategoryAndPublishesEvent() {
        CreateTransactionRequest request = buildRequest();
        Transaction mapped = new Transaction();
        Category category = new Category();
        Transaction saved = new Transaction();
        saved.setId("tx-1");
        TransactionResponse response = new TransactionResponse();

        when(transactionMapper.toTransaction(request)).thenReturn(mapped);
        when(categoryRepository.findById("cat-1")).thenReturn(Optional.of(category));
        when(transactionRepository.save(mapped)).thenReturn(saved);
        when(transactionMapper.toTransactionResponse(saved)).thenReturn(response);

        TransactionResponse result = transactionService.createTransaction(request);

        assertThat(mapped.getCategory()).isSameAs(category);
        assertThat(result).isSameAs(response);

        ArgumentCaptor<TransactionCreatedEvent> eventCaptor = ArgumentCaptor.forClass(TransactionCreatedEvent.class);
        verify(rabbitTemplate).convertAndSend(eq("transaction-exchange"), eq("transaction.created"), eventCaptor.capture());
        assertThat(eventCaptor.getValue().transactionId()).isEqualTo("tx-1");
        assertThat(eventCaptor.getValue().accountId()).isEqualTo("acc-1");
        assertThat(eventCaptor.getValue().amount()).isEqualByComparingTo("25.50");
    }

    @Test
    void createTransactionThrowsWhenCategoryNotFoundAndDoesNotSave() {
        CreateTransactionRequest request = buildRequest();

        when(transactionMapper.toTransaction(request)).thenReturn(new Transaction());
        when(categoryRepository.findById("cat-1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.createTransaction(request))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(transactionRepository, never()).save(any());
        verify(rabbitTemplate, never()).convertAndSend(any(String.class), any(String.class), any(Object.class));
    }

    @Test
    void getTransactionByIdReturnsTransactionWhenFound() {
        Transaction transaction = new Transaction();
        TransactionResponse response = new TransactionResponse();

        when(transactionRepository.findById("tx-1")).thenReturn(Optional.of(transaction));
        when(transactionMapper.toTransactionResponse(transaction)).thenReturn(response);

        assertThat(transactionService.getTransactionById("tx-1")).isSameAs(response);
    }

    @Test
    void getTransactionByIdThrowsWhenNotFound() {
        when(transactionRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.getTransactionById("missing"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getAllTransactionsMapsEveryTransaction() {
        Transaction first = new Transaction();
        Transaction second = new Transaction();
        TransactionResponse firstResponse = new TransactionResponse();
        TransactionResponse secondResponse = new TransactionResponse();

        when(transactionRepository.findAll()).thenReturn(List.of(first, second));
        when(transactionMapper.toTransactionResponse(first)).thenReturn(firstResponse);
        when(transactionMapper.toTransactionResponse(second)).thenReturn(secondResponse);

        assertThat(transactionService.getAllTransactions()).containsExactly(firstResponse, secondResponse);
    }

    @Test
    void deleteTransactionDeletesWhenExists() {
        when(transactionRepository.existsById("tx-1")).thenReturn(true);

        transactionService.deleteTransaction("tx-1");

        verify(transactionRepository).deleteById("tx-1");
    }

    @Test
    void deleteTransactionThrowsWhenNotFoundAndDoesNotDelete() {
        when(transactionRepository.existsById("missing")).thenReturn(false);

        assertThatThrownBy(() -> transactionService.deleteTransaction("missing"))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(transactionRepository, never()).deleteById(any());
    }

    private CreateTransactionRequest buildRequest() {
        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setType(TransactionType.EXPENSE);
        request.setAmount(new BigDecimal("25.50"));
        request.setAccountId("acc-1");
        request.setCategoryId("cat-1");
        return request;
    }
}
