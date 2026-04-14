package com.mini.bank.auth.service;

import com.mini.bank.auth.dto.*;
import com.mini.bank.auth.entity.User;
import com.mini.bank.auth.enums.Role;
import com.mini.bank.auth.repository.UserRepository;
import com.mini.bank.auth.security.JwtUtil;
import com.mini.bank.common.exception.AccountLockedException;
import com.mini.bank.common.exception.InvalidCredentialsException;
import com.mini.bank.common.exception.UsernameAlreadyExistsException;
import com.mini.bank.customer.entity.Customer;
import com.mini.bank.customer.repository.CustomerRepository;
import com.mini.bank.customer.service.CustomerService;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CustomerService customerService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public UserRegisterResponse register(UserRegisterRequest request) {

        // Validate username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        // Validate email
        if (customerService.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
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

        UserRegisterResponse response = UserRegisterResponse.builder()
                .userId(user.getId())
                .customerId(customer.getId())
                .customerNumber(customer.getCustomerNumber())
                .username(request.getUsername())
                .name(request.getName())
                .email(request.getEmail())
                .build();

        return response;

    }

    public UserLoginResponse login(UserLoginRequest request) {

        try {
            System.out.println("Login User");

            User user = userRepository
                    .findByUsername(request.getUsername())
                    .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

            if (!user.isEnabled()) {
                throw new RuntimeException("User is disabled");
            }

            unlockIfTimeExpired(user);

            if (user.isAccountLocked()) {
                throw new AccountLockedException("Account is locked, Try after 15 minutes.");
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            user.setFailedAttempts(0);
            userRepository.save(user);
            UUID customerId = user.getCustomer().getId();

            UserLoginResponse response = UserLoginResponse.builder()
                    .token(jwtUtil.generateToken(user, customerId))
                    .build();

            return response;

        } catch (BadCredentialsException e) {

            User user = userRepository
                    .findByUsername(request.getUsername())
                    .orElse(null);

            if (user != null) {
                int attempts = user.getFailedAttempts() + 1;
                user.setFailedAttempts(attempts);

                if (attempts >= 5) {
                    user.setAccountLocked(true);
                    user.setLockTime(LocalDateTime.now());
                }
                userRepository.save(user);
            }

            throw new InvalidCredentialsException("Invalid credentials");
        }

    }

    private void unlockIfTimeExpired(User user) {

        if (user.isAccountLocked() &&
                user.getLockTime() != null &&
                user.getLockTime().plusMinutes(15).isBefore(LocalDateTime.now())) {

            user.setAccountLocked(false);
            user.setFailedAttempts(0);
            user.setLockTime(null);

            userRepository.save(user);
        }
    }

    // Get Current User
    public UserResponse getCurrentUser() {

        User user = getUserObject();

        UserResponse response = UserResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole().toString())
                .enabled(user.isEnabled())
                .build();

        return response;
    }

    public void changePassword(ChangePasswordRequest request) {

        User user = getUserObject();

        if (!bCryptPasswordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid old password");
        }

        user.setPasswordHash(bCryptPasswordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public User getUserObject() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Claims claims = (Claims) auth.getDetails();
        UUID userId = UUID.fromString(claims.get("userId").toString());
        return userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public void enableUser(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
    }

    public void disableUser(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (userId.equals(user.getId())) {
            throw new RuntimeException("You cannot disable yourself");
        }
        user.setEnabled(false);
        userRepository.save(user);
    }
}
