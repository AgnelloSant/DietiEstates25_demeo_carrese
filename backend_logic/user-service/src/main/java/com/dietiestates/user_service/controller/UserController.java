package com.dietiestates.user_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dietiestates.user_service.dto.LoginRequest;
import com.dietiestates.user_service.service.LoginLogic;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    private LoginLogic loginLogic;
    
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        boolean success = loginLogic.userLogin(loginRequest);
        if (success) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }

}
