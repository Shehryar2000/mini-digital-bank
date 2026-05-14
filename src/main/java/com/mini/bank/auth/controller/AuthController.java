package com.mini.bank.auth.controller;

import com.mini.bank.auth.dto.*;
import com.mini.bank.auth.service.AuthService;
import com.mini.bank.common.util.IpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(
            summary = "Register new user",
            description = "Creates a new banking user account"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed")
    })
    @PostMapping("/register-user")
    public ResponseEntity<UserRegisterResponse> registerUser(@Valid @RequestBody UserRegisterRequest request, HttpServletRequest http) {
        String ip = IpUtil.getClientIp(http);
        UserRegisterResponse response = authService.register(request, ip);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }


    @Operation(
            summary = "Login user",
            description = "Authenticates user and return JWT token"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login-user")
    public ResponseEntity<UserLoginResponse> loginUser(@RequestBody UserLoginRequest request, HttpServletRequest http) {
        String ip = IpUtil.getClientIp(http);
        UserLoginResponse response = authService.login(request, ip);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
