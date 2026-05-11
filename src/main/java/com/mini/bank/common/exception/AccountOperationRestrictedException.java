package com.mini.bank.common.exception;

public class AccountOperationRestrictedException extends RuntimeException {
    public AccountOperationRestrictedException(String message) {
        super(message);
    }
}
