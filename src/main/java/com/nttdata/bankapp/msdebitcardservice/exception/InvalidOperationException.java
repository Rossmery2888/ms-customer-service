package com.nttdata.bankapp.msdebitcardservice.exception;

/**
 * Excepción para operaciones inválidas.
 */
public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException(String message) {
        super(message);
    }
}