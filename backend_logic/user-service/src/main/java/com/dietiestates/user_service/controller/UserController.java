package com.dietiestates.user_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dietiestates.user_service.dto.LoginRequest;
import com.dietiestates.user_service.dto.RegisterRequest;
import com.dietiestates.user_service.service.LoginLogic;
import com.dietiestates.user_service.service.PswChangeLogic;
import com.dietiestates.user_service.service.RegistrationLogic;
import com.dietiestates.user_service.dto.PswChangeRequest;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    private LoginLogic loginLogic;

    @Autowired
    private RegistrationLogic registerLogic;

    @Autowired
    private PswChangeLogic pswChangeLogic;
    
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        boolean success = loginLogic.userLogin(loginRequest);
        if (success) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        boolean success = registerLogic.userRegister(registerRequest);
        if (!success) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed");
        }
        return ResponseEntity.ok("Registration successful");
    }

    @PutMapping("/newpsw")
    public ResponseEntity<String> newPsw(@RequestBody PswChangeRequest pswChangeRequest) {
        boolean success = pswChangeLogic.userPswChange(pswChangeRequest);
        if (!success) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password change failed");
        }
        return ResponseEntity.ok("Password change successful");
    }


    //@PostMapping("/")
}
