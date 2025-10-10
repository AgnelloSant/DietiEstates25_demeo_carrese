package com.dietiestates.user_service.service;

import com.dietiestates.user_service.dto.UpdateProfileRequest;
import com.dietiestates.user_service.model.User;
import com.dietiestates.user_service.rep.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UpdateProfileLogic {

    private final UserRepository userRepository;

    public UpdateProfileLogic(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Aggiorna nome e telefono dell'utente
     */
    public boolean updateProfile(Long userId, UpdateProfileRequest request) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        
        // Aggiorna solo se i campi non sono null/vuoti
        if (request.name() != null && !request.name().isBlank()) {
            user.setName(request.name());
        }
        
        if (request.phone() != null && !request.phone().isBlank()) {
            user.setPhone(request.phone());
        }
        
        userRepository.save(user);
        return true;
    }

    /**
     * Ottiene i dati completi dell'utente per il profilo
     */
    public Optional<User> getUserProfile(Long userId) {
        return userRepository.findById(userId);
    }
}
