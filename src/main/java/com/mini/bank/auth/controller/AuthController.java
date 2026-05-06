package com.mini.bank.auth.controller;

import com.mini.bank.auth.dto.*;
import com.mini.bank.auth.service.AuthService;
import com.mini.bank.common.util.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register-user")
    public ResponseEntity<UserRegisterResponse> registerUser(@Valid @RequestBody UserRegisterRequest request, HttpServletRequest http) {
        String ip = IpUtil.getClientIp(http);
        UserRegisterResponse response = authService.register(request, ip);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @PostMapping("/login-user")
    public ResponseEntity<UserLoginResponse> loginUser(@RequestBody UserLoginRequest request, HttpServletRequest http) {
        String ip = IpUtil.getClientIp(http);
        UserLoginResponse response = authService.login(request, ip);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
