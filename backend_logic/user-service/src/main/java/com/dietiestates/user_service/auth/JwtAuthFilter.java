package com.dietiestates.user_service.auth;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
  private final JwtService jwt;

  public JwtAuthFilter(JwtService jwt) { this.jwt = jwt; }

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws java.io.IOException, jakarta.servlet.ServletException {

    String h = req.getHeader(HttpHeaders.AUTHORIZATION);
    if (h != null && h.startsWith("Bearer ")) {
      String token = h.substring(7);
      try {
        Claims c = jwt.verify(token);
        String role = (String) c.get("role");
        var auth = new AbstractAuthenticationToken(
            role != null ? List.of(new SimpleGrantedAuthority("ROLE_"+role)) : List.of()) {
          @Override public Object getCredentials() { return token; }
          @Override public Object getPrincipal() { return c.getSubject(); }
          @Override public boolean isAuthenticated() { return true; }
        };
        SecurityContextHolder.getContext().setAuthentication(auth);
      } catch (Exception ignored) { /* 401 gestita a valle */ }
    }
    chain.doFilter(req, res);
  }
}
