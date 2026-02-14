package com.backend.config;

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

        @Bean
        public RouteLocator customRouteLocator(RouteLocatorBuilder builder,
                        com.backend.filter.AuthenticationFilter authFilter) {
                return builder.routes()
                                .route("user-service", r -> r.path("/user/**")
                                                .filters(f -> f.filter(authFilter.apply(
                                                                new com.backend.filter.AuthenticationFilter.Config())))
                                                .uri("http://user-service:8080"))
                                .route("auth-service", r -> r.path("/auth/**")
                                                .filters(f -> f.prefixPath("/user"))
                                                .uri("http://user-service:8080"))
                                .route("property-service", r -> r.path("/properties/**")
                                                .filters(f -> f.filter(authFilter.apply(
                                                                new com.backend.filter.AuthenticationFilter.Config())))
                                                .uri("http://property-service:8081"))
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
