package com.dietiestates.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.dietiestates.user_service.dto.LoginRequest;
import com.dietiestates.user_service.model.User;
import com.dietiestates.user_service.rep.UserRepository;

public class LoginLogic {
    public boolean userLogin(LoginRequest loginRequest){
        
        // Logic to authenticate user
        User user = UserRepository.findByEmail(loginRequest.getEmail());
        if (user == null) {
            return false; // User not found
        }
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            return false; // Incorrect password
        }
        // Logic to generate JWT token

        return true; // Placeholder return value
    }
}



