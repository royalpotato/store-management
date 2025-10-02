package com.store.management.api.dto;

/**
 * DTO for authentication responses using Java 17+ record feature
 */
public record AuthResponse(
        String token,
        String type,
        int expiresIn,
        String username,
        String role
) {}