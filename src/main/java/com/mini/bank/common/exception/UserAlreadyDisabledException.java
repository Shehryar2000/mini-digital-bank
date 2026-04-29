package com.mini.bank.common.exception;

public class UserAlreadyDisabledException extends RuntimeException {
    public UserAlreadyDisabledException(String message) {
        super(message);
    }
}
