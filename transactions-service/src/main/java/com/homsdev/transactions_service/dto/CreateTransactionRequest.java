package com.homsdev.transactions_service.dto;

import com.homsdev.transactions_service.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data needed to create a new transaction")
public class CreateTransactionRequest {
    @Schema(description = "Kind of transaction: INCOME, EXPENSE or TRANSFER", requiredMode = Schema.RequiredMode.REQUIRED)
    private TransactionType type;
    @Schema(description = "Free text describing the transaction", example = "Weekly groceries")
    private String description;
    @Schema(description = "Positive amount of the transaction", example = "25.50", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;
    @Schema(description = "ISO 4217 currency code", example = "USD")
    private String currency;
    @Schema(description = "Id of the account that owns this transaction", example = "acc-1001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String accountId;
    @Schema(description = "Id of an existing, active category", example = "c0a80121-7b5e-4c3a-9d1f-2a4b6c8d9e0f", requiredMode = Schema.RequiredMode.REQUIRED)
    private String categoryId;
}
