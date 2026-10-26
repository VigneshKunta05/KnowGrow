
package com.knowgrow.knowgrow.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthUserRepository repository;
    private final BCryptPasswordEncoder encoder =
            new BCryptPasswordEncoder();

    public AuthController(AuthUserRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request) {

        if (request.name() == null || request.name().isBlank()
                || request.email() == null
                || request.password() == null
                || request.password().length() < 8
                || request.role() == null
                || request.role().isBlank()) {
            return ResponseEntity.badRequest().body(
                    Map.of("message",
                            "Provide name, email, role and a password of at least 8 characters."));
        }

        String email = request.email().trim();

        if (repository.existsByEmailIgnoreCase(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message",
                            "An account with this email already exists."));
        }

        AuthUser user = new AuthUser();
        user.setName(request.name().trim());
        user.setEmail(email);
        user.setRole(request.role());
        user.setPasswordHash(encoder.encode(request.password()));

        AuthUser saved = repository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                        "message", "Account created successfully.",
                        "user", Map.of(
                                "id", saved.getId(),
                                "name", saved.getName(),
                                "email", saved.getEmail(),
                                "role", saved.getRole()
                        )
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request) {

        if (request.email() == null || request.password() == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Email and password are required."));
        }

        AuthUser user = repository
                .findByEmailIgnoreCase(request.email().trim())
                .orElse(null);

        if (user == null
                || !encoder.matches(
                request.password(), user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message",
                            "Invalid email or password."));
        }

        return ResponseEntity.ok(
                Map.of(
                        "message", "Login successful.",
                        "user", Map.of(
                                "id", user.getId(),
                                "name", user.getName(),
                                "email", user.getEmail(),
                                "role", user.getRole()
                        )
                )
        );
    }

    public record RegisterRequest(
            String name, String email, String role, String password) {}

    public record LoginRequest(String email, String password) {}
}
