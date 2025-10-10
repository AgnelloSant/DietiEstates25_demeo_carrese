package com.dietiestates.user_service.controller;

import com.dietiestates.user_service.dto.FavouriteListRequest;
import com.dietiestates.user_service.dto.LoginRequest;
import com.dietiestates.user_service.dto.LoginResponse;
import com.dietiestates.user_service.dto.PswChangeRequest;
import com.dietiestates.user_service.dto.PublicUserDTO;
import com.dietiestates.user_service.dto.RegisterRequest;
import com.dietiestates.user_service.dto.UpdateProfileRequest;
import com.dietiestates.user_service.model.User;
import com.dietiestates.user_service.service.FavouritesLogic;
import com.dietiestates.user_service.service.LoginLogic;
import com.dietiestates.user_service.service.PswChangeLogic;
import com.dietiestates.user_service.service.RegistrationLogic;
import com.dietiestates.user_service.service.UpdateProfileLogic;
import com.dietiestates.user_service.auth.AuthService;
import com.dietiestates.shared.dto.PropertySearchDTO;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.dietiestates.shared.security.JwtProperties;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    // === DEPENDENCY INJECTION (usa il tuo @Service esistente) ===
    private final LoginLogic loginLogic;
    private final RegistrationLogic registerLogic;
    private final PswChangeLogic pswChangeLogic;
    private final FavouritesLogic favouritesLogic;
    private final AuthService authService;
    private final JwtProperties jwtProps;
private final UpdateProfileLogic updateProfileLogic;

    public UserController(
            LoginLogic loginLogic,
            RegistrationLogic registerLogic,
            PswChangeLogic pswChangeLogic,
            FavouritesLogic favouritesLogic,
            AuthService authService,
            JwtProperties jwtProps,
            UpdateProfileLogic updateProfileLogic
    ) {
        this.loginLogic = loginLogic;
        this.registerLogic = registerLogic;
        this.pswChangeLogic = pswChangeLogic;
        this.favouritesLogic = favouritesLogic;
        this.authService = authService;
        this.jwtProps = jwtProps;
        this.updateProfileLogic=updateProfileLogic;
    }


    // === LOGIN ===
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req, HttpServletRequest httpReq) {
        String uaHash = sha256(httpReq.getHeader("User-Agent"));
        String ipHash = sha256(httpReq.getRemoteAddr());

        var resultOpt = loginLogic.userLogin(req, uaHash, ipHash);
        if (resultOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        var result = resultOpt.get();

        PublicUserDTO publicUser = new PublicUserDTO(
        result.user().getId(),
        result.user().getEmail(),
        result.user().getRole()
    );

    LoginResponse response = new LoginResponse(
        result.tokens().access(),
        publicUser
    );

    return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, result.tokens().refreshCookie().toString())
            .body(response);
    }


//  Ottieni profilo completo
@GetMapping("/profile")
public ResponseEntity<?> getProfile(@AuthenticationPrincipal String userId) {
    if (userId == null || "anonymousUser".equals(userId)) {
        return ResponseEntity.status(401).build();
    }
    
    Long uid = Long.valueOf(userId);
    var userOpt = updateProfileLogic.getUserProfile(uid);
    
    if (userOpt.isEmpty()) {
        return ResponseEntity.status(404).body(Map.of("error", "User not found"));
    }
    
    User user = userOpt.get();
    
    // Ritorna DTO pulito (senza password)
    Map<String, Object> profile = Map.of(
        "id", user.getId(),
        "name", user.getName() != null ? user.getName() : "",
        "email", user.getEmail(),
        "phone", user.getPhone() != null ? user.getPhone() : "",
        "role", user.getRole(),
        "provider", user.getProvider() != null ? user.getProvider() : "local"
    );
    
    return ResponseEntity.ok(profile);
}

//  Aggiorna profilo
@PutMapping("/profile")
public ResponseEntity<?> updateProfile(
    @AuthenticationPrincipal String userId,
    @RequestBody UpdateProfileRequest request
) {
    if (userId == null || "anonymousUser".equals(userId)) {
        return ResponseEntity.status(401).build();
    }
    
    Long uid = Long.valueOf(userId);
    boolean success = updateProfileLogic.updateProfile(uid, request);
    
    if (!success) {
        return ResponseEntity.status(400).body(Map.of("error", "Update failed"));
    }
    
    return ResponseEntity.ok(Map.of("message", "Profile updated successfully"));
}




    // === REFRESH ===
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest httpReq) {
        String refresh = readRefreshCookie(httpReq);
        if (refresh == null || refresh.isBlank()) {
            return ResponseEntity.status(401).body(Map.of("error", "missing_refresh"));
        }
        try {
            var tokens = authService.rotate(
                    refresh,
                    sha256(httpReq.getHeader("User-Agent")),
                    sha256(httpReq.getRemoteAddr())
            );
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, tokens.refreshCookie().toString())
                    .body(Map.of("accessToken", tokens.access()));
        } catch (IllegalStateException reuse) {
            return ResponseEntity.status(401).body(Map.of("error", "refresh_reuse"));
        }
    }

    // === LOGOUT ===
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest httpReq) {
        String refresh = readRefreshCookie(httpReq);
        if (refresh != null && !refresh.isBlank()) {
            authService.logout(refresh);
        }
        ResponseCookie cleared = ResponseCookie.from(jwtProps.getRefreshCookieName(), "")
                .httpOnly(true)
                .secure(jwtProps.isRefreshCookieSecure())
                .sameSite(jwtProps.getRefreshCookieSameSite())
                .path(jwtProps.getRefreshCookiePath())
                .maxAge(0)
                .build();
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cleared.toString())
                .build();
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        boolean success = registerLogic.userRegister(registerRequest);
        if (!success) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed");
        return ResponseEntity.ok("Registration successful");
    }

    @PutMapping("/newpsw")
    public ResponseEntity<String> newPsw(@RequestBody PswChangeRequest pswChangeRequest) {
        boolean success = pswChangeLogic.userPswChange(pswChangeRequest);
        if (!success) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password change failed");
        return ResponseEntity.ok("Password change successful");
    }

    @GetMapping("/favourites")
    public ResponseEntity<List<PropertySearchDTO>> getFavourites(@AuthenticationPrincipal Object principal) {
        if (principal == null || "anonymousUser".equals(principal)) {
            return ResponseEntity.status(401).build(); // non loggato
        }


        Long userId = Long.valueOf(principal.toString());
        var favourites = favouritesLogic.getFavouriteProperties(userId);
        return ResponseEntity.ok(favourites);
    }

    @PostMapping("/addfavourite")
    public ResponseEntity<Boolean> addFavourite(@AuthenticationPrincipal String userId, @RequestBody FavouriteListRequest favRequest) {

        Long propId = favRequest.getIdProp();
        Long userid = Long.valueOf(userId); 
        favouritesLogic.addFavouriteProperty(userid, propId);
        return ResponseEntity.ok(true);
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("OK");
    }

    // === UTILITIES ===
    private String readRefreshCookie(HttpServletRequest req) {
        if (req.getCookies() == null) return null;
        for (Cookie c : req.getCookies()) {
            if (c.getName().equals(jwtProps.getRefreshCookieName())) return c.getValue();
        }
        return null;
    }

    private static String sha256(String s) {
        if (s == null) return "";
        try {
            var md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(s.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            return "";
        }
    }
}
