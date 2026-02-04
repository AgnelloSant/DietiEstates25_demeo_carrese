package com.dietiestates.user_service.service;

import com.dietiestates.user_service.model.User;
import com.dietiestates.user_service.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.ResponseCookie;

import java.io.IOException;

@Component
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public CustomOAuth2SuccessHandler(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                       HttpServletResponse response,
                                       Authentication authentication) throws IOException {
        
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String provider = extractProvider(request);
        
        // 1. Calcolo Email
        String tempEmail = oauth2User.getAttribute("email");
        if (tempEmail == null || tempEmail.isEmpty()) {
            String login = oauth2User.getAttribute("login");
            tempEmail = (login != null) ? login + "@users.noreply.github.com" : "oauth2user@dietiestates.com";
        }
        final String finalEmail = tempEmail;

        // 2. Calcolo Nome
        String tempName = oauth2User.getAttribute("name");
        if (tempName == null || tempName.isEmpty()) {
            tempName = finalEmail.split("@")[0];
        }
        final String finalName = tempName; 

        // 3. Estrazione ID univoco del provider
        Object idObj = oauth2User.getAttribute("id");
        if (idObj == null) idObj = oauth2User.getAttribute("sub"); // Google usa 'sub'
        final String providerId = (idObj != null) ? idObj.toString() : null;

        // 4. Ricerca o creazione utente
        User user = userRepository.findByEmail(finalEmail)
                .orElseGet(() -> createOAuthUser(finalEmail, finalName, provider, providerId));

        // 5. Generazione Token tramite il tuo JwtService
        String accessToken = jwtService.generateToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        // 6. Impostazione Cookie Refresh (HttpOnly)
        ResponseCookie refreshCookie = jwtService.createRefreshCookie(refreshToken);
        response.addHeader("Set-Cookie", refreshCookie.toString());

        // 7. Redirect al frontend con Access Token nell'URL
        String redirectUrl = UriComponentsBuilder
                .fromUriString("http://localhost:5173/oauth2/callback")
                .queryParam("token", accessToken)
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    // Metodo privato corretto per mappare i campi del tuo modello User
    private User createOAuthUser(String email, String name, String provider, String providerId) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setRole("USER"); // Ruolo di default
        user.setProvider(provider);
        user.setPassword(null); // Utente social non ha password interna

        // Assegna l'ID al campo corretto in base al provider
        if ("google".equalsIgnoreCase(provider)) {
            user.setGoogleId(providerId);
        } else if ("github".equalsIgnoreCase(provider)) {
            user.setGithubId(providerId);
        } else if ("facebook".equalsIgnoreCase(provider)) {
            user.setFacebookId(providerId);
        }
        
        return userRepository.save(user);
    }

    private String extractProvider(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri.contains("google")) return "google";
        if (uri.contains("facebook")) return "facebook";
        if (uri.contains("github")) return "github";
        return "unknown";
    }
}