package com.mini.bank.common.exception;

public class UserAlreadyEnabledException extends RuntimeException {
  public UserAlreadyEnabledException(String message) {
    super(message);
  }
}
