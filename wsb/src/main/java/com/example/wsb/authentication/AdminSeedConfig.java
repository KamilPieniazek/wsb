package com.example.wsb.authentication;

import com.example.wsb.authentication.model.AdminUser;
import com.example.wsb.authentication.repository.AdminUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
class AdminSeedConfig {

    @Bean
    CommandLineRunner seedAdmin(AdminUserRepository repo, PasswordEncoder encoder) {
        return args -> {
            String email = "admin@example.com";
            if (repo.findByEmail(email).isEmpty()) {
                repo.save(AdminUser.builder()
                        .email(email)
                        .passwordHash(encoder.encode("Admin123"))
                        .enabled(true)
                        .build());
            }
        };
    }
}

