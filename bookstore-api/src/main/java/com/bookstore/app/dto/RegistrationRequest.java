package com.bookstore.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * RegistrationRequest
 */
@Data 
public class RegistrationRequest {
    @NotBlank(message = "UserName is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
 
    @NotBlank(message = "Password is required")
    @Size(min = 4, message = "Password must be at least of 4 characters")
    private String password;
    
}
