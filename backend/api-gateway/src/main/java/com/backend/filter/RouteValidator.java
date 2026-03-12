package com.backend.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openApiEndpoints = List.of(
            "/auth/.*",
            "/user/auth/.*",
            "/eureka.*",
            "/properties/search.*",
            "/properties/getall",
            "/properties/\\d+",
            "/properties/search-by-bounds",
            "/properties/\\d+/images",
            "/properties/uploads/.*"
    );

    public Predicate<ServerHttpRequest> isSecured = request -> openApiEndpoints
            .stream()
            .noneMatch(regex -> request.getURI().getPath().matches(regex));
}