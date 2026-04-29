package com.mini.bank.common.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthContext {

    public UUID getUserId() {
        Claims claims = getClaims();
        return UUID.fromString(claims.get("userId").toString());
    }

    public UUID getCustomerId() {
        Claims claims = getClaims();
        return UUID.fromString(claims.get("customerId").toString());
    }

    public String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    private Claims getClaims() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getDetails() instanceof Claims)) {
            throw new RuntimeException("Invalid authentication context");
        }

        return (Claims) authentication.getDetails();
    }
}
