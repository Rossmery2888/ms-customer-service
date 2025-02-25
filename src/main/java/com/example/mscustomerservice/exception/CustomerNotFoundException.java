package com.example.mscustomerservice.exception;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String id) {
        super("Customer not found with id: " + id);
    }
}
