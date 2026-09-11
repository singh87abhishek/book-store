package com.bookstore.app.configuration;

import java.util.List;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.bookstore.app.entity.Book;
import com.bookstore.app.entity.User;
import com.bookstore.app.repository.BookRepository;
import com.bookstore.app.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor 
public class DataSeeder implements CommandLineRunner {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override 
    public void run(String... args) throws Exception {
        if(userRepository.count() == 0) {
            // Seed default users
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setEmail("admin@bookstore.com");
            adminUser.setRoles(Set.of("ROLE_ADMIN"));


            User user = new User();
            user.setUsername("Abhi");
            user.setPassword(passwordEncoder.encode("abhi123"));
            user.setEmail("abhishek@bookstore.com");
            user.setRoles(Set.of("ROLE_USER"));

            List<User> users = List.of(adminUser, user);

            userRepository.saveAll(users);
        }

        if(bookRepository.count() == 0) {
            // Seed default books
            bookRepository.saveAll(List.of(
                new Book(null, "Java Programming Basics", "John Doe", 19.99, 5, "A comprehensive guide to Java programming.","https://example.com/java-basics.jpg"),
                new Book(null, "Design Patterns Effects", "Eric Peter", 29.99, 10, "A book about design patterns and its effects.","https://example.com/design-patterns.jpg"),
                new Book(null, "Spring Season in Java", "Jimmy Aderson", 9.99, 7, "A book about the Spring framework in Java.","https://example.com/Spring.jpg")
            ));
        }
    }
}
