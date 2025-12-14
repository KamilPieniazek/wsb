package com.example.wsb.authentication;


import com.example.wsb.authentication.model.LoginRequest;
import com.example.wsb.authentication.model.LoginResponse;
import com.example.wsb.authentication.repository.AdminUserRepository;
import com.example.wsb.exception.NotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AdminUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        var user = repository.findByEmail(request.email())
                .orElseThrow(() -> new NotFoundException("Invalid credentials"));

        if (!user.isEnabled() || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new NotFoundException("Invalid credentials");
        }

        return new LoginResponse(jwtService.generateAccessToken(user));
    }
}
