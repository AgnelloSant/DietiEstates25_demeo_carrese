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
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class GatewayConfig {

    @Value("${USER_SERVICE_URL:http://user-service:8080}")
    private String userServiceUrl;

    @Value("${PROPERTY_SERVICE_URL:http://property-service:8081}")
    private String propertyServiceUrl;

    @Value("${ALLOWED_ORIGINS:http://localhost:5173,http://localhost:3000}")
    private String allowedOrigins;

    @Bean
    public RouteLocator customRouteLocator(
            RouteLocatorBuilder builder,
            com.backend.filter.AuthenticationFilter authFilter) {

        return builder.routes()
         .route("user-public-route", r -> r
        .path(
            "/user/auth/**",
            "/user/oauth2/**",
            "/user/login/oauth2/**"
        )
        .uri(userServiceUrl))

                .route("user-service", r -> r
        .path("/user/**")
        .filters(f -> f.filter(authFilter.apply(
                new com.backend.filter.AuthenticationFilter.Config())))
        .uri(userServiceUrl))

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

        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        corsConfig.setAllowedOriginPatterns(origins);
        corsConfig.setMaxAge(3600L);
        corsConfig.addAllowedMethod("*");
        corsConfig.addAllowedHeader("*");
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}