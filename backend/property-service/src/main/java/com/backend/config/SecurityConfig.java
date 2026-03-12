package com.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final HeaderAuthenticationFilter headerAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/search", "/search/**", "/getall", "/get/**", "/uploads/**", "/favourites/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/{id:[0-9]+}").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/{id:[0-9]+}/images").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/{id:[0-9]+}/images").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/create")
                        .hasAnyRole("AGENT", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/{id:[0-9]+}/upload")
                        .hasAnyRole("AGENT", "ADMIN")
                        .anyRequest().authenticated())
                .addFilterBefore(headerAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}

    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
        configuration.setAllowedOrigins(java.util.List.of("*"));
        configuration.setAllowedMethods(java.util.List.of("*"));
        configuration.setAllowedHeaders(java.util.List.of("*"));
        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
