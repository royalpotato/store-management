package com.store.management.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for authentication requests using Java 17+ record feature
 */
public record LoginRequest(
        @NotBlank(message = "Username is required")
        String username,
        
        @NotBlank(message = "Password is required")
        String password
) {}