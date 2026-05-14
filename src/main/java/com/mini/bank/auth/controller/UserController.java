package com.mini.bank.auth.controller;

import com.mini.bank.auth.dto.ChangePasswordRequest;
import com.mini.bank.auth.dto.ChangePasswordResponse;
import com.mini.bank.auth.dto.UserResponse;
import com.mini.bank.auth.service.AuthService;
import com.mini.bank.auth.service.UserService;
import com.mini.bank.common.util.IpUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Get Current User",
            description = "Returns currently logged-in user details"
    )
    @GetMapping("/myself")
    public ResponseEntity<UserResponse> getCurrentUser(HttpServletRequest http) {
        String ip = IpUtil.getClientIp(http);
        UserResponse userResponse = userService.getCurrentUser(ip);
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

    @Operation(
            summary = "Change Password",
            description = "Allows user to change password securely"
    )
    @PutMapping("/change-password")
    public ResponseEntity<ChangePasswordResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request, HttpServletRequest http) {
        String ip = IpUtil.getClientIp(http);
        ChangePasswordResponse response = userService.changePassword(request, ip);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
