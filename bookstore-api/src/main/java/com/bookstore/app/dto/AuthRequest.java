package com.bookstore.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * AuthResponse
 */
@Data 
@AllArgsConstructor 
public class AuthRequest {

    @NotBlank(message = "Username is required")
    private String userName;

    @NotBlank(message = "Password is required")
    private String password;
}
