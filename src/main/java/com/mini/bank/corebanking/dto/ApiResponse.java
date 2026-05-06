package com.mini.bank.corebanking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ApiResponse {
    public String message;
}
