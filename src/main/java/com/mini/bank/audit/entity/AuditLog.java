package com.mini.bank.audit.entity;

import com.mini.bank.audit.enums.AuditAction;
import com.mini.bank.audit.enums.AuditEntityType;
import com.mini.bank.audit.enums.AuditStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
public class AuditLog {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 100)
    private AuditAction action;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private AuditStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type")
    private AuditEntityType entityType;

    @Column(name = "entity_id")
    private UUID entityId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String,Object> metadata;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


}
