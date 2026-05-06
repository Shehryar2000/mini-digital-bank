package com.mini.bank.common.exception;

public class IncompleteAccountNumberException extends RuntimeException {
    public IncompleteAccountNumberException(String message) {
        super(message);
    }
}
