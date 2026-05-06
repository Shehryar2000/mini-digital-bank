package com.mini.bank.transfer.service;

import com.mini.bank.ledger.service.LedgerEntryService;
import com.mini.bank.transfer.dto.TransferRequest;
import com.mini.bank.account.entity.Account;
import com.mini.bank.transfer.entity.Transfer;
import com.mini.bank.ledger.enums.LedgerType;
import com.mini.bank.transfer.enums.TransferStatus;
import com.mini.bank.account.repository.AccountRepository;
import com.mini.bank.transfer.repository.TransferRepository;
import com.mini.bank.account.validator.AccountValidator;
import com.mini.bank.audit.enums.AuditAction;
import com.mini.bank.audit.enums.AuditEntityType;
import com.mini.bank.audit.service.AuditService;
import com.mini.bank.common.exception.AccountNotActiveException;
import com.mini.bank.common.exception.AccountNotFoundException;
import com.mini.bank.common.exception.InsufficientBalanceException;
import com.mini.bank.common.exception.UnauthorizedException;
import com.mini.bank.common.security.AuthContext;
import com.mini.bank.common.util.ReferenceIdGenerator;
import com.mini.bank.corebanking.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransferService {

    private static final Logger log = LoggerFactory.getLogger(TransferService.class);

    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;
    private final AccountValidator accountValidator;
    private final LedgerEntryService ledgerEntryService;
    private final AuditService auditService;
    private final AuthContext authContext;
    private final ReferenceIdGenerator refIdGenerator;


    @Transactional
    public ApiResponse transfer(TransferRequest request, String ip) {

        String refId = refIdGenerator.generate("TXN");

        try {

            log.info("Transfer request initiated | from={} | to={} | amount={} | ref={} ",
                    request.getFromAccount().toString(),
                    request.getToAccount().toString(),
                    request.getAmount().toString(),
                    refId);

            if (request.getFromAccount().equals(request.getToAccount())) {
                throw new IllegalArgumentException("Cannot transfer to the same account");
            }

            accountValidator.validateAmount(request.getFromAccount(), request.getAmount(), ip, AuditAction.TRANSFER);

            // Locking order of account
            Account fromAccount;
            Account toAccount;

            // Setting sequence of account locking in order to avoid race condition

            if (request.getFromAccount().compareTo(request.getToAccount()) < 0) {
                fromAccount = accountValidator.getAccountById(request.getFromAccount(), ip, AuditAction.TRANSFER);
                toAccount = accountValidator.getAccountById(request.getToAccount(), ip, AuditAction.TRANSFER);
            } else {
                toAccount = accountValidator.getAccountById(request.getToAccount(), ip, AuditAction.TRANSFER);
                fromAccount = accountValidator.getAccountById(request.getFromAccount(), ip, AuditAction.TRANSFER);
            }

            accountValidator.validateOwnership(fromAccount, ip, AuditAction.TRANSFER);
            accountValidator.validateAccountActive(fromAccount, ip, AuditAction.TRANSFER);
            accountValidator.validateAccountActive(toAccount, ip, AuditAction.TRANSFER);
            accountValidator.validateBalance(fromAccount, request.getAmount(), ip, AuditAction.TRANSFER);

            fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
            toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));

            accountRepository.save(fromAccount);
            accountRepository.save(toAccount);

            // Transfer record
            Transfer txn = new Transfer();
            txn.setFromAccount(fromAccount);
            txn.setToAccount(toAccount);
            txn.setAmount(request.getAmount());
            txn.setStatus(TransferStatus.SUCCESS);
            txn.setReferenceId(refId);

            transferRepository.save(txn);

            ledgerEntryService.createLedgerEntry(fromAccount, request.getAmount(), LedgerType.DEBIT, refId, ip);
            ledgerEntryService.createLedgerEntry(toAccount, request.getAmount(), LedgerType.CREDIT, refId, ip);

            log.info("Transfer success | ref={} | from={} | to={} | amount={}",
                    refId,
                    fromAccount.getId().toString(),
                    toAccount.getId().toString(),
                    request.getAmount().toString()
            );

            auditService.success(
                    authContext.getUserId(),
                    AuditAction.TRANSFER,
                    ip,
                    AuditEntityType.ACCOUNT,
                    fromAccount.getId(),
                    Map.of(
                            "fromAccount", fromAccount.getId().toString(),
                            "toAccount", toAccount.getId().toString(),
                            "amount", request.getAmount().toString(),
                            "referenceId", refId
                    )
            );

            return ApiResponse.builder()
                    .message("Transfer successful. Ref: " + refId)
                    .build();
        } catch (IllegalArgumentException | AccountNotFoundException | UnauthorizedException |
                 AccountNotActiveException | InsufficientBalanceException e) {
            throw e;
        } catch (Exception e) {

            log.error("Transfer failed | refId={} | error={}", refId, e.getMessage(), e);

            auditService.failure(
                    authContext.getUserId(),
                    AuditAction.TRANSFER,
                    ip,
                    AuditEntityType.ACCOUNT,
                    null,
                    Map.of(
                            "referenceId", refId,
                            "error", e.getMessage()
                    )
            );

            throw e;
        }
    }

}
