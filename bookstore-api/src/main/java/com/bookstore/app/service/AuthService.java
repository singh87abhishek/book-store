package com.bookstore.app.service;

import com.bookstore.app.dto.AuthRequest;
import com.bookstore.app.dto.AuthResponse;
import com.bookstore.app.dto.RegistrationRequest;

public interface AuthService {

    AuthResponse registerUser(RegistrationRequest request);

    AuthResponse login(AuthRequest request);

}