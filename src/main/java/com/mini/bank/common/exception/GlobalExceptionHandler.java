package com.mini.bank.common.exception;

import com.mini.bank.auth.dto.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleUsernameExist(UsernameAlreadyExistsException ex) {
        ApiError api = new ApiError(ex.getMessage(), HttpStatus.CONFLICT.value());
        return new ResponseEntity<>(api, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCreds(InvalidCredentialsException ex) {
        ApiError api = new ApiError(ex.getMessage(), HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(api, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ApiError> handleAccountLock(AccountLockedException ex) {
        ApiError api = new ApiError(ex.getMessage(), HttpStatus.LOCKED.value());
        return new ResponseEntity<>(api, HttpStatus.LOCKED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneralException(Exception ex) {
        ApiError api = new ApiError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(api, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
