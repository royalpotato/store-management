package com.store.management.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    
    private final ProductService productService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ProductResponse> addProduct(@Valid @RequestBody CreateProductRequest request) {
        log.info("Request to add new product: {}", request.name());
        
        ProductResponse response = productService.addProduct(request);
        
        log.info("Successfully added product with ID: {}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ProductResponse> findProduct(@PathVariable Long id) {
        log.info("Request to find product with ID: {}", id);
        
        ProductResponse response = productService.findProduct(id);
        
        log.info("Successfully found product: {}", response.name());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        log.info("Request to get all products with pagination: {}", pageable);
        
        Page<ProductResponse> response = productService.getAllProducts(pageable);
        
        log.info("Successfully retrieved {} products", response.getNumberOfElements());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<ProductResponse>> searchProductsByName(@RequestParam String name) {
        log.info("Request to search products by name: {}", name);
        
        List<ProductResponse> response = productService.findProductsByName(name);
        
        log.info("Found {} products matching name: {}", response.size(), name);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/category/{category}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<ProductResponse>> findProductsByCategory(@PathVariable String category) {
        log.info("Request to find products by category: {}", category);
        
        List<ProductResponse> response = productService.findProductsByCategory(category);
        
        log.info("Found {} products in category: {}", response.size(), category);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/price")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ProductResponse> changePrice(@PathVariable Long id,
            @Valid @RequestBody UpdatePriceRequest request) {
        log.info("Request to change price for product ID: {} to {}", id, request.newPrice());
        
        ProductResponse response = productService.changePrice(id, request);
        
        log.info("Successfully changed price for product ID: {}", id);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ProductResponse> updateStock(@PathVariable Long id, @RequestParam Integer quantity) {
        log.info("Request to update stock for product ID: {} to {}", id, quantity);
        
        ProductResponse response = productService.updateStock(id, quantity);
        
        log.info("Successfully updated stock for product ID: {}", id);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.info("Request to delete product with ID: {}", id);
        
        productService.deleteProduct(id);
        
        log.info("Successfully deleted product with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/price-range")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<ProductResponse>> findProductsByPriceRange(
            @RequestParam BigDecimal minPrice, @RequestParam BigDecimal maxPrice) {
        log.info("Request to find products in price range: {} - {}", minPrice, maxPrice);
        
        List<ProductResponse> response = productService.findProductsByPriceRange(minPrice, maxPrice);
        
        log.info("Found {} products in price range", response.size());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<ProductResponse>> findLowStockProducts(
            @RequestParam(defaultValue = "10") Integer threshold) {
        log.info("Request to find products with stock below: {}", threshold);
        
        List<ProductResponse> response = productService.findLowStockProducts(threshold);
        
        log.info("Found {} products with low stock", response.size());
        return ResponseEntity.ok(response);
    }
}