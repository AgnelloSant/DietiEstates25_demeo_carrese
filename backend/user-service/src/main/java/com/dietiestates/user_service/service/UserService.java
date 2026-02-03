package com.dietiestates.user_service.service;

import com.dietiestates.user_service.dto.PswChangeRequest;
import com.dietiestates.user_service.dto.PublicUserDTO;
import com.dietiestates.user_service.dto.UpdateProfileRequest;
import com.dietiestates.user_service.dto.UserProfileDTO;
import com.dietiestates.user_service.model.User;
import com.dietiestates.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public PublicUserDTO getPublicProfile(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return new PublicUserDTO(user.getId(), user.getEmail(), user.getRole());
    }

    public UserProfileDTO getProfile(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        UserProfileDTO userProfileDTO = new UserProfileDTO(user.getName(), user.getEmail(), user.getPhone(),
                user.getRole(), user.getProvider());
        return userProfileDTO;
    }

    public PublicUserDTO getProfileByEmail(String email) {
        System.out.println("Request arrived at the service: " + email);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        System.out.println("User found: " + user.getName() + " " + user.getPhone() + " " + user.getRole());
        return new PublicUserDTO(user.getId(), user.getEmail(), user.getRole());
    }

    public void updateProfile(Long id, UpdateProfileRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setName(request.name());
        user.setPhone(request.phone());
        userRepository.save(user);
    }

    public void changePassword(PswChangeRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getOldPsw(), user.getPassword())) {
            throw new RuntimeException("Invalid old password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPsw()));
        userRepository.save(user);
    }
}
