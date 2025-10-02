package com.store.management.api.controller;

import com.store.management.api.dto.AuthResponse;
import com.store.management.api.dto.LoginRequest;
import com.store.management.api.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final AuthenticationService authenticationService;
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.username());
        
        AuthResponse authResponse = authenticationService.authenticate(loginRequest);
        
        log.info("User {} authenticated successfully", loginRequest.username());
        return ResponseEntity.ok(authResponse);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        log.info("Logout request received");
        return ResponseEntity.ok("Logout successful. Please discard your token.");
    }
}