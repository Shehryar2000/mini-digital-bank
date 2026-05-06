package com.mini.bank.ledger.entity;

import com.mini.bank.account.entity.Account;
import com.mini.bank.ledger.enums.LedgerType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ledger_entries")
@Getter
@Setter
public class LedgerEntry {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private LedgerType ledgerType;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "reference_id")
    private String referenceId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

}
