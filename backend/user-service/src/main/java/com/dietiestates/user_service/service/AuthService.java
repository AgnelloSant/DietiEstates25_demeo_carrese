package com.dietiestates.user_service.service;

import com.dietiestates.user_service.dto.AuthResult;
import com.dietiestates.user_service.dto.LoginRequest;
import com.dietiestates.user_service.dto.PswChangeRequest;
import com.dietiestates.user_service.dto.TokenPair;
import com.dietiestates.user_service.model.User;
import com.dietiestates.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public Optional<AuthResult> userLogin(LoginRequest req, String ua, String ip) {
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

            if (authenticate.isAuthenticated()) {
                User user = repository.findByEmail(req.getEmail()).orElseThrow();
                String accessToken = jwtService.generateToken(req.getEmail());
                String refreshToken = jwtService.generateRefreshToken(req.getEmail());
                var cookie = jwtService.createRefreshCookie(refreshToken);

                return Optional.of(new AuthResult(user, new TokenPair(accessToken, cookie)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public String saveUser(User credential) {
        credential.setPassword(passwordEncoder.encode(credential.getPassword()));
        repository.save(credential);
        return "user added to the system";
    }

    public String generateToken(String username) {
        return jwtService.generateToken(username);
    }

    public void validateToken(String token) {
        jwtService.validateToken(token);
    }

    public void changePassword(PswChangeRequest request) {
        User user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getOldPsw(), user.getPassword())) {
            throw new RuntimeException("Invalid old password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPsw()));
        repository.save(user);
    }
}
