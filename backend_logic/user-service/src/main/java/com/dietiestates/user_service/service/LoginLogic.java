package com.dietiestates.user_service.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dietiestates.user_service.dto.LoginRequest;
import com.dietiestates.user_service.model.User;
import com.dietiestates.user_service.rep.UserRepository;

@Service
public class LoginLogic {

    @Autowired
    private UserRepository userRepository;

    public User userLogin(LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());

        if (userOpt.isEmpty()) {
            return null; // Utente non trovato
        }

        User user = userOpt.get();

        if (!user.getPassword().equals(loginRequest.getPassword())) {
            return null; // Password errata
        }

        return user; // ritorna l'utente trovato
    }
}
