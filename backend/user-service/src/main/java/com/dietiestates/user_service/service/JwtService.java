package com.dietiestates.user_service.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dietiestates.user_service.repository.UserRepository;
import com.dietiestates.user_service.model.User;

//import java.security.Key;
import java.security.spec.RSAPublicKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
//import java.util.function.Function;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.interfaces.RSAPrivateCrtKey;

@Service
public class JwtService {

    @Autowired
    private UserRepository repository;

    @Value("${jwt.private.key}")
    private String PRIVATE_KEY_PEM;

    @Value("${jwt.cookie.name}")
    private String cookieName;

    @Value("${jwt.cookie.path}")
    private String cookiePath;

    @Value("${jwt.cookie.samesite}")
    private String cookieSameSite;

    @Value("${jwt.cookie.secure}")
    private boolean cookieSecure;

    @Value("${jwt.issuer}")
    private String jwtIssuer;

    @Value("${jwt.audience}")
    private String jwtAudience;

    @Value("${jwt.ttl.access}")
    private long jwtAccessTtl;

    @Value("${jwt.ttl.refresh}")
    private long jwtRefreshTtl;

    public void validateToken(final String token) {
        extractAllClaims(token);
    }

    public String generateToken(String userName) {
        User user = repository.findByEmail(userName).orElseThrow();

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        claims.put("id", user.getId());

        return createToken(claims, userName, jwtAccessTtl);
    }

    public String generateRefreshToken(String userName) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userName, jwtRefreshTtl);
    }

    public org.springframework.http.ResponseCookie createRefreshCookie(String refreshToken) {
        return org.springframework.http.ResponseCookie.from(cookieName, refreshToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .path(cookiePath)
                .maxAge(jwtRefreshTtl / 1000)
                .sameSite(cookieSameSite)
                .build();
    }

    private String createToken(Map<String, Object> claims, String userName, long expire) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuer(jwtIssuer)
                .setAudience(jwtAudience)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expire))
                .signWith(getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getPublicKey())
                .requireIssuer(jwtIssuer)
                .requireAudience(jwtAudience)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public void validateRefreshToken(String token) {
        extractAllClaims(token);
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    private PrivateKey getPrivateKey() {
        try {
            String privateKeyPEM = PRIVATE_KEY_PEM
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] encoded = Decoders.BASE64.decode(privateKeyPEM);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (PrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encoded));
        } catch (Exception e) {
            throw new RuntimeException("Could not load private key", e);
        }
    }

    private PublicKey getPublicKey() {
        try {
            RSAPrivateCrtKey privateKey = (RSAPrivateCrtKey) getPrivateKey();

            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(
                    privateKey.getModulus(),
                    privateKey.getPublicExponent());

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(publicKeySpec);
        } catch (Exception e) {
            throw new RuntimeException("Could not derive public key from private key", e);
        }
    }
}