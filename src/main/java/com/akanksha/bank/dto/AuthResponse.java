package com.akanksha.bank.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String message;
    private String token; // Optional: Include JWT token if you want to return it on login
}