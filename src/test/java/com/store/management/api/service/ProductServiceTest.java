package com.store.management.api.service;

import com.store.management.api.dto.CreateProductRequest;
import com.store.management.api.dto.ProductResponse;
import com.store.management.api.dto.UpdatePriceRequest;
import com.store.management.api.model.Product;
import com.store.management.api.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Tests")
class ProductServiceTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @InjectMocks
    private ProductService productService;
    
    private Product testProduct;
    private CreateProductRequest createRequest;
    private UpdatePriceRequest updatePriceRequest;
    
    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
            .id(1L)
            .name("Test Product")
            .description("Test Description")
            .price(new BigDecimal("99.99"))
            .category("Electronics")
            .stockQuantity(10)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        
        createRequest = new CreateProductRequest(
            "Test Product",
            "Test Description",
            new BigDecimal("99.99"),
            "Electronics",
            10
        );
        
        updatePriceRequest = new UpdatePriceRequest(new BigDecimal("149.99"));
    }
    
    @Test
    @DisplayName("Should add product successfully when valid request provided")
    void addProduct_WithValidRequest_ShouldReturnProductResponse() {
        // Given
        when(productRepository.findByNameAndCategory(anyString(), anyString()))
            .thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        // When
        ProductResponse result = productService.addProduct(createRequest);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(testProduct.getId());
        assertThat(result.name()).isEqualTo(testProduct.getName());
        assertThat(result.description()).isEqualTo(testProduct.getDescription());
        assertThat(result.price()).isEqualTo(testProduct.getPrice());
        assertThat(result.category()).isEqualTo(testProduct.getCategory());
        assertThat(result.stockQuantity()).isEqualTo(testProduct.getStockQuantity());
        
        verify(productRepository).findByNameAndCategory(createRequest.name(), createRequest.category());
        verify(productRepository).save(any(Product.class));
    }
    
    @Test
    @DisplayName("Should throw exception when product already exists")
    void addProduct_WithExistingProduct_ShouldThrowException() {
        // Given
        when(productRepository.findByNameAndCategory(anyString(), anyString()))
            .thenReturn(Optional.of(testProduct));
        
        // When & Then
        assertThatThrownBy(() -> productService.addProduct(createRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Product already exists with this name and category");
        
        verify(productRepository).findByNameAndCategory(createRequest.name(), createRequest.category());
        verify(productRepository, never()).save(any(Product.class));
    }
    
    @Test
    @DisplayName("Should find product by ID successfully when product exists")
    void findProduct_WithValidId_ShouldReturnProductResponse() {
        // Given
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        
        // When
        ProductResponse result = productService.findProduct(productId);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(testProduct.getId());
        assertThat(result.name()).isEqualTo(testProduct.getName());
        
        verify(productRepository).findById(productId);
    }
    
    @Test
    @DisplayName("Should throw exception when product not found by ID")
    void findProduct_WithInvalidId_ShouldThrowException() {
        // Given
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> productService.findProduct(productId))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Product not found with ID: " + productId);
        
        verify(productRepository).findById(productId);
    }
    
    @Test
    @DisplayName("Should find products by name successfully")
    void findProductsByName_WithValidName_ShouldReturnProductList() {
        // Given
        String productName = "Test";
        List<Product> products = List.of(testProduct);
        when(productRepository.findByNameContainingIgnoreCase(productName)).thenReturn(products);
        
        // When
        List<ProductResponse> result = productService.findProductsByName(productName);
        
        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo(testProduct.getName());
        
        verify(productRepository).findByNameContainingIgnoreCase(productName);
    }
    
    @Test
    @DisplayName("Should find products by category successfully")
    void findProductsByCategory_WithValidCategory_ShouldReturnProductList() {
        // Given
        String category = "Electronics";
        List<Product> products = List.of(testProduct);
        when(productRepository.findByCategory(category)).thenReturn(products);
        
        // When
        List<ProductResponse> result = productService.findProductsByCategory(category);
        
        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).category()).isEqualTo(testProduct.getCategory());
        
        verify(productRepository).findByCategory(category);
    }
    
    @Test
    @DisplayName("Should get all products with pagination successfully")
    void getAllProducts_WithPagination_ShouldReturnPagedResults() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(List.of(testProduct), pageable, 1);
        when(productRepository.findAll(pageable)).thenReturn(productPage);
        
        // When
        Page<ProductResponse> result = productService.getAllProducts(pageable);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).name()).isEqualTo(testProduct.getName());
        
        verify(productRepository).findAll(pageable);
    }
    
    @Test
    @DisplayName("Should change product price successfully when product exists")
    void changePrice_WithValidIdAndPrice_ShouldReturnUpdatedProduct() {
        // Given
        Long productId = 1L;
        BigDecimal newPrice = new BigDecimal("149.99");
        Product updatedProduct = testProduct.toBuilder()
            .price(newPrice)
            .build();
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);
        
        // When
        ProductResponse result = productService.changePrice(productId, updatePriceRequest);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.price()).isEqualTo(newPrice);
        
        verify(productRepository).findById(productId);
        verify(productRepository).save(any(Product.class));
    }
    
    @Test
    @DisplayName("Should throw exception when changing price of non-existent product")
    void changePrice_WithInvalidId_ShouldThrowException() {
        // Given
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> productService.changePrice(productId, updatePriceRequest))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Product not found with ID: " + productId);
        
        verify(productRepository).findById(productId);
        verify(productRepository, never()).save(any(Product.class));
    }
    
    @Test
    @DisplayName("Should update stock successfully when product exists")
    void updateStock_WithValidIdAndQuantity_ShouldReturnUpdatedProduct() {
        // Given
        Long productId = 1L;
        Integer newQuantity = 25;
        Product updatedProduct = testProduct.toBuilder()
            .stockQuantity(newQuantity)
            .build();
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);
        
        // When
        ProductResponse result = productService.updateStock(productId, newQuantity);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.stockQuantity()).isEqualTo(newQuantity);
        
        verify(productRepository).findById(productId);
        verify(productRepository).save(any(Product.class));
    }
    
    @Test
    @DisplayName("Should delete product successfully when product exists")
    void deleteProduct_WithValidId_ShouldDeleteProduct() {
        // Given
        Long productId = 1L;
        when(productRepository.existsById(productId)).thenReturn(true);
        doNothing().when(productRepository).deleteById(productId);
        
        // When
        productService.deleteProduct(productId);
        
        // Then
        verify(productRepository).existsById(productId);
        verify(productRepository).deleteById(productId);
    }
    
    @Test
    @DisplayName("Should throw exception when deleting non-existent product")
    void deleteProduct_WithInvalidId_ShouldThrowException() {
        // Given
        Long productId = 999L;
        when(productRepository.existsById(productId)).thenReturn(false);
        
        // When & Then
        assertThatThrownBy(() -> productService.deleteProduct(productId))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Product not found with ID: " + productId);
        
        verify(productRepository).existsById(productId);
        verify(productRepository, never()).deleteById(productId);
    }
    
    @Test
    @DisplayName("Should find products by price range successfully")
    void findProductsByPriceRange_WithValidRange_ShouldReturnProductList() {
        // Given
        BigDecimal minPrice = new BigDecimal("50.00");
        BigDecimal maxPrice = new BigDecimal("150.00");
        List<Product> products = List.of(testProduct);
        when(productRepository.findByPriceBetween(minPrice, maxPrice)).thenReturn(products);
        
        // When
        List<ProductResponse> result = productService.findProductsByPriceRange(minPrice, maxPrice);
        
        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).price()).isEqualTo(testProduct.getPrice());
        
        verify(productRepository).findByPriceBetween(minPrice, maxPrice);
    }
    
    @Test
    @DisplayName("Should find low stock products successfully")
    void findLowStockProducts_WithThreshold_ShouldReturnProductList() {
        // Given
        Integer threshold = 15;
        List<Product> products = List.of(testProduct);
        when(productRepository.findByStockQuantityLessThan(threshold)).thenReturn(products);
        
        // When
        List<ProductResponse> result = productService.findLowStockProducts(threshold);
        
        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).stockQuantity()).isEqualTo(testProduct.getStockQuantity());
        
        verify(productRepository).findByStockQuantityLessThan(threshold);
    }
    
    @Test
    @DisplayName("Should return empty list when no products found by name")
    void findProductsByName_WithNoMatches_ShouldReturnEmptyList() {
        // Given
        String productName = "NonExistent";
        when(productRepository.findByNameContainingIgnoreCase(productName)).thenReturn(List.of());
        
        // When
        List<ProductResponse> result = productService.findProductsByName(productName);
        
        // Then
        assertThat(result).isEmpty();
        verify(productRepository).findByNameContainingIgnoreCase(productName);
    }
    
    @Test
    @DisplayName("Should handle null price gracefully")
    void changePrice_WithNullPrice_ShouldThrowException() {
        // Given
        Long productId = 1L;
        UpdatePriceRequest invalidRequest = new UpdatePriceRequest(null);
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        
        // When & Then
        assertThatThrownBy(() -> productService.changePrice(productId, invalidRequest))
            .isInstanceOf(IllegalArgumentException.class);
        
        verify(productRepository).findById(productId);
    }
}