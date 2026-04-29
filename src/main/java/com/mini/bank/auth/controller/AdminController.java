package com.mini.bank.auth.controller;

import com.mini.bank.auth.dto.AdminResponse;
import com.mini.bank.auth.dto.UpdateRoleRequest;
import com.mini.bank.auth.service.AdminUserService;
import com.mini.bank.auth.service.AuthService;
import com.mini.bank.common.util.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
public class AdminController {

    private final AdminUserService  adminUserService;

    public AdminController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{userId}/enable")
    public ResponseEntity<?> enableUser(@PathVariable UUID userId, HttpServletRequest http) {
        String ip = IpUtil.getClientIp(http);
        AdminResponse response = adminUserService.enableUser(userId, ip);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{userId}/disable")
    public ResponseEntity<?> disableUser(@PathVariable UUID userId, HttpServletRequest http) {
        String ip = IpUtil.getClientIp(http);
        AdminResponse response = adminUserService.disableUser(userId, ip);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{userId}/role")
    public ResponseEntity<?> updateRole(@PathVariable UUID userId, @RequestBody UpdateRoleRequest request, HttpServletRequest http) {

        String ip = IpUtil.getClientIp(http);
        AdminResponse response = adminUserService.updateUserRole(userId, request, ip);
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

}
