package com.mini.bank.ledger.service;

import com.mini.bank.account.entity.Account;
import com.mini.bank.account.validator.AccountValidator;
import com.mini.bank.audit.enums.AuditAction;
import com.mini.bank.audit.enums.AuditEntityType;
import com.mini.bank.common.exception.AccountNotActiveException;
import com.mini.bank.common.exception.AccountNotFoundException;
import com.mini.bank.common.exception.UnauthorizedException;
import com.mini.bank.ledger.dto.LedgerResponse;
import com.mini.bank.ledger.entity.LedgerEntry;
import com.mini.bank.ledger.enums.LedgerType;
import com.mini.bank.ledger.repository.LedgerRepository;
import com.mini.bank.audit.service.AuditService;
import com.mini.bank.common.security.AuthContext;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LedgerEntryService {

    private static final Logger log = LoggerFactory.getLogger(LedgerEntryService.class);
    private final LedgerRepository ledgerRepository;
    private final AuditService auditService;
    private final AuthContext authContext;
    private final AccountValidator accountValidator;

    @Transactional
    public void createLedgerEntry(Account account, BigDecimal amount, LedgerType ledgerType, String refId, String ip) {

        try {
            LedgerEntry ledgerEntry = new LedgerEntry();
            ledgerEntry.setAccount(account);
            ledgerEntry.setAmount(amount);
            ledgerEntry.setLedgerType(ledgerType);
            ledgerEntry.setReferenceId(refId);
            ledgerRepository.save(ledgerEntry);

            log.info("Ledger entry created | account={} | amount={} | type={} | refId={}",
                    account.getAccountNumber(),
                    amount,
                    ledgerType,
                    refId
            );


        } catch (Exception e) {

            log.error("Ledger entry failed | account={} | error={}",
                    account.getAccountNumber(),
                    e.getMessage(),
                    e);

            throw e;
        }

    }

    public List<LedgerResponse> getHistory(UUID accountId, String ip) {

        try {

            Account account = accountValidator.getAccountById(accountId, ip, AuditAction.LEDGER_FETCH);
            accountValidator.validateOwnership(account, ip, AuditAction.LEDGER_FETCH);
            accountValidator.validateAccountActive(account, ip, AuditAction.LEDGER_FETCH);

            List<LedgerEntry> entries = ledgerRepository.findByAccountIdOrderByCreatedAtDesc(accountId);

            List<LedgerResponse> responses = new ArrayList<>();

            for (LedgerEntry entry : entries) {

                responses.add(

                        LedgerResponse.builder()
                                .type(entry.getLedgerType().toString())
                                .amount(entry.getAmount())
                                .referenceId(entry.getReferenceId())
                                .createdAt(entry.getCreatedAt())
                                .build()
                );

            }

            log.info("Ledger history fetched | accountId={}, entries={}, userId={}",
                    accountId,
                    entries.size(),
                    authContext.getUserId()
            );

            // Audit Maintaining - Ledger Fetched Success
            auditService.success(
                    authContext.getUserId(),
                    AuditAction.LEDGER_FETCH,
                    ip,
                    AuditEntityType.LEDGER,
                    accountId,
                    Map.of(
                            "accountId", accountId,
                            "totalEntries", entries.size(),
                            "customerId", authContext.getCustomerId()
                    )
            );

            return responses;

        } catch (AccountNotFoundException | UnauthorizedException | AccountNotActiveException e) {
            throw e;
        } catch (Exception e) {

            log.error("Ledger entry failed | accountId={} | error={}",
                    accountId.toString(),
                    e.getMessage(),
                    e);

            auditService.failure(
                    authContext.getUserId(),
                    AuditAction.LEDGER_FETCH,
                    ip,
                    AuditEntityType.LEDGER,
                    null,
                    errorMeta(e)
            );

            throw e;
        }
    }

    private Map<String, Object> errorMeta(Exception e) {
        return Map.of(
                "error", e.getClass().getSimpleName(),
                "message", e.getMessage()
        );
    }
}
