package com.store.management.api.repository;

import com.store.management.api.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByNameContainingIgnoreCase(String name);
    
    List<Product> findByCategory(String category);
    
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    List<Product> findByStockQuantityLessThan(Integer threshold);
    
    Optional<Product> findByNameAndCategory(String name, String category);
    
    @Query("SELECT DISTINCT p.category FROM Product p ORDER BY p.category")
    List<String> findAllCategories();
    
    @Query("SELECT p FROM Product p WHERE p.price >= :minPrice ORDER BY p.price")
    List<Product> findProductsWithMinimumPrice(@Param("minPrice") BigDecimal minPrice);
    
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% OR p.description LIKE %:keyword%")
    List<Product> searchByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category = :category")
    long countByCategory(@Param("category") String category);
    
    @Query("SELECT SUM(p.stockQuantity) FROM Product p")
    Long getTotalStockQuantity();
}