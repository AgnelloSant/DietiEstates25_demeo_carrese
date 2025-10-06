package com.dietiestates.shared.security;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class JwtUtils {

    private static JwtParser parser;

    public static JwtParser getParser() {
        if (parser == null) {
            parser = buildParser();
        }
        return parser;
    }

    private static JwtParser buildParser() {
        try {
            String publicKeyContent = Files.readString(Path.of("/run/secrets/jwt_public.pem"))
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

            byte[] decoded = Base64.getDecoder().decode(publicKeyContent);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PublicKey publicKey = kf.generatePublic(spec);

            return Jwts.parser()
                       .verifyWith((RSAPublicKey) publicKey)
                       .build();
        } catch (Exception e) {
            throw new RuntimeException("Errore caricamento chiave pubblica", e);
        }
    }
}
