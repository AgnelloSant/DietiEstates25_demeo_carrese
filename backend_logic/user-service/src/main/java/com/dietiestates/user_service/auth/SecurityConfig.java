package com.dietiestates.user_service.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import com.dietiestates.shared.security.JwtAuthFilter;

@Configuration
public class SecurityConfig {
    
    private final JwtAuthFilter jwtFilter;
    private final CustomOAuth2SuccessHandler oauth2SuccessHandler;

    public SecurityConfig(JwtAuthFilter jwtFilter, CustomOAuth2SuccessHandler oauth2SuccessHandler) {
        this.jwtFilter = jwtFilter;
        this.oauth2SuccessHandler = oauth2SuccessHandler;
    }

    @Bean
    SecurityFilterChain api(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/v1/user/login",
                    "/api/v1/user/register",
                    "/api/v1/user/refresh",
                    "/api/v1/user/logout",
                    "/api/v1/user/jwks.json",
                    "/oauth2/**",              
                    "/login/oauth2/**"       
                ).permitAll()
                
                .requestMatchers(
                    "/api/v1/user/favourites",
                    "/api/v1/user/addfavourite"
                ).authenticated()
                
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
            
            .oauth2Login(oauth2 -> oauth2
                .successHandler(oauth2SuccessHandler)
                .failureUrl("http://localhost:5173/login?error=oauth")
            );

        return http.build();
    }
}
