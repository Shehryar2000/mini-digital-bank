package com.mini.bank.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class UserResponse {
    private UUID userId;
    private String username;
    private String role;
    private boolean enabled;
}
