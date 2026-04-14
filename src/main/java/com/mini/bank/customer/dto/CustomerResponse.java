package com.mini.bank.customer.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class CustomerResponse {

    private UUID customerId;
    private String name;
    private String email;
    private Long customerNumber;
}
