package com.nttdata.bankapp.msdebitcardservice.exception;

/**
 * Excepción para cuando no se encuentra una tarjeta de débito.
 */
public class DebitCardNotFoundException extends RuntimeException {
    public DebitCardNotFoundException(String message) {
        super(message);
    }
}