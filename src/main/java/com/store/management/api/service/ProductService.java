package com.store.management.api.service;

import com.store.management.api.dto.*;
import com.store.management.api.model.Product;
import com.store.management.api.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    
    /**
     * Adds a new product to the store
     * @param request Product creation request
     * @return Created product response
     */
    public ProductResponse addProduct(CreateProductRequest request) {
        log.info("Adding new product: {}", request.name());
        
        // Check if product with same name and category already exists
        Optional<Product> existingProduct = productRepository
            .findByNameAndCategory(request.name(), request.category());
        
        if (existingProduct.isPresent()) {
            log.warn("Product already exists with this name and category: {} - {}", 
                     request.name(), request.category());
            throw new IllegalArgumentException(
                "Product already exists with this name and category");
        }
        
        Product product = Product.builder()
            .name(request.name())
            .description(request.description())
            .price(request.price())
            .category(request.category())
            .stockQuantity(request.stockQuantity())
            .build();
        
        Product savedProduct = productRepository.save(product);
        log.info("Successfully added product with ID: {}", savedProduct.getId());
        
        return mapToResponse(savedProduct);
    }
    
    /**
     * Finds a product by ID
     * @param id Product ID
     * @return Product response
     */
    @Transactional(readOnly = true)
    public ProductResponse findProduct(Long id) {
        log.info("Finding product with ID: {}", id);
        
        Product product = productRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Product not found with ID: {}", id);
                return new EntityNotFoundException("Product not found with ID: " + id);
            });
        
        log.info("Found product: {}", product.getName());
        return mapToResponse(product);
    }
    
    /**
     * Finds products by name (case-insensitive partial match)
     * @param name Product name
     * @return List of matching products
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> findProductsByName(String name) {
        log.info("Searching products by name: {}", name);
        
        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
        log.info("Found {} products matching name: {}", products.size(), name);
        
        return products.stream()
            .map(this::mapToResponse)
            .toList();
    }
    
    /**
     * Finds products by category
     * @param category Product category
     * @return List of products in category
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> findProductsByCategory(String category) {
        log.info("Finding products by category: {}", category);
        
        List<Product> products = productRepository.findByCategory(category);
        log.info("Found {} products in category: {}", products.size(), category);
        
        return products.stream()
            .map(this::mapToResponse)
            .toList();
    }
    
    /**
     * Gets all products with pagination
     * @param pageable Pagination parameters
     * @return Page of products
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        log.info("Getting all products with pagination: {}", pageable);
        
        Page<Product> productPage = productRepository.findAll(pageable);
        log.info("Retrieved {} products from page {}", 
            productPage.getNumberOfElements(), productPage.getNumber());
        
        return productPage.map(this::mapToResponse);
    }
    
    /**
     * Changes the price of a product
     * @param id Product ID
     * @param request Price update request
     * @return Updated product response
     */
    public ProductResponse changePrice(Long id, UpdatePriceRequest request) {
        log.info("Changing price for product ID: {} to {}", id, request.newPrice());
        
        Product product = productRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Product not found with ID: {} for price update", id);
                return new EntityNotFoundException("Product not found with ID: " + id);
            });
        
        BigDecimal oldPrice = product.getPrice();
        product.changePrice(request.newPrice());
        
        Product savedProduct = productRepository.save(product);
        log.info("Successfully changed price for product ID: {} from {} to {}", 
            id, oldPrice, request.newPrice());
        
        return mapToResponse(savedProduct);
    }
    
    /**
     * Updates product stock quantity
     * @param id Product ID
     * @param quantity New stock quantity
     * @return Updated product response
     */
    public ProductResponse updateStock(Long id, Integer quantity) {
        log.info("Updating stock for product ID: {} to {}", id, quantity);
        
        Product product = productRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Product not found with ID: {} for stock update", id);
                return new EntityNotFoundException("Product not found with ID: " + id);
            });
        
        Integer oldQuantity = product.getStockQuantity();
        product.updateStock(quantity);
        
        Product savedProduct = productRepository.save(product);
        log.info("Successfully updated stock for product ID: {} from {} to {}", 
            id, oldQuantity, quantity);
        
        return mapToResponse(savedProduct);
    }
    
    /**
     * Deletes a product
     * @param id Product ID
     */
    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);
        
        if (!productRepository.existsById(id)) {
            log.error("Product not found with ID: {} for deletion", id);
            throw new EntityNotFoundException("Product not found with ID: " + id);
        }
        
        productRepository.deleteById(id);
        log.info("Successfully deleted product with ID: {}", id);
    }
    
    /**
     * Finds products within a price range
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @return List of products in price range
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> findProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        log.info("Finding products in price range: {} - {}", minPrice, maxPrice);
        
        List<Product> products = productRepository.findByPriceBetween(minPrice, maxPrice);
        log.info("Found {} products in price range: {} - {}", 
            products.size(), minPrice, maxPrice);
        
        return products.stream()
            .map(this::mapToResponse)
            .toList();
    }
    
    /**
     * Finds low stock products
     * @param threshold Stock threshold
     * @return List of low stock products
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> findLowStockProducts(Integer threshold) {
        log.info("Finding products with stock below: {}", threshold);
        
        List<Product> products = productRepository.findByStockQuantityLessThan(threshold);
        log.info("Found {} products with low stock", products.size());
        
        return products.stream()
            .map(this::mapToResponse)
            .toList();
    }
    
    /**
     * Maps Product entity to ProductResponse DTO
     * @param product Product entity
     * @return Product response DTO
     */
    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getCategory(),
            product.getStockQuantity(),
            product.getCreatedAt(),
            product.getUpdatedAt()
        );
    }
}