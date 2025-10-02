package com.store.management.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for product response using Java 17+ record feature
 */
public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String category,
        Integer stockQuantity,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}