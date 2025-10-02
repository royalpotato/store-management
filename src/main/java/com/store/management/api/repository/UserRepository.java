package com.store.management.api.repository;

import com.store.management.api.model.Role;
import com.store.management.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    List<User> findByRole(Role role);
    
    List<User> findByEnabledTrue();
    
    @Query("SELECT u FROM User u WHERE u.enabled = true AND u.role IN :roles")
    List<User> findByEnabledTrueAndRoleIn(List<Role> roles);
    
    long countByEnabledTrue();
}