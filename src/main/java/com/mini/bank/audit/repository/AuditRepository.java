package com.mini.bank.audit.repository;

import com.mini.bank.audit.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditRepository extends JpaRepository<AuditLog, UUID> {

}
