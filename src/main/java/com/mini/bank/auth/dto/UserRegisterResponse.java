package com.mini.bank.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class UserRegisterResponse {

    private UUID userId;
    private UUID customerId;
    private Long customerNumber;
    private String username;
    private String name;
    private String email;

}
