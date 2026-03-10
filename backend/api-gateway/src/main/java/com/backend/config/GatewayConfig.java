package com.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class GatewayConfig {


    @Value("${USER_SERVICE_URL:http://localhost:8080}")
    private String userServiceUrl;

    @Value("${PROPERTY_SERVICE_URL:http://localhost:8081}")
    private String propertyServiceUrl;

    @Bean
    public RouteLocator customRouteLocator(
            RouteLocatorBuilder builder,
            com.backend.filter.AuthenticationFilter authFilter) {

        return builder.routes()
                // 1. ROTTE PUBBLICHE
                .route("auth-social-route", r -> r
                        .path("/user/oauth2/**", "/user/login/**", "/user/auth/**")
                        .uri(userServiceUrl))

                // 2. API UTENTE PROTETTE
                .route("user-service", r -> r
                        .path("/user/**")
                        .filters(f -> f.filter(authFilter.apply(
                                new com.backend.filter.AuthenticationFilter.Config())))
                        .uri(userServiceUrl))

                // 3. PROPERTY SERVICE
                .route("property-service", r -> r
                        .path("/properties/**")
                        .filters(f -> f.filter(authFilter.apply(
                                new com.backend.filter.AuthenticationFilter.Config())))
                        .uri(propertyServiceUrl))

                .build();
    }

        @Bean
        public CorsWebFilter corsWebFilter() {
                CorsConfiguration corsConfig = new CorsConfiguration();
                corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:3000", "*"));
                corsConfig.setMaxAge(3600L);
                corsConfig.addAllowedMethod("*");
                corsConfig.addAllowedHeader("*");
                corsConfig.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", corsConfig);

                return new CorsWebFilter(source);
        }
}
