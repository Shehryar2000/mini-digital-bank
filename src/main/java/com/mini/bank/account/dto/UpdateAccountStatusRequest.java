package com.mini.bank.account.dto;

import com.mini.bank.account.enums.AccountStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAccountStatusRequest {

    @NotNull
    private AccountStatus status;
}
