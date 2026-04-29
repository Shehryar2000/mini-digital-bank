package com.mini.bank.auth.config;

import com.mini.bank.auth.entity.User;
import com.mini.bank.auth.enums.Role;
import com.mini.bank.auth.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminUserInitializer {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostConstruct
    public void init() {

        if (!userRepository.existsByUsername("admin")) {

            User admin = new User();
            admin.setUsername("admin");
            admin.setPasswordHash(bCryptPasswordEncoder.encode("admin"));
            admin.setRole(Role.ROLE_ADMIN);
            admin.setEnabled(true);
            userRepository.save(admin);

        }
    }
}
