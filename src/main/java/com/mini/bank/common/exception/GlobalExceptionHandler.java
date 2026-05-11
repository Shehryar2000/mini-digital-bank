package com.mini.bank.common.exception;

import com.mini.bank.audit.enums.AuditAction;
import com.mini.bank.audit.enums.AuditEntityType;
import com.mini.bank.audit.service.AuditService;
import com.mini.bank.auth.dto.ApiError;
import com.mini.bank.common.security.AuthContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;


@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final AuthContext authContext;
    private final AuditService auditService;

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

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ApiError> handleCustomerNotFound(
            CustomerNotFoundException ex,
            HttpServletRequest request) {
        ApiError api = new ApiError(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.name(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(api, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BranchNotFoundException.class)
    public ResponseEntity<ApiError> handleBranchNotFound(
            BranchNotFoundException ex,
            HttpServletRequest request) {
        ApiError api = new ApiError(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.name(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(api, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiError> handleAccountNotFound(
            AccountNotFoundException ex,
            HttpServletRequest request) {
        ApiError api = new ApiError(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.name(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(api, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccountNotActiveException.class)
    public ResponseEntity<ApiError> handleAccountNotActive(
            AccountNotActiveException ex,
            HttpServletRequest request) {
        ApiError api = new ApiError(
                ex.getMessage(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.name(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(api, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ApiError> handleInsufficientBalance(
            InsufficientBalanceException ex,
            HttpServletRequest request) {
        ApiError api = new ApiError(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.name(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(api, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorized(
            UnauthorizedException ex,
            HttpServletRequest request) {
        ApiError api = new ApiError(
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.name(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(api, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IncompleteAccountNumberException.class)
    public ResponseEntity<ApiError> handleAccountNotActive(
            IncompleteAccountNumberException ex,
            HttpServletRequest request) {
        ApiError api = new ApiError(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.name(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(api, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransferNotFoundException.class)
    public ResponseEntity<ApiError> handleTransferNotActive(
            TransferNotFoundException ex,
            HttpServletRequest request) {
        ApiError api = new ApiError(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.name(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(api, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccountOperationRestrictedException.class)
    public ResponseEntity<ApiError> handleOperationRestricted(
            AccountOperationRestrictedException ex,
            HttpServletRequest request) {
        ApiError api = new ApiError(
                ex.getMessage(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.name(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(api, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidAccountStatusTransitionException.class)
    public ResponseEntity<ApiError> handleOperationRestricted(
            InvalidAccountStatusTransitionException ex,
            HttpServletRequest request) {
        ApiError api = new ApiError(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.name(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(api, HttpStatus.BAD_REQUEST);
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

        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + " " + err.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        log.error("Validation failed | user={} | error={}", authContext.getUserId(), errorMessage);

        auditService.failure(
                authContext.getUserId(),
                AuditAction.ACCOUNT_CREATED,
                request.getRemoteAddr(),
                AuditEntityType.ACCOUNT,
                null,
                Map.of("error", errorMessage)
        );

        ApiError api = new ApiError(
                errorMessage,
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
