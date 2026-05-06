package com.mini.bank.account.dto;

import com.mini.bank.account.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccountRequest {

    @NotBlank
    private String name;

    @NotNull
    private AccountType accountType;

    @NotNull
    private Integer branchId;
}
