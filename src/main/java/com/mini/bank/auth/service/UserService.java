package com.mini.bank.auth.service;

import com.mini.bank.audit.enums.AuditAction;
import com.mini.bank.audit.enums.AuditEntityType;
import com.mini.bank.audit.service.AuditService;
import com.mini.bank.auth.dto.ChangePasswordRequest;
import com.mini.bank.auth.dto.ChangePasswordResponse;
import com.mini.bank.auth.dto.UserResponse;
import com.mini.bank.auth.entity.User;
import com.mini.bank.auth.repository.UserRepository;
import com.mini.bank.common.exception.InvalidCredentialsException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    private final AuditService auditService;

    // Get Current User
    public UserResponse getCurrentUser(String ip) {

        try {
            User user = getUserObject();

            log.info("User fetched successfully | userId={}, userName={}",
                    user.getId(),
                    user.getUsername()
            );

            auditService.success(
                    user.getId(),
                    AuditAction.USER_FETCH,
                    ip,
                    AuditEntityType.USER,
                    user.getId(),
                    null
            );

            return UserResponse.builder()
                    .userId(user.getId())
                    .username(user.getUsername())
                    .role(user.getRole().toString())
                    .enabled(user.isEnabled())
                    .build();

        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {

            log.warn("User fetching failed");

            auditService.failure(
                    null,
                    AuditAction.USER_FETCH,
                    ip,
                    AuditEntityType.USER,
                    null,
                    errorMeta(e)
            );

            throw e;
        }

    }

    public ChangePasswordResponse changePassword(ChangePasswordRequest request, String ip) {

        try {

            User user = getUserObject();

            if (!bCryptPasswordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {

                log.warn("User password change failed due to invalid old password | userId={}", user.getId());

                // Audit Maintaining - Password Change Failure
                auditService.failure(
                        user.getId(),
                        AuditAction.PASSWORD_CHANGE,
                        ip,
                        AuditEntityType.USER,
                        user.getId(),
                        Map.of("reason", "invalid old password")
                );
                throw new InvalidCredentialsException("Invalid old password");
            }

            user.setPasswordHash(bCryptPasswordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);

            log.info("User password changed successfully | userId={}, userName={}",
                    user.getId(),
                    user.getUsername()
            );

            // Audit Maintaining - Password Change Success
            auditService.success(
                    user.getId(),
                    AuditAction.PASSWORD_CHANGE,
                    ip,
                    AuditEntityType.USER,
                    user.getId(),
                    null
            );

            return ChangePasswordResponse.builder()
                    .message("Password changed successfully")
                    .build();
        } catch (InvalidCredentialsException e) {
            throw e;
        } catch (Exception e) {

            log.warn("User password change failed");

            auditService.failure(
                    null,
                    AuditAction.PASSWORD_CHANGE,
                    ip,
                    AuditEntityType.USER,
                    null,
                    errorMeta(e)
            );
            throw e;
        }
    }

    public User getUserObject() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Claims claims = (Claims) auth.getDetails();
        UUID userId = UUID.fromString(claims.get("userId").toString());
        return userRepository.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("User not found")
        );
    }

    private Map<String, Object> errorMeta(Exception e) {
        return Map.of(
                "error", e.getClass().getSimpleName(),
                "message", e.getMessage()
        );
    }
}
