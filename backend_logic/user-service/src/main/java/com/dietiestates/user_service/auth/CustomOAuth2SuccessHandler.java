package com.dietiestates.user_service.auth;

import com.dietiestates.user_service.model.User;
import com.dietiestates.user_service.rep.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * Handler chiamato quando login OAuth2 (Google/Facebook) ha successo.
 * 
 * Flusso:
 * 1. Utente fa login con Google/Facebook/git
 * 2. Questo handler riceve i dati dall'OAuth provider
 * 3. Cerca o crea l'utente nel database
 * 4. Genera JWT usando il tuo AuthService esistente
 * 5. Redirect al frontend con il token
 */
@Component
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final AuthService authService;

    public CustomOAuth2SuccessHandler(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

  @Override
public void onAuthenticationSuccess(HttpServletRequest request,
                                   HttpServletResponse response,
                                   Authentication authentication) throws IOException {
    
    // Estrai info dall'utente OAuth2 (Google, Facebook o GitHub)
    OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
    
    // Determina provider PRIMA di tutto
    String provider = extractProvider(request);
    
    //  Leggi valori grezzi dagli attributi OAuth2
    String rawEmail = oauth2User.getAttribute("email");
    String rawName = oauth2User.getAttribute("name");
    String login = oauth2User.getAttribute("login");  // Username GitHub
    
    //Ottieni ID univoco del provider
    String providerId;
    if ("google".equals(provider)) {
        providerId = oauth2User.getAttribute("sub");
    } else if ("facebook".equals(provider) || "github".equals(provider)) {
        Object idObj = oauth2User.getAttribute("id");
        providerId = idObj != null ? idObj.toString() : null;
    } else {
        providerId = oauth2User.getAttribute("id") != null 
            ? oauth2User.getAttribute("id").toString() 
            : oauth2User.getAttribute("sub");
    }
    
    //  Processa email (gestisce GitHub email privata)
    String email;
    if (rawEmail == null || rawEmail.isEmpty()) {
        if ("github".equals(provider)) {
            if (login != null && !login.isEmpty()) {
                email = login + "@users.noreply.github.com";
                System.out.println("⚠️ GitHub email privata, uso placeholder: " + email);
            } else {
                email = "user" + providerId + "@users.noreply.github.com";
                System.out.println("⚠️ GitHub email e login privati, uso ID: " + email);
            }
        } else {
            // Fallback per altri provider senza email
            email = "user" + providerId + "@unknown.com";
        }
    } else {
        email = rawEmail;
    }
    
    //  Processa name (gestisce GitHub senza nome)
    String name;
    if (rawName == null || rawName.isEmpty()) {
        if ("github".equals(provider) && login != null && !login.isEmpty()) {
            name = login;  // Usa username GitHub
        } else {
            name = email.split("@")[0];  // Fallback: parte prima della @
        }
    } else {
        name = rawName;
    }
    
    System.out.println("🟢 OAuth2 Login Success:");
    System.out.println("   Provider: " + provider);
    System.out.println("   Email: " + email);
    System.out.println("   Name: " + name);
    System.out.println("   Provider ID: " + providerId);

    //  email e name ora sono final (non più modificati dopo)
    // Cerca utente esistente o creane uno nuovo
    final String finalEmail = email;  // Per la lambda
    final String finalName = name;    // Per la lambda
    
    User user = userRepository.findByEmail(finalEmail)
        .orElseGet(() -> createOAuthUser(finalEmail, finalName, providerId, provider));

    // Genera JWT usando il TUO AuthService esistente
    String uaHash = hashString(request.getHeader("User-Agent"));
    String ipHash = hashString(getClientIP(request));
    
    AuthService.Tokens tokens = authService.issueTokens(
        user.getId(),
        user.getRole() != null ? user.getRole() : "USER",
        uaHash,
        ipHash
    );

    // Imposta refresh token come cookie
    response.addHeader("Set-Cookie", tokens.refreshCookie().toString());

    // Redirect al frontend con access token
    String redirectUrl = UriComponentsBuilder
        .fromUriString("http://localhost:5173/oauth2/callback")
        .queryParam("token", tokens.access())
        .build()
        .toUriString();

    System.out.println("🟢 Redirecting to frontend: " + redirectUrl);
    getRedirectStrategy().sendRedirect(request, response, redirectUrl);
}


    /**
     * Crea nuovo utente OAuth2 nel database.
     * Viene chiamato solo al primo login dell'utente con Google/Facebook.
     */
    private User createOAuthUser(String email, String name, String providerId, String provider) {
        User user = new User();
        
        // Campi base
        user.setEmail(email);
        user.setName(name != null ? name : email.split("@")[0]); // Fallback se name è null
        user.setRole("USER");  // Ruolo di default
        user.setPassword(null); // Nessuna password per utenti OAuth
        user.setPhone(null);    // Phone può essere null
        
        // Campi OAuth2
        user.setProvider(provider);  // "google" o "facebook"
        
        // Salva ID specifico del provider
        if ("google".equals(provider)) {
            user.setGoogleId(providerId);
        } else if ("facebook".equals(provider)) {
            user.setFacebookId(providerId);
        }else if ("github".equals(provider)) {  
        user.setGithubId(providerId);
    }
        
        User saved = userRepository.save(user);
        
        System.out.println(" Created new " + provider + " user:");
        System.out.println("   Email: " + email);
        System.out.println("   User ID: " + saved.getId());
        
        return saved;
    }

    /**
     * Estrae il provider (google/facebook) dalla URL della richiesta
     */
    private String extractProvider(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri.contains("google")) return "google";
        if (uri.contains("facebook")) return "facebook";
        if (uri.contains("github")) return "github"; 
        return "unknown";
    }

    /**
     * Ottiene l'indirizzo IP del client (gestisce anche proxy/load balancer)
     */
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        // Se passa attraverso proxy, prende il primo IP
        return xfHeader.split(",")[0];
    }

    /**
     * Hash SHA-256 per fingerprinting (come fai già per UA e IP)
     */
    private String hashString(String input) {
        if (input == null) return "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            return "";
        }
    }
}
