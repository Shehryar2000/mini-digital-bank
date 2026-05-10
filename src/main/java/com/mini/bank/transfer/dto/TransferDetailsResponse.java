package com.mini.bank.transfer.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class TransferDetailsResponse {
    private String referenceId;
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private String status;
    private String createdAt;
}
