package com.homsdev.transactions_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public static final String CATEGORY_NOT_FOUND_EXAMPLE = "{\"title\":\"Resource not found\",\"status\":404,"
            + "\"detail\":\"Category with id c0a80121-7b5e-4c3a-9d1f-2a4b6c8d9e0f not found\","
            + "\"instance\":\"/api/v1/category/c0a80121-7b5e-4c3a-9d1f-2a4b6c8d9e0f\"}";

    public static final String TRANSACTION_NOT_FOUND_EXAMPLE = "{\"title\":\"Resource not found\",\"status\":404,"
            + "\"detail\":\"Transaction with id 7d4e2a10-3b8f-4f5e-9a6c-1e2d3f4a5b6c not found\","
            + "\"instance\":\"/api/v1/transaction/7d4e2a10-3b8f-4f5e-9a6c-1e2d3f4a5b6c\"}";

    public static final String CATEGORY_NOT_FOUND_ON_TRANSACTION_EXAMPLE = "{\"title\":\"Resource not found\",\"status\":404,"
            + "\"detail\":\"Category with id c0a80121-7b5e-4c3a-9d1f-2a4b6c8d9e0f not found\","
            + "\"instance\":\"/api/v1/transaction\"}";

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFound(ResourceNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Resource not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }
}
