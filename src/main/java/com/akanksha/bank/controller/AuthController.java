package com.akanksha.bank.controller;

import com.akanksha.bank.dto.AuthRequest;
import com.akanksha.bank.dto.AuthResponse;
import com.akanksha.bank.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        return userService.login(request);
    }
}