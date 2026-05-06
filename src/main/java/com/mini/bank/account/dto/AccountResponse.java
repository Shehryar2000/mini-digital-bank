package com.mini.bank.account.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AccountResponse {

    private String accountNumber;
    private String name;
    private String type;
    private String status;
    private String branch;
}
