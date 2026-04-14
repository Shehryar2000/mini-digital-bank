package com.mini.bank.auth.controller;

import com.mini.bank.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
public class AdminController {

    private final AuthService authService;

    public AdminController(AuthService authService) {
        this.authService = authService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{userId}/enable")
    public ResponseEntity<?> enableUser(@PathVariable UUID userId) {
        authService.enableUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body("User enabled successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{userId}/disable")
    public ResponseEntity<?> disableUser(@PathVariable UUID userId) {
        authService.disableUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body("User disabled successfully");
    }

}
