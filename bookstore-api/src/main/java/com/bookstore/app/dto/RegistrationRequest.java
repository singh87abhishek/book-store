package com.bookstore.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * RegistrationRequest
 */
@Data 
public class RegistrationRequest {
    @NotBlank 
    private String username;

    @NotBlank 
    private String password;

    @Email 
    @NotBlank 
    private String email;

}
