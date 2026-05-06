package com.mini.bank.transfer.entity;

import com.mini.bank.account.entity.Account;
import com.mini.bank.transfer.enums.TransferStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transfers")
@Getter
@Setter
public class Transfer {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @JoinColumn(name = "from_account_id", nullable = false)
    @ManyToOne
    private Account fromAccount;

    @JoinColumn(name = "to_account_id", nullable = false)
    @ManyToOne
    private Account toAccount;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransferStatus status;

    @Column(name = "reference_id", nullable = false)
    private String referenceId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

}
