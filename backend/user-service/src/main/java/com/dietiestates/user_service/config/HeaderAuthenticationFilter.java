package com.dietiestates.user_service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;

    public HeaderAuthenticationFilter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. OTTIENI IL PATH DELLA RICHIESTA
        String path = request.getServletPath();
        
        // 2. LOG DI DEBUG PER IL PATH
        System.out.println("DEBUG: HeaderAuthenticationFilter checking path: " + path);

        if (path.contains("/oauth2") || path.contains("/login/oauth2")) {
            System.out.println("DEBUG: Skipping HeaderAuthenticationFilter for OAuth2 path");
            filterChain.doFilter(request, response);
            return;
        }

        String userEmail = request.getHeader("X-User-Email");
        System.out.println("DEBUG: Found X-User-Email: " + userEmail);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                
                if (userDetails != null) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("DEBUG: Authentication set in SecurityContext for " + userEmail);
                }
            } catch (UsernameNotFoundException e) {
                // Gestisce il caso in cui l'utente nell'header non esiste nel DB
                System.out.println("DEBUG: User not found in DB for email: " + userEmail);
            }
        } else {
            System.out.println("DEBUG: No action taken (userEmail null or already authenticated)");
        }

        filterChain.doFilter(request, response);
    }
}