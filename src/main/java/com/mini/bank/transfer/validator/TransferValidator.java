package com.mini.bank.transfer.validator;

import com.mini.bank.audit.enums.AuditAction;
import com.mini.bank.audit.enums.AuditEntityType;
import com.mini.bank.audit.service.AuditService;
import com.mini.bank.common.exception.AccountNotFoundException;
import com.mini.bank.common.exception.TransferNotFoundException;
import com.mini.bank.common.security.AuthContext;
import com.mini.bank.transfer.entity.Transfer;
import com.mini.bank.transfer.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class TransferValidator {

    private static final Logger log = LoggerFactory.getLogger(TransferValidator.class);
    private final TransferRepository transferRepository;
    private final AuditService auditService;
    private final AuthContext authContext;


    public Transfer getTransferByReferenceId(String referenceId, String ip, AuditAction action) {

        log.info("Fetching transfer details | referenceId={}", referenceId);

        Transfer transfer = transferRepository.findByReferenceId(referenceId).orElse(null);

        if (transfer == null) {

            log.warn("Transfer not found | referenceId={}", referenceId);

            auditService.failure(
                    authContext.getUserId(),
                    action,
                    ip,
                    AuditEntityType.TRANSFER,
                    null,
                    Map.of(
                            "referenceId", referenceId,
                            "reason", "transaction not found"
                    )
            );

            throw new TransferNotFoundException("transfer not found");

        }

        return transfer;

    }
}
