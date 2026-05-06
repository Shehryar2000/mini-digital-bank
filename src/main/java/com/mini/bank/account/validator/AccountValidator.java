package com.mini.bank.account.validator;

import com.mini.bank.account.entity.Account;
import com.mini.bank.account.enums.AccountStatus;
import com.mini.bank.account.repository.AccountRepository;
import com.mini.bank.audit.enums.AuditAction;
import com.mini.bank.audit.enums.AuditEntityType;
import com.mini.bank.audit.service.AuditService;
import com.mini.bank.common.exception.*;
import com.mini.bank.common.security.AuthContext;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountValidator {

    private static final Logger log = LoggerFactory.getLogger(AccountValidator.class);

    private final AccountRepository accountRepository;
    private final AuditService auditService;
    private final AuthContext authContext;

    public Account getAccountById(UUID accountId, String ip, AuditAction action) {

        Account account;

        if (action == AuditAction.TRANSFER) {
            log.info("Getting account by id {} | Transfer request", accountId);
            account = accountRepository.findByIdForUpdate(accountId).orElse(null);
        } else {
            account = accountRepository.findById(accountId).orElse(null);
        }

        if (account == null) {

            log.warn("Account not found | accountId={}", accountId);

            auditService.failure(
                    authContext.getUserId(),
                    action,
                    ip,
                    AuditEntityType.ACCOUNT,
                    null,
                    Map.of(
                            "accountId", accountId.toString(),
                            "reason", "account not found"
                    )
            );

            throw new AccountNotFoundException("account not found");

        }

        return account;
    }

    public Account getAccountByNumber(String accountNumber, String ip, AuditAction action) {

        accountNumber = accountNumber.trim();

        if (accountNumber.length() != 14) {

            log.warn("Incomplete account number | accountNumber={}", accountNumber);

            auditService.failure(
                    authContext.getUserId(),
                    action,
                    ip,
                    AuditEntityType.ACCOUNT,
                    null,
                    Map.of(
                            "accountNumber", accountNumber,
                            "reason", "incomplete account number"
                    )
            );

            throw new IncompleteAccountNumberException("Account number should be of 14 digits long");

        }

        Long acctNum = Long.valueOf(accountNumber.substring(4));

        Account account = accountRepository.findByAccountNumber(acctNum).orElse(null);

        if (account == null) {

            log.warn("Account not found | accountNumber={}", accountNumber);

            auditService.failure(
                    authContext.getUserId(),
                    action,
                    ip,
                    AuditEntityType.ACCOUNT,
                    null,
                    Map.of(
                            "accountNumber", accountNumber,
                            "reason", "account not found"
                    )
            );

            throw new AccountNotFoundException("account not found");

        }

        return account;
    }

    public void validateAccountActive(Account account, String ip, AuditAction action) {

        if (!account.getStatus().equals(AccountStatus.ACTIVE)) {

            log.warn("Account inactive | accountId={}", account.getId());

            auditService.failure(
                    authContext.getUserId(),
                    action,
                    ip,
                    AuditEntityType.ACCOUNT,
                    null,
                    Map.of(
                            "accountId", account.getId().toString(),
                            "reason", "account not active"
                    )
            );
            throw new AccountNotActiveException("Account not active");
        }
    }

    public void validateOwnership(Account account, String ip, AuditAction action) {

        if (!account.getCustomer().getId().equals(authContext.getCustomerId())) {

            log.warn("Invalid ownership of account | accountId={} | customerId={}", account.getId(), authContext.getCustomerId());

            auditService.failure(
                    authContext.getUserId(),
                    action,
                    ip,
                    AuditEntityType.ACCOUNT,
                    null,
                    Map.of(
                            "customerId", authContext.getCustomerId(),
                            "accountId", account.getId().toString(),
                            "reason", "account not belongs to the customer"
                    )
            );

            throw new UnauthorizedException("Access denied");
        }
    }

    public void validateAmount(UUID accountId, BigDecimal amount, String ip, AuditAction action) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {

            log.warn("Invalid amount | accountId={} | amount={}", accountId, amount);

            auditService.failure(
                    authContext.getUserId(),
                    action,
                    ip,
                    AuditEntityType.ACCOUNT,
                    null,
                    Map.of(
                            "accountId", accountId.toString(),
                            "amount", amount,
                            "reason", "amount must be greater than zero"
                    )
            );

            throw new IllegalArgumentException("amount must be greater than zero");
        }
    }

    public void validateBalance(Account account, BigDecimal amount, String ip, AuditAction action) {

        if (account.getBalance().compareTo(amount) < 0) {

            log.warn("Insufficient balance in account | accountId={}", account.getId());

            auditService.failure(
                    authContext.getUserId(),
                    action,
                    ip,
                    AuditEntityType.ACCOUNT,
                    null,
                    Map.of(
                            "accountId", account.getId(),
                            "accountNumber", account.getAccountNumber(),
                            "reason", "insufficient balance"
                    )
            );

            throw new InsufficientBalanceException("insufficient balance");

        }
    }
}
