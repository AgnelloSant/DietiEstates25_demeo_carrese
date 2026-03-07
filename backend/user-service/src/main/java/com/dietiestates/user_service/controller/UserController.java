package com.dietiestates.user_service.controller;

import com.dietiestates.user_service.dto.PswChangeRequest;
import com.dietiestates.user_service.dto.PublicUserDTO;
import com.dietiestates.user_service.dto.UpdateProfileRequest;
import com.dietiestates.user_service.service.UserService;
import com.dietiestates.user_service.dto.UserProfileDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.dietiestates.user_service.model.User;

@RestController
@RequestMapping("")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicUserDTO> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getPublicProfile(id));
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getProfile(@RequestHeader("X-User-Email") String email) {
        System.out.println("Request arrived at the controller: " + email);
        return ResponseEntity.ok(userService.getProfile(email));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProfile(@PathVariable Long id, @RequestBody UpdateProfileRequest request) {
        userService.updateProfile(id, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create/admin")
    public ResponseEntity<Void> createAdmin(@RequestBody User user) {
        userService.createAdmin(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create/agent")
    public ResponseEntity<Void> createAgent(@RequestBody User user) {
        userService.createAgent(user);
        return ResponseEntity.ok().build();
    }
}
