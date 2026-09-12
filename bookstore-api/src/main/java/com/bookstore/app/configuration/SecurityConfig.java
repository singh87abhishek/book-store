package com.bookstore.app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/bookstore-api/auth/login").permitAll()
                .requestMatchers("/bookstore-api/auth/register").hasRole("ADMIN")
                // Books: GET allowed for USERS and ADMINS; write operations restricted to ADMIN
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/bookstore-api/books/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/bookstore-api/books/**").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/bookstore-api/books/**").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/bookstore-api/books/**").hasRole("ADMIN")
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
