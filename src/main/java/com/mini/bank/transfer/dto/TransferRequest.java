package com.mini.bank.transfer.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class TransferRequest {

    @NotNull
    private UUID fromAccount;

    @NotNull
    private UUID toAccount;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;
}
