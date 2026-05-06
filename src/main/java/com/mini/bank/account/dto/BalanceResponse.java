package com.mini.bank.account.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class BalanceResponse {
    private BigDecimal balance;
}
