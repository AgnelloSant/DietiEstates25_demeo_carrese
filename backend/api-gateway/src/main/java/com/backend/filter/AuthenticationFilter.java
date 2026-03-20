package com.backend.filter;

import com.backend.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator validator;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            boolean isSecured = validator.isSecured.test(exchange.getRequest());
            System.out.println("DEBUG Gateway checks path: " + path + " | isSecured=" + isSecured);

            if (!isSecured) {
                return chain.filter(exchange);
            }

            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                System.out.println("DEBUG Gateway: Missing auth header for secured path: " + path);
                exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                authHeader = authHeader.substring(7);
            }

            try {
                System.out.println("DEBUG Gateway: Token received: " + authHeader);
                Claims claims = jwtUtil.validateToken(authHeader); // validazione token

                String email = claims.getSubject();
                Object idObj = claims.get("id");
                Object roleObj = claims.get("role");

                String id = idObj != null ? String.valueOf(idObj) : "";
                String role = roleObj != null ? String.valueOf(roleObj) : "USER";

                System.out.println(
                        "DEBUG Gateway: Token validated. Email: " + email + ", ID: " + id + ", Role: " + role);

                // Add header to request e creiamo mutazione
                exchange = exchange.mutate()
                        .request(exchange.getRequest().mutate()
                                .header("X-User-Email", email)
                                .header("X-User-Id", id)
                                .header("X-User-Role", role)
                                .build())
                        .build();

            } catch (Exception e) {
                System.out.println("DEBUG Gateway: Token verification failed: " + e.getMessage());
                e.printStackTrace();
                exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            return chain.filter(exchange);
        });
    }

    public static class Config {

    }
}
