package com.homsdev.transactions_service.mapper;

import com.homsdev.transactions_service.dto.CategoryResponse;
import com.homsdev.transactions_service.dto.CreateTransactionRequest;
import com.homsdev.transactions_service.dto.TransactionResponse;
import com.homsdev.transactions_service.enums.TransactionType;
import com.homsdev.transactions_service.model.Category;
import com.homsdev.transactions_service.model.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TransactionMapperTest {

    @Spy
    private CategoryMapper categoryMapper = new CategoryMapperImpl();

    @InjectMocks
    private TransactionMapperImpl transactionMapper;

    @Test
    void toTransactionCopiesFieldsAndIgnoresCategory() {
        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setType(TransactionType.EXPENSE);
        request.setDescription("Weekly groceries");
        request.setAmount(new BigDecimal("25.50"));
        request.setCurrency("USD");
        request.setAccountId("acc-1");
        request.setCategoryId("cat-1");

        Transaction transaction = transactionMapper.toTransaction(request);

        assertThat(transaction.getType()).isEqualTo(TransactionType.EXPENSE);
        assertThat(transaction.getDescription()).isEqualTo("Weekly groceries");
        assertThat(transaction.getAmount()).isEqualByComparingTo("25.50");
        assertThat(transaction.getCurrency()).isEqualTo("USD");
        assertThat(transaction.getAccountId()).isEqualTo("acc-1");
        assertThat(transaction.getCategory()).isNull();
    }

    @Test
    void toTransactionResponseMapsAmountAndCategory() {
        Category category = new Category();
        category.setId("cat-1");
        category.setName("Groceries");

        Transaction transaction = new Transaction();
        transaction.setId("tx-1");
        transaction.setType(TransactionType.EXPENSE);
        transaction.setAmount(new BigDecimal("25.50"));
        transaction.setCategory(category);

        TransactionResponse response = transactionMapper.toTransactionResponse(transaction);

        assertThat(response.getId()).isEqualTo("tx-1");
        assertThat(response.getAmount()).isEqualByComparingTo("25.50");
        assertThat(response.getCategory()).isEqualTo(new CategoryResponse("cat-1", "Groceries"));
    }

    @Test
    void toTransactionReturnsNullForNullRequest() {
        assertThat(transactionMapper.toTransaction(null)).isNull();
    }

    @Test
    void toTransactionResponseReturnsNullForNullTransaction() {
        assertThat(transactionMapper.toTransactionResponse(null)).isNull();
    }

    @Test
    void toTransactionResponseLeavesAmountNullWhenTransactionAmountIsNull() {
        Transaction transaction = new Transaction();
        transaction.setId("tx-1");

        TransactionResponse response = transactionMapper.toTransactionResponse(transaction);

        assertThat(response.getAmount()).isNull();
        assertThat(response.getCategory()).isNull();
    }
}
