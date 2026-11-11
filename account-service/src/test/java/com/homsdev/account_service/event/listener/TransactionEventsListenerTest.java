package com.homsdev.account_service.event.listener;

import com.homsdev.account_service.config.RabbitMQConfig;
import com.homsdev.account_service.dto.UpdateBalanceRequest;
import com.homsdev.account_service.event.dto.TransactionCompensationEvent;
import com.homsdev.account_service.event.dto.TransactionCreatedEvent;
import com.homsdev.account_service.exception.ResourceNotFoundException;
import com.homsdev.account_service.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Transaction Events Listener Test")
class TransactionEventsListenerTest {

    @Mock
    private AccountService accountService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private TransactionEventsListener transactionEventsListener;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Should update the account balance when a transaction event is received")
    void shouldUpdateAccountBalanceWhenTransactionEventIsReceived() {
        TransactionCreatedEvent event =
                new TransactionCreatedEvent("transaction-123", "account-123",BigDecimal.TEN);

        when(accountService.updateAccountBalance(anyString(), any(UpdateBalanceRequest.class)))
                .thenReturn(BigDecimal.valueOf(100));

        transactionEventsListener.handleTransactionCreatedEvent(event);

        ArgumentCaptor<UpdateBalanceRequest> requestCaptor =
                ArgumentCaptor.forClass(UpdateBalanceRequest.class);

        verify(accountService).updateAccountBalance(eq(event.accountId()), requestCaptor.capture());
        assertEquals(event.amount(), requestCaptor.getValue().getAmount());
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Object.class));
    }

    @Test
    @DisplayName("Should publish a compensation event when the account does not exist")
    void shouldPublishCompensationEventWhenAccountDoesNotExist() {
        TransactionCreatedEvent event =
                new TransactionCreatedEvent("transaction-123", "account-123", BigDecimal.TEN);
        doThrow(new ResourceNotFoundException("Account", event.accountId()))
                .when(accountService)
                .updateAccountBalance(anyString(), any(UpdateBalanceRequest.class));

        transactionEventsListener.handleTransactionCreatedEvent(event);

        ArgumentCaptor<TransactionCompensationEvent> eventCaptor =
                ArgumentCaptor.forClass(TransactionCompensationEvent.class);

        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.COMPENSATION_EXCHANGE),
                eq(RabbitMQConfig.ROUTING_KEY_TRANSACTION_COMPENSATE),
                (Object) eventCaptor.capture()
        );
        assertEquals(
                new TransactionCompensationEvent(
                        event.transactionId(),
                        event.accountId(),
                        event.amount(),
                        "ACCOUNT_NOT_FOUND"
                ),
                eventCaptor.getValue()
        );
    }

    @Test
    @DisplayName("Should rethrow unexpected processing errors")
    void shouldRethrowUnexpectedProcessingErrors() {
        TransactionCreatedEvent event =
                new TransactionCreatedEvent("transaction-123", "account-123", BigDecimal.TEN);
        IllegalStateException cause = new IllegalStateException("Service unavailable");
        when(accountService.updateAccountBalance(anyString(), any(UpdateBalanceRequest.class)))
                .thenThrow(cause);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> transactionEventsListener.handleTransactionCreatedEvent(event)
        );

        assertEquals("Failed to process transaction event", exception.getMessage());
        assertSame(cause, exception.getCause());
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Object.class));
    }

    @Test
    @DisplayName("Should consume dead-letter messages without throwing an error")
    void shouldConsumeDeadLetterMessages() {
        Message message = org.mockito.Mockito.mock(Message.class);
        MessageProperties messageProperties = org.mockito.Mockito.mock(MessageProperties.class);
        when(message.getBody()).thenReturn("failed message".getBytes(StandardCharsets.UTF_8));
        when(message.getMessageProperties()).thenReturn(messageProperties);
        when(messageProperties.getHeaders()).thenReturn(Collections.emptyMap());

        assertDoesNotThrow(() -> transactionEventsListener.handleCompensationEvent(message));

        verify(message).getBody();
        verify(message).getMessageProperties();
        verify(messageProperties).getHeaders();
    }
}