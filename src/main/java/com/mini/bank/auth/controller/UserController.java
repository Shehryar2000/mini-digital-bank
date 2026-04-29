package com.mini.bank.auth.controller;

import com.mini.bank.auth.dto.ChangePasswordRequest;
import com.mini.bank.auth.dto.ChangePasswordResponse;
import com.mini.bank.auth.dto.UserResponse;
import com.mini.bank.auth.service.AuthService;
import com.mini.bank.auth.service.UserService;
import com.mini.bank.common.util.IpUtil;
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

    @GetMapping("/myself")
    public ResponseEntity<UserResponse> getCurrentUser(HttpServletRequest http) {
        String ip = IpUtil.getClientIp(http);
        UserResponse userResponse = userService.getCurrentUser(ip);
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

    @PutMapping("/change-password")
    public ResponseEntity<ChangePasswordResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request, HttpServletRequest http) {
        String ip = IpUtil.getClientIp(http);
        ChangePasswordResponse response = userService.changePassword(request, ip);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
