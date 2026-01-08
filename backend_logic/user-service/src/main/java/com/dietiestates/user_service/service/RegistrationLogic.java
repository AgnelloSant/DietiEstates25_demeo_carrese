package com.dietiestates.user_service.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.dietiestates.user_service.dto.RegisterRequest;
import com.dietiestates.user_service.model.User;
import com.dietiestates.user_service.rep.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class RegistrationLogic {

    @Autowired
    private UserRepository userRepository;
    private JavaMailSender mailSender; //modificare (o creare) mail in application.properties
    private BCryptPasswordEncoder passwordEncoder; 

    public RegistrationLogic(UserRepository userRepository, JavaMailSender mailSender, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean userRegister(RegisterRequest registerRequest) {

        Optional<User> userOpt = userRepository.findByEmail(registerRequest.getEmail());

        if (userOpt.isPresent()) {
            return false; 
        }

        User user = new User(); 
        user.setEmail(registerRequest.getEmail());
        user.setName(registerRequest.getName());
        user.setPhone(registerRequest.getPhone());
        user.setRole(registerRequest.getRole());

        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        userRepository.save(user);
        return true; 
    }}