package com.nttdata.bankapp.msdebitcardservice.exception;

/**
 * Excepción para operaciones con fondos insuficientes.
 */
public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}