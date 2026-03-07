package com.dietiestates.user_service.controller;

import com.dietiestates.user_service.dto.LoginRequest;
import com.dietiestates.user_service.dto.LoginResponse;
import com.dietiestates.user_service.dto.PswChangeRequest;
import com.dietiestates.user_service.dto.PublicUserDTO;
import com.dietiestates.user_service.dto.RegisterRequest;
import com.dietiestates.user_service.model.User;
import com.dietiestates.user_service.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService service;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public String addNewUser(
            @RequestBody @jakarta.validation.Valid RegisterRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        return service.saveUser(user);
    }

    @PostMapping("/token")
    public String getToken(@RequestBody LoginRequest authRequest) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        if (authenticate.isAuthenticated()) {
            return service.generateToken(authRequest.getEmail());
        } else {
            throw new RuntimeException("invalid access");
        }
    }

    @PostMapping("/password")
    public ResponseEntity<Void> changePassword(@RequestBody PswChangeRequest request) {
        service.changePassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpServletRequest httpReq) {
        try {
            String uaHash = sha256(httpReq.getHeader("User-Agent"));
            String ipHash = sha256(httpReq.getRemoteAddr());

            var resultOpt = service.userLogin(req, uaHash, ipHash);
            if (resultOpt.isEmpty()) {
                return ResponseEntity.status(401).build();
            }
            var result = resultOpt.get();

            PublicUserDTO publicUser = new PublicUserDTO(
                    result.user().getId(),
                    result.user().getEmail(),
                    result.user().getRole());

            LoginResponse response = new LoginResponse(
                    result.tokens().access(),
                    publicUser);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, result.tokens().refreshCookie().toString())
                    .body(response);
        } catch (Throwable e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("DEBUG: " + e.getClass().getName() + " message: " + e.getMessage());
        }
    }

    @GetMapping("/validate")
    public String validateToken(@RequestParam("token") String token) {
        service.validateToken(token);
        return "Token is valid";
    }

    private String sha256(String input) {
        if (input == null)
            return "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
