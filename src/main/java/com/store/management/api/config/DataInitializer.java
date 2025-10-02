package com.store.management.api.config;

import com.store.management.api.model.Product;
import com.store.management.api.model.Role;
import com.store.management.api.model.User;
import com.store.management.api.repository.ProductRepository;
import com.store.management.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        initializeUsers();
        initializeProducts();
    }
    
    private void initializeUsers() {
        if (userRepository.count() == 0) {
            log.info("Initializing default users...");
            
            // Create admin user
            User admin = User.builder()
                .username("admin")
                .email("admin@storemanagement.com")
                .password(passwordEncoder.encode("admin123"))
                .firstName("Admin")
                .lastName("User")
                .role(Role.ADMIN)
                .enabled(true)
                .build();
            userRepository.save(admin);
            log.info("Created admin user: admin/admin123");
            
            // Create manager user
            User manager = User.builder()
                .username("manager")
                .email("manager@storemanagement.com")
                .password(passwordEncoder.encode("manager123"))
                .firstName("Manager")
                .lastName("User")
                .role(Role.MANAGER)
                .enabled(true)
                .build();
            userRepository.save(manager);
            log.info("Created manager user: manager/manager123");
            
            // Create regular user
            User user = User.builder()
                .username("user")
                .email("user@storemanagement.com")
                .password(passwordEncoder.encode("user123"))
                .firstName("Regular")
                .lastName("User")
                .role(Role.USER)
                .enabled(true)
                .build();
            userRepository.save(user);
            log.info("Created regular user: user/user123");
            
            log.info("Default users created successfully");
        }
    }
    
    private void initializeProducts() {
        if (productRepository.count() == 0) {
            log.info("Initializing sample products...");
            
            // Electronics
            Product laptop = Product.builder()
                .name("Gaming Laptop")
                .description("High-performance gaming laptop with RTX graphics")
                .price(new BigDecimal("1299.99"))
                .category("Electronics")
                .stockQuantity(15)
                .build();
            productRepository.save(laptop);
            
            Product smartphone = Product.builder()
                .name("Smartphone Pro")
                .description("Latest smartphone with advanced camera features")
                .price(new BigDecimal("899.99"))
                .category("Electronics")
                .stockQuantity(25)
                .build();
            productRepository.save(smartphone);
            
            Product headphones = Product.builder()
                .name("Wireless Headphones")
                .description("Noise-cancelling wireless headphones")
                .price(new BigDecimal("249.99"))
                .category("Electronics")
                .stockQuantity(30)
                .build();
            productRepository.save(headphones);
            
            // Clothing
            Product jeans = Product.builder()
                .name("Premium Jeans")
                .description("High-quality denim jeans")
                .price(new BigDecimal("89.99"))
                .category("Clothing")
                .stockQuantity(50)
                .build();
            productRepository.save(jeans);
            
            Product tshirt = Product.builder()
                .name("Cotton T-Shirt")
                .description("Comfortable cotton t-shirt")
                .price(new BigDecimal("19.99"))
                .category("Clothing")
                .stockQuantity(100)
                .build();
            productRepository.save(tshirt);
            
            // Books
            Product programmingBook = Product.builder()
                .name("Spring Boot in Action")
                .description("Comprehensive guide to Spring Boot development")
                .price(new BigDecimal("49.99"))
                .category("Books")
                .stockQuantity(20)
                .build();
            productRepository.save(programmingBook);
            
            Product novel = Product.builder()
                .name("Science Fiction Novel")
                .description("Bestselling science fiction adventure")
                .price(new BigDecimal("14.99"))
                .category("Books")
                .stockQuantity(35)
                .build();
            productRepository.save(novel);
            
            // Home & Garden
            Product coffeeMaker = Product.builder()
                .name("Automatic Coffee Maker")
                .description("Programmable coffee maker with timer")
                .price(new BigDecimal("129.99"))
                .category("Home & Garden")
                .stockQuantity(12)
                .build();
            productRepository.save(coffeeMaker);
            
            Product plant = Product.builder()
                .name("Indoor Plant")
                .description("Low-maintenance indoor plant")
                .price(new BigDecimal("24.99"))
                .category("Home & Garden")
                .stockQuantity(8)
                .build();
            productRepository.save(plant);
            
            // Sports
            Product runningShoes = Product.builder()
                .name("Running Shoes")
                .description("Professional running shoes")
                .price(new BigDecimal("119.99"))
                .category("Sports")
                .stockQuantity(40)
                .build();
            productRepository.save(runningShoes);
            
            log.info("Sample products created successfully");
        }
    }
}