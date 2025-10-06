package com.dietiestates.user_service.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import com.dietiestates.shared.security.JwtAuthFilter;;

@Configuration
public class SecurityConfig {
  private final JwtAuthFilter jwtFilter;

  public SecurityConfig(JwtAuthFilter jwtFilter) { this.jwtFilter = jwtFilter; }

  @Bean
  SecurityFilterChain api(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable()) // per v1; potrai abilitare double-submit sul refresh
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/v1/user/login", "/api/v1/user/refresh", "/api/v1/user/logout", "/api/v1/user/register", "/api/v1/user/jwks.json"
            ).permitAll()
            .requestMatchers("/api/v1/user/favourites", "/api/v1/user/addfavourite").authenticated()
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}
