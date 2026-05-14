package com.mini.bank.audit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mini.bank.audit.entity.AuditLog;
import com.mini.bank.audit.enums.AuditAction;
import com.mini.bank.audit.enums.AuditEntityType;
import com.mini.bank.audit.enums.AuditStatus;
import com.mini.bank.audit.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);
    private final AuditRepository auditRepository;

    public void log(
            UUID userId,
            AuditAction action,
            String ipAddress,
            AuditStatus status,
            AuditEntityType entityType,
            UUID entityId,
            Map<String, Object> metadata) {

        try {

            AuditLog auditLog = new AuditLog();
            auditLog.setUserId(userId);
            auditLog.setAction(action);
            auditLog.setIpAddress(ipAddress);
            auditLog.setStatus(status);
            auditLog.setEntityType(entityType);
            auditLog.setEntityId(entityId);

            if (metadata != null) {

                try{
                    auditLog.setMetadata(metadata);
                }catch (Exception e){
                    auditLog.setMetadata(
                            Map.of("error", "metadata serialization failed")
                    );
                }
            }

            auditRepository.save(auditLog);

        } catch (RuntimeException e) {
            log.error("Audit logging failed", e);
        }
    }

    public void success(UUID userId, AuditAction action, String ip,
                        AuditEntityType type, UUID entityId, Map<String, Object> metadata) {
        log(userId, action, ip, AuditStatus.SUCCESS, type, entityId, metadata);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void failure(UUID userId, AuditAction action, String ip,
                        AuditEntityType type, UUID entityId, Map<String, Object> metadata) {
        log(userId, action, ip, AuditStatus.FAILURE, type, entityId, metadata);
    }
}
