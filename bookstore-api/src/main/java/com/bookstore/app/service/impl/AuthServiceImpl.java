package com.bookstore.app.service.impl;

import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bookstore.app.dto.AuthRequest;
import com.bookstore.app.dto.AuthResponse;
import com.bookstore.app.dto.RegistrationRequest;
import com.bookstore.app.entity.User;
import com.bookstore.app.exception.BadRequestException;
import com.bookstore.app.repository.UserRepository;
import com.bookstore.app.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse registerUser(RegistrationRequest request) {
        log.debug("Registering user: {}", request.getUsername());
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            log.error("Registration Failed : Username cannot be null or empty");
            throw new BadRequestException("Username cannot be null or empty");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            log.error("Registration Failed : Username already exists: {}", request.getUsername());
            throw new BadRequestException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            log.error("Registration Failed : Email already exists: {}", request.getEmail());
            throw new BadRequestException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of("ROLE_USER")); // default role
        userRepository.save(user);

        log.debug("User registered successfully: {}", user.getUsername());
        return new AuthResponse(user.getUsername());
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        log.debug("Login attempt for user: {}", request.getUserName());
        User user = userRepository.findByUsername(request.getUserName())
                .orElseThrow(() -> {
                    log.error("Login Failed : User not found: {}", request.getUserName());
                    return new BadRequestException("Invalid username or password");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.error("Login Failed : Invalid password for user: {}", request.getUserName());
            throw new BadRequestException("Invalid username or password");
        }

        log.debug("User logged in successfully: {}", user.getUsername());
        return new AuthResponse(user.getUsername());
    }

}
