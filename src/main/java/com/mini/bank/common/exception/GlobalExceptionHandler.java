package com.mini.bank.common.exception;

import com.mini.bank.auth.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Self Defined Exceptions

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleUsernameExist(
            UsernameAlreadyExistsException ex,
            HttpServletRequest request) {

        ApiError api = new ApiError(
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.name(),
                request.getRequestURI()

        );
        return new ResponseEntity<>(api, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCreds(
            InvalidCredentialsException ex,
            HttpServletRequest request) {

        ApiError api = new ApiError(
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.name(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(api, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ApiError> handleAccountLock(
            AccountLockedException ex,
            HttpServletRequest request) {

        ApiError api = new ApiError(
                ex.getMessage(),
                HttpStatus.LOCKED.value(),
                HttpStatus.LOCKED.name(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(api, HttpStatus.LOCKED);
    }

    @ExceptionHandler(SelfActionNotAllowedException.class)
    public ResponseEntity<ApiError> handleSelfAction(
            SelfActionNotAllowedException ex,
            HttpServletRequest request) {

        ApiError api = new ApiError(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.name(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(api, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyEnabledException.class)
    public ResponseEntity<ApiError> handleUserAlreadyEnabled(
            UserAlreadyEnabledException ex,
            HttpServletRequest request) {
        ApiError api = new ApiError(
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.name(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(api, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserAlreadyDisabledException.class)
    public ResponseEntity<ApiError> handleUserAlreadyDisabled(
            UserAlreadyDisabledException ex,
            HttpServletRequest request) {
        ApiError api = new ApiError(
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.name(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(api, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserDisabledException.class)
    public ResponseEntity<ApiError> handleUserDisabled(
            UserDisabledException ex,
            HttpServletRequest request) {
        ApiError api = new ApiError(
                ex.getMessage(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.name(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(api, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleEmailExists(
            EmailAlreadyExistsException ex,
            HttpServletRequest request) {
        ApiError api = new ApiError(
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.name(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(api, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NoChangesDetectedException.class)
    public ResponseEntity<ApiError> handleNoChangesDetected(
            NoChangesDetectedException ex,
            HttpServletRequest request) {
        ApiError api = new ApiError(
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.name(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(api, HttpStatus.CONFLICT);
    }

    // Pre-defined Exceptions

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArguments(
            IllegalArgumentException ex,
            HttpServletRequest request) {
        ApiError api = new ApiError(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.name(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(api, HttpStatus.BAD_REQUEST);
    }

    // Validation Exceptions

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        ApiError api = new ApiError(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.name(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(api, HttpStatus.BAD_REQUEST);
    }

    // Security Exceptions

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            UsernameNotFoundException ex,
            HttpServletRequest request) {
        ApiError api = new ApiError(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.name(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(api, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {
        ApiError api = new ApiError(
                ex.getMessage(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.name(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(api, HttpStatus.FORBIDDEN);
    }

    // Generic Exception

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneralException(Exception ex, HttpServletRequest request) {
        ApiError api = new ApiError(
                "Something went wrong",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(api, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
