package com.bookstore.app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.bookstore.app.constants.UserRole;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity 
@RequiredArgsConstructor 
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    @Bean 
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // allow anonymous POST to login, keep register restricted to ADMIN
                .requestMatchers(HttpMethod.POST, "/bookstore-api/auth/login").permitAll()
                .requestMatchers("/bookstore-api/auth/register").hasRole(UserRole.ADMIN.name())

                // Books: GET allowed for USERS and ADMINS; write operations restricted to ADMIN
                .requestMatchers(HttpMethod.GET, "/bookstore-api/books/**").hasAnyRole(UserRole.ADMIN.name(), UserRole.USER.name())
                .requestMatchers("/bookstore-api/books/**").hasRole(UserRole.ADMIN.name())

                .requestMatchers("/bookstore-api/cart/**").hasAnyRole(UserRole.ADMIN.name(), UserRole.USER.name())
                .requestMatchers("/bookstore-api/orders/**").hasAnyRole(UserRole.ADMIN.name(), UserRole.USER.name())
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> {})
            .authenticationProvider(authenticationProvider());
        return http.build();
    }

    @Bean 
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean 
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
