package com.bookstore.app.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.bookstore.app.dto.AuthResponse;
import com.bookstore.app.dto.RegistrationRequest;
import com.bookstore.app.entity.User;
import com.bookstore.app.repository.UserRepository;
import com.bookstore.app.service.AuthService;



@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock  PasswordEncoder passwordEncoder;

    @InjectMocks 
    private AuthService authService;

    @Test 
    public void testRegisterUser_Success() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        request.setEmail("testuser@example.com");

        // Mock the behavior of userRepository
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("testuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthResponse response = authService.registerUser(request);
        assertThat(response.getUserName()).isEqualTo("testuser");
    }

    @Test 
    void register_throwsException_whenUsernameExists() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        request.setEmail("testuser@example.com");

        // Mock the behavior of userRepository
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Verify that an exception is thrown
        assertThatThrownBy(() -> authService.registerUser(request))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Username already exists");
    }
}
