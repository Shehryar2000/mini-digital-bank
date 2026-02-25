package com.mini.bank.auth.service;

import com.mini.bank.auth.dto.UserLoginRequest;
import com.mini.bank.auth.dto.UserRegisterRequest;
import com.mini.bank.auth.entity.User;
import com.mini.bank.auth.enums.Role;
import com.mini.bank.auth.repository.UserRepository;
import com.mini.bank.auth.security.JwtUtil;
import com.mini.bank.common.exception.AccountLockedException;
import com.mini.bank.common.exception.InvalidCredentialsException;
import com.mini.bank.common.exception.UsernameAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public void register(UserRegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(bCryptPasswordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_USER);
        user.setEnabled(true);
        userRepository.save(user);
    }

    public String login(UserLoginRequest request) {

        try {
            System.out.println("Login User");

            User user = userRepository
                    .findByUsername(request.getUsername())
                    .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

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

            return jwtUtil.generateToken(request.getUsername());

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
}
