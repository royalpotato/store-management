package com.store.management.api.service;

import com.store.management.api.dto.AuthResponse;
import com.store.management.api.dto.LoginRequest;
import com.store.management.api.model.User;
import com.store.management.api.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private int jwtExpiration;
    
    public AuthResponse authenticate(LoginRequest request) {
        log.info("Attempting login for user: {}", request.username());
        
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + request.username()));
        
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("Invalid password attempt for user: {}", request.username());
            throw new BadCredentialsException("Invalid credentials");
        }
        
        String token = generateToken(user);
        
        log.info("Successfully authenticated user: {} with role: {}", user.getUsername(), user.getRole());
        
        return new AuthResponse(
                token,
                "Bearer",
                jwtExpiration,
                user.getUsername(),
                user.getRole().name()
        );
    }
    
    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant expiration = now.plus(jwtExpiration, ChronoUnit.SECONDS);
        
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("role", user.getRole().name())
                .claim("userId", user.getId())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(key)
                .compact();
    }
    
    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }
    
    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }
    
    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userId", Long.class);
    }
    
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            log.debug("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
    
    public boolean validateToken(String token) {
        return isTokenValid(token);
    }
    
    public String getUsernameFromToken(String token) {
        return extractUsername(token);
    }
    
    private Claims extractAllClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}