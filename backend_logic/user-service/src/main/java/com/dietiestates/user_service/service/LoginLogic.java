package com.dietiestates.user_service.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dietiestates.user_service.dto.LoginRequest;
import com.dietiestates.user_service.model.User;
import com.dietiestates.user_service.rep.UserRepository;

// importa i tuoi file auth:
import com.dietiestates.user_service.auth.AuthService;

@Service
public class LoginLogic {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

<<<<<<< HEAD
    public record LoginResult(User user, AuthService.Tokens tokens) {}

    public LoginLogic(UserRepository userRepository,
                      PasswordEncoder passwordEncoder,
                      AuthService authService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    public Optional<LoginResult> userLogin(LoginRequest loginRequest,
                                           String uaHash,
                                           String ipHash) {
=======
    public User userLogin(LoginRequest loginRequest) {
>>>>>>> main-pulito
        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
        if (userOpt.isEmpty()) {
<<<<<<< HEAD
            return Optional.empty(); // Utente non trovato
=======
            return null; // Utente non trovato
>>>>>>> main-pulito
        }

        User user = userOpt.get();

<<<<<<< HEAD
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return Optional.empty(); // Password errata
        }

        String role = user.getRole();

        // Emetti access + refresh (cookie) con binding UA/IP (hash)
        var tokens = authService.issueTokens(user.getId(), role, uaHash, ipHash);
        System.out.println("Access Token: " + tokens.access());

        return Optional.of(new LoginResult(user, tokens));
=======
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            return null; // Password errata
        }

        return user; // ritorna l'utente trovato
>>>>>>> main-pulito
    }
}
