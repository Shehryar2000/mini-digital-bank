package com.mini.bank.auth.service;

import com.mini.bank.auth.dto.UserRegisterRequest;
import com.mini.bank.auth.entity.User;
import com.mini.bank.auth.enums.Role;
import com.mini.bank.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void register(UserRegisterRequest request) {

//        if (userRepository.existsByUsername(request.getUsername())) {
//            throw new RuntimeException("Username already exists");
//        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(bCryptPasswordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_USER);
        user.setEnabled(true);
        userRepository.save(user);
    }
}
