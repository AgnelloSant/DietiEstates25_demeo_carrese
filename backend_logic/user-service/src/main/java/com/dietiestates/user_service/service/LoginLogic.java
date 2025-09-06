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
        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
        if (userOpt.isEmpty()) {

            return Optional.empty(); // Utente non trovato

        }

        User user = userOpt.get();


        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return Optional.empty(); // Password errata
        }

        String role = user.getRole();

        // Emetti access + refresh (cookie) con binding UA/IP (hash)
        var tokens = authService.issueTokens(user.getId(), role, uaHash, ipHash);
        System.out.println("Access Token: " + tokens.access());

        return Optional.of(new LoginResult(user, tokens));
    }
}
