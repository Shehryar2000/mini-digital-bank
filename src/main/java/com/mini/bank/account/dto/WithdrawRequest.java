package com.mini.bank.account.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class WithdrawRequest{
    @NotNull
    private UUID accountId;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

}
