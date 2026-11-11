package com.homsdev.transactions_service.event.listener;

import com.homsdev.transactions_service.event.dto.TransactionCompensationEvent;
import com.homsdev.transactions_service.exception.ResourceNotFoundException;
import com.homsdev.transactions_service.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionCompensationListenerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionCompensationListener listener;

    @Test
    void handleCompensationDeletesTheTransaction() {
        TransactionCompensationEvent event = new TransactionCompensationEvent(
                "tx-1", "acc-1", new BigDecimal("10.00"), "account service failed");

        listener.handleCompensation(event);

        verify(transactionService).deleteTransaction("tx-1");
    }

    @Test
    void handleCompensationRethrowsWhenDeletionFails() {
        TransactionCompensationEvent event = new TransactionCompensationEvent(
                "missing", "acc-1", new BigDecimal("10.00"), "account service failed");
        doThrow(new ResourceNotFoundException("Transaction", "missing"))
                .when(transactionService).deleteTransaction("missing");

        assertThatThrownBy(() -> listener.handleCompensation(event))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
