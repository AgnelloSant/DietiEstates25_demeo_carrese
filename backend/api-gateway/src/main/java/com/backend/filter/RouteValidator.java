package com.backend.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;

@Component
public class RouteValidator {

        public static final List<String> openApiEndpoints = List.of(
                        "/auth/.*", // Matches /auth/register, /auth/login, etc.
                        "/user/auth/.*",
                        "/eureka",
                        "/properties/search.*", // Matches /properties/search, search-by-bounds, etc.
                        "/properties/getall",
                        "/properties/\\d+",
                        "/properties/search-by-bounds",
                        "/properties/\\d+/images", // Matches /properties/1/images
                        "/properties/uploads/.*" // Matches /properties/uploads/property_1/image.jpg
        );

        public Predicate<ServerHttpRequest> isSecured = request -> openApiEndpoints
                        .stream()
                        .noneMatch(regex -> request.getURI().getPath().matches(regex)
                                        || request.getURI().getPath().startsWith(regex.replace(".*", "")));
}
