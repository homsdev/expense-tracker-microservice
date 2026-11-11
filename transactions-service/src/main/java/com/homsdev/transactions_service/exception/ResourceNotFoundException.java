package com.homsdev.transactions_service.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, String id) {
        super(String.format("%s with id %s not found", resource, id));
    }
}
