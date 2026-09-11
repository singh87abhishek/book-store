package com.bookstore.app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.app.dto.AuthRequest;
import com.bookstore.app.dto.AuthResponse;
import com.bookstore.app.dto.RegistrationRequest;
import com.bookstore.app.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@RestController 
@RequestMapping ("/bookstore-api/auth")
@RequiredArgsConstructor 
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody RegistrationRequest request) {
        log.debug("Registration request for user: {}", request.getUsername());
        AuthResponse response = authService.registerUser(request);
        log.debug("User registered successfully: {}", response.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping ("/login")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody AuthRequest request) {
        log.debug("Login request for user: {}", request.getUserName());
        AuthResponse response = authService.login(request);
        log.debug("User logged in successfully: {}", response.getUsername());
        return ResponseEntity.ok(response);
    }
}