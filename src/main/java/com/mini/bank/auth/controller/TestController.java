package com.mini.bank.auth.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/")
    public String test(Authentication authentication){
        return "Hello" + authentication.getName() + ", your token is valid!";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public String userEndPoint(){
        return "User Access Granted";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminEndPoint(){
        return "Admin Access Granted";
    }
}
