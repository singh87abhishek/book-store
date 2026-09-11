package com.bookstore.app.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.bookstore.app.dto.AuthRequest;
import com.bookstore.app.dto.AuthResponse;
import com.bookstore.app.dto.RegistrationRequest;
import com.bookstore.app.entity.User;
import com.bookstore.app.exception.BadRequestException;
import com.bookstore.app.repository.UserRepository;
import com.bookstore.app.service.impl.AuthServiceImpl;



@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock  PasswordEncoder passwordEncoder;

    @InjectMocks 
    private AuthServiceImpl authService;

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
        assertThat(response.getUsername()).isEqualTo("testuser");
    }

    @Test 
    void register_throwsBadRequestException_whenUsernameExists() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        request.setEmail("testuser@example.com");

        // Mock the behavior of userRepository
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Verify that an exception is thrown
        assertThatThrownBy(() -> authService.registerUser(request))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Username already exists");
    }

    @Test 
    void register_throwsBadRequestException_whenEmailExists() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        request.setEmail("testuser@example.com");

        // Mock the behavior of userRepository
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("testuser@example.com")).thenReturn(true);

        // Verify that an exception is thrown
        assertThatThrownBy(() -> authService.registerUser(request))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Email already exists");
    }

    @Test 
    void login_resturnsUserName_whenCredentialsAreValid() {
        User user = new User();
        user.setUsername("test");
        // stored password should be the encoded value as produced by PasswordEncoder
        user.setPassword("encoded");

        AuthRequest request = new AuthRequest("test", "password");

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encoded")).thenReturn(true);

        AuthResponse response = authService.login(request);
        assertThat(response.getUsername()).isEqualTo("test");
    }

    @Test 
    void login_throwsBadRequestException_whenUserNotFound() {
        AuthRequest request = new AuthRequest("unknown", "password");

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> authService.login(request))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessageContaining("Invalid username or password");
    }


}
