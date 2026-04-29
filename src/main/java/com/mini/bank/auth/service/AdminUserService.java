package com.mini.bank.auth.service;

import com.mini.bank.audit.enums.AuditAction;
import com.mini.bank.audit.enums.AuditEntityType;
import com.mini.bank.audit.service.AuditService;
import com.mini.bank.auth.dto.AdminResponse;
import com.mini.bank.auth.dto.UpdateRoleRequest;
import com.mini.bank.auth.entity.User;
import com.mini.bank.auth.enums.Role;
import com.mini.bank.auth.repository.UserRepository;
import com.mini.bank.common.exception.SelfActionNotAllowedException;
import com.mini.bank.common.exception.UserAlreadyDisabledException;
import com.mini.bank.common.exception.UserAlreadyEnabledException;
import com.mini.bank.common.security.AuthContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final AuditService auditService;
    private final AuthContext authContext;

    public AdminResponse enableUser(UUID userId, String ip) {

        UUID adminId = authContext.getUserId();

        try {

            User user = getUser(userId);

            if (user.isEnabled()) {
                throw new UserAlreadyEnabledException("User is already enabled");
            }

            user.setEnabled(true);
            userRepository.save(user);


            // Audit Maintaining - Enable User Success
            auditService.success(adminId,
                    AuditAction.USER_ENABLED,
                    ip,
                    AuditEntityType.USER,
                    user.getId(),
                    Map.of("status", "enabled")
            );

            return AdminResponse.builder()
                    .message("user enabled successfully")
                    .build();

        } catch (Exception e) {

            // Audit Maintaining - Enable User Failure
            auditService.failure(
                    adminId,
                    AuditAction.USER_ENABLED,
                    ip,
                    AuditEntityType.USER,
                    userId,
                    errorMeta(e)
            );
            throw e;
        }

    }

    public AdminResponse disableUser(UUID userId, String ip) {

        UUID adminId = authContext.getUserId();

        try {

            if (adminId.equals(userId)) {
                throw new SelfActionNotAllowedException("self disable not allowed");
            }

            User user = getUser(userId);

            if (!user.isEnabled()) {
                throw new UserAlreadyDisabledException("User is already disabled");
            }

            user.setEnabled(false);
            userRepository.save(user);
            // Audit Maintaining - Disable User Success
            auditService.success(
                    adminId,
                    AuditAction.USER_DISABLED,
                    ip,
                    AuditEntityType.USER,
                    user.getId(),
                    Map.of("status", "disabled")
            );

            return AdminResponse.builder()
                    .message("user disabled successfully")
                    .build();

        } catch (Exception e) {

            // Audit Maintaining - Disable User Failure
            auditService.failure(
                    adminId,
                    AuditAction.USER_DISABLED,
                    ip,
                    AuditEntityType.USER,
                    userId,
                    errorMeta(e)
            );

            throw e;
        }
    }

    @Transactional
    public AdminResponse updateUserRole(UUID userId, UpdateRoleRequest request, String ip) {

        UUID adminId = authContext.getUserId();

        try {

            User user = getUser(userId);
            Role oldRole = user.getRole();
            Role newRole = Role.valueOf(request.getRole());
            user.setRole(newRole);
            userRepository.save(user);

            // Audit Maintaining - Role Change Success
            auditService.success(
                    adminId,
                    AuditAction.ROLE_CHANGED,
                    ip,
                    AuditEntityType.USER,
                    userId,
                    Map.of(
                            "oldRole", oldRole.name(),
                            "newRole", newRole.name()
                    ));

            return AdminResponse.builder()
                    .message("role updated successfully")
                    .build();

        } catch (Exception e) {

            // Audit Maintaining - Role Change Failure
            auditService.failure(
                    adminId,
                    AuditAction.ROLE_CHANGED,
                    ip,
                    AuditEntityType.USER,
                    userId,
                    errorMeta(e)
            );
            throw e;
        }
    }

    private User getUser(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));
    }

    private Map<String, Object> errorMeta(Exception e) {
        return Map.of(
                "error", e.getClass().getSimpleName(),
                "message", e.getMessage()
        );
    }

}
