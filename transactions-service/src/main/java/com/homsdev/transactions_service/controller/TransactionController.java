package com.homsdev.transactions_service.controller;

import com.homsdev.transactions_service.dto.CreateTransactionRequest;
import com.homsdev.transactions_service.dto.TransactionResponse;
import com.homsdev.transactions_service.exception.GlobalExceptionHandler;
import com.homsdev.transactions_service.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Create, read and delete income, expense and transfer transactions. "
		+ "Each transaction belongs to one existing category. Creating a transaction also publishes a "
		+ "transaction.created event to RabbitMQ. Base path: /api/v1/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a transaction",
            description = "Creates a transaction linked to an existing active category (categoryId). "
                    + "After saving, a transaction.created event with the transaction id, accountId and amount "
                    + "is published to RabbitMQ. Returns 404 when the category does not exist.")
    @ApiResponse(responseCode = "201", description = "Transaction created. The response contains the generated id.")
    @ApiResponse(responseCode = "404", description = "The categoryId does not match an existing category",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(type = "object"),
                    examples = @ExampleObject(value = GlobalExceptionHandler.CATEGORY_NOT_FOUND_ON_TRANSACTION_EXAMPLE)))
    public TransactionResponse createTransaction(@RequestBody CreateTransactionRequest request) {
        return transactionService.createTransaction(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "List all transactions",
            description = "Returns every stored transaction, each with its category. The list is empty when none exist.")
    @ApiResponse(responseCode = "200", description = "All transactions")
    public List<TransactionResponse> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a transaction by id",
            description = "Returns one transaction with its category. Returns 404 when no transaction has that id.")
    @ApiResponse(responseCode = "200", description = "The requested transaction")
    @ApiResponse(responseCode = "404", description = "No transaction has that id",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(type = "object"),
                    examples = @ExampleObject(value = GlobalExceptionHandler.TRANSACTION_NOT_FOUND_EXAMPLE)))
    public TransactionResponse getTransactionById(
            @Parameter(description = "Transaction id (UUID)", example = "7d4e2a10-3b8f-4f5e-9a6c-1e2d3f4a5b6c")
            @PathVariable String id) {
        return transactionService.getTransactionById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a transaction",
            description = "Permanently deletes the transaction. This service does not publish a reversal event "
                    + "for account balances.")
    @ApiResponse(responseCode = "204", description = "Transaction deleted. No response body.")
    @ApiResponse(responseCode = "404", description = "No transaction has that id",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(type = "object"),
                    examples = @ExampleObject(value = GlobalExceptionHandler.TRANSACTION_NOT_FOUND_EXAMPLE)))
    public void deleteTransactionById(
            @Parameter(description = "Transaction id (UUID)", example = "7d4e2a10-3b8f-4f5e-9a6c-1e2d3f4a5b6c")
            @PathVariable String id) {
        transactionService.deleteTransaction(id);
    }
}
