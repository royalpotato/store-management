package com.store.management.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "products")
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String name;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Column(length = 500)
    private String description;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price must have at most 10 integer digits and 2 decimal places")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;
    
    @NotBlank(message = "Category is required")
    @Size(min = 2, max = 50, message = "Category must be between 2 and 50 characters")
    @Column(nullable = false, length = 50)
    private String category;
    
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    @Column(nullable = false)
    private Integer stockQuantity;
    
    @Size(max = 50, message = "Brand cannot exceed 50 characters")
    @Column(length = 50)
    private String brand;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // Constructors
    
    public Product(String name, String description, BigDecimal price, String category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    
    // Business Methods
    public void changePrice(@NotNull(message = "Price is required") 
                           @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0") 
                           @Digits(integer = 10, fraction = 2, message = "Price must have at most 10 integer digits and 2 decimal places") 
                           BigDecimal newPrice) {
        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
        this.price = newPrice;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void updateStock(Integer newStock) {
        if (newStock == null || newStock < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        this.stockQuantity = newStock;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void incrementStock(Integer amount) {
        if (amount == null || amount < 0) {
            throw new IllegalArgumentException("Amount to increment cannot be negative");
        }
        this.stockQuantity += amount;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void decrementStock(Integer amount) {
        if (amount == null || amount < 0) {
            throw new IllegalArgumentException("Amount to decrement cannot be negative");
        }
        if (this.stockQuantity < amount) {
            throw new IllegalArgumentException("Insufficient stock quantity");
        }
        this.stockQuantity -= amount;
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean isInStock() {
        return this.stockQuantity != null && this.stockQuantity > 0;
    }
    
    public boolean isLowStock(Integer threshold) {
        return this.stockQuantity != null && threshold != null && this.stockQuantity <= threshold;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", active=" + active +
                '}';
    }
}