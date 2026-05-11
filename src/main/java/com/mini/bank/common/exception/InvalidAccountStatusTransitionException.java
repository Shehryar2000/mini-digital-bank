package com.mini.bank.common.exception;

public class InvalidAccountStatusTransitionException extends RuntimeException {
    public InvalidAccountStatusTransitionException(String message) {
        super(message);
    }
}
