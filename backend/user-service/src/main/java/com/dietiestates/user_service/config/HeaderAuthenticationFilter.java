package com.dietiestates.user_service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;

    public HeaderAuthenticationFilter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String userEmail = request.getHeader("X-User-Email");
        System.out.println("DEBUG: HeaderAuthenticationFilter processing request to " + request.getRequestURI());
        System.out.println("DEBUG: Found X-User-Email: " + userEmail);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            System.out.println("DEBUG: UserDetails loaded for " + userEmail + ": " + (userDetails != null));

            if (userDetails != null) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("DEBUG: Authentication set in SecurityContext for " + userEmail);
            } else {
                System.out.println("DEBUG: UserDetails is null for " + userEmail);
            }
        } else if (userEmail == null) {
            // This is expected for public endpoints like /auth/login
            System.out.println("DEBUG: No X-User-Email header found for " + request.getRequestURI());
        } else {
            System.out.println("DEBUG: userEmail (" + userEmail + ") already authenticated (Context: "
                    + SecurityContextHolder.getContext().getAuthentication() + ")");
        }
        filterChain.doFilter(request, response);
    }
}
