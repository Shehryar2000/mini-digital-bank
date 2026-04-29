package com.mini.bank.auth.service;

import com.mini.bank.audit.enums.AuditAction;
import com.mini.bank.audit.enums.AuditEntityType;
import com.mini.bank.audit.service.AuditService;
import com.mini.bank.auth.dto.*;
import com.mini.bank.auth.entity.User;
import com.mini.bank.auth.enums.Role;
import com.mini.bank.auth.repository.UserRepository;
import com.mini.bank.auth.security.JwtUtil;
import com.mini.bank.common.exception.*;
import com.mini.bank.customer.entity.Customer;
import com.mini.bank.customer.service.CustomerService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CustomerService customerService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AuditService auditService;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public UserRegisterResponse register(UserRegisterRequest request, String ip) {

        try {

            // Validate username
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new UsernameAlreadyExistsException("Username already exists");
            }

            // Validate email
            if (customerService.existsByEmail(request.getEmail())) {
                throw new EmailAlreadyExistsException("Email already exists");
            }

            //Creating User
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPasswordHash(bCryptPasswordEncoder.encode(request.getPassword()));
            user.setRole(Role.ROLE_CUSTOMER);
            user.setEnabled(true);
            userRepository.save(user);

            // Creating Customer via Customer Service
            Customer customer = customerService.createCustomerInternal(user, request.getName(), request.getEmail());
            user.setCustomer(customer);
            entityManager.refresh(customer);

            // Audit Maintaining - User Registration Success
            auditService.success(user.getId(),
                    AuditAction.REGISTER,
                    ip,
                    AuditEntityType.USER,
                    user.getId(),
                    Map.of(
                            "customerId", customer.getId(),
                            "customerNumber", customer.getCustomerNumber(),
                            "role", user.getRole().name()
                    ));

            UserRegisterResponse response = UserRegisterResponse.builder()
                    .userId(user.getId())
                    .customerId(customer.getId())
                    .customerNumber(customer.getCustomerNumber())
                    .username(request.getUsername())
                    .name(request.getName())
                    .email(request.getEmail())
                    .build();

            return response;

        } catch (Exception e) {

            // Audit Maintaining - User Registration Failed
            auditService.failure(
                    null,
                    AuditAction.REGISTER,
                    ip,
                    AuditEntityType.USER,
                    null,
                    errorMeta(e)
            );
            throw e;
        }

    }

    public UserLoginResponse login(UserLoginRequest request, String ip) {

        try {

            // Fetch User
            User user = userRepository.findByUsername(request.getUsername().trim()).orElse(null);

            // Username Not Found
            if (user == null) {

                auditService.failure(
                        null,
                        AuditAction.LOGIN,
                        ip,
                        AuditEntityType.USER,
                        null,
                        Map.of(
                                "username", request.getUsername().trim(),
                                "reason", "username not found"
                        )
                );
                throw new InvalidCredentialsException("Invalid Credentials");
            }

            // User disabled
            if (!user.isEnabled()) {

                auditService.failure(
                        user.getId(),
                        AuditAction.LOGIN,
                        ip,
                        AuditEntityType.USER,
                        user.getId(),
                        Map.of("reason", "user disabled")
                );

                throw new UserDisabledException("User is disabled");
            }

            // Account Auto Unlocked
            unlockIfTimeExpired(user, ip);

            // Still Account Locked
            if (user.isAccountLocked()) {

                auditService.failure(
                        user.getId(),
                        AuditAction.LOGIN,
                        ip,
                        AuditEntityType.USER,
                        user.getId(),
                        Map.of("reason", "account locked")
                );

                throw new AccountLockedException("Account is locked, Try after 15 minutes.");
            }

            // Authentication (Spring Security password check)
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // Reset Failed Attempts
            user.setFailedAttempts(0);
            userRepository.save(user);

            UUID customerId = null;

            if (user.getRole() == Role.ROLE_CUSTOMER && user.getCustomer() != null) {
                customerId = user.getCustomer().getId();
            }

            Map<String, Object> metadata = new HashMap<>();

            if (customerId != null) {
                metadata.put("customerId", customerId.toString());
            } else {
                metadata.put("role", user.getRole().name());
            }

            // Audit Maintaining - User Login Success
            auditService.success(
                    user.getId(),
                    AuditAction.LOGIN,
                    ip,
                    AuditEntityType.USER,
                    user.getId(),
                    metadata
            );

            return UserLoginResponse.builder()
                    .token(jwtUtil.generateToken(user, customerId))
                    .build();

        } catch (BadCredentialsException e) {

            User user = userRepository
                    .findByUsername(request.getUsername().trim())
                    .orElse(null);

            if (user != null) {
                int attempts = user.getFailedAttempts() + 1;
                user.setFailedAttempts(attempts);

                // Audit Maintaining - Wrong Password
                auditService.failure(
                        user.getId(),
                        AuditAction.LOGIN,
                        ip,
                        AuditEntityType.USER,
                        user.getId(),
                        Map.of("attempts", attempts)
                );

                // Lock Account After Threshold
                if (attempts >= 5) {
                    user.setAccountLocked(true);
                    user.setLockTime(LocalDateTime.now());

                    // Audit Maintaining - Account Locking
                    auditService.success(
                            user.getId(),
                            AuditAction.ACCOUNT_LOCKED,
                            ip,
                            AuditEntityType.USER,
                            user.getId(),
                            Map.of("reason", "too many failed login attempts")
                    );
                }
                userRepository.save(user);
            }

            throw new InvalidCredentialsException("Invalid credentials");

        } catch (InvalidCredentialsException | UserDisabledException | AccountLockedException e) {
            throw e;
        } catch (Exception e) {

            auditService.failure(
                    null,
                    AuditAction.LOGIN,
                    ip,
                    AuditEntityType.USER,
                    null,
                    errorMeta(e)
            );

            throw e;
        }

    }

    private void unlockIfTimeExpired(User user, String ip) {

        if (user.isAccountLocked() &&
                user.getLockTime() != null &&
                user.getLockTime().plusMinutes(15).isBefore(LocalDateTime.now())) {

            user.setAccountLocked(false);
            user.setFailedAttempts(0);
            user.setLockTime(null);

            userRepository.save(user);

            // Audit Maintaining - Account Unlocked Success

            auditService.success(
                    user.getId(),
                    AuditAction.ACCOUNT_UNLOCKED,
                    ip,
                    AuditEntityType.USER,
                    user.getId(),
                    Map.of("auto", true)
            );

        }
    }

    private Map<String, Object> errorMeta(Exception e) {
        return Map.of(
                "error", e.getClass().getSimpleName(),
                "message", e.getMessage()
        );
    }

}
