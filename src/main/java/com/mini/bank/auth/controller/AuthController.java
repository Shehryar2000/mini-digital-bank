package com.mini.bank.auth.controller;

import com.mini.bank.auth.dto.UserLoginRequest;
import com.mini.bank.auth.dto.UserRegisterRequest;
import com.mini.bank.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register-user")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserRegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login-user")
    public ResponseEntity<String> loginUser(@RequestBody UserLoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(token);
    }
}
