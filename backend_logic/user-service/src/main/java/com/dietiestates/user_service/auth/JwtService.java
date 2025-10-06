package com.dietiestates.user_service.auth;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import com.dietiestates.shared.security.JwtProperties;
import com.dietiestates.shared.security.RsaKeyLoader;

@Service
public class JwtService {
  private final JwtProperties props;
  private final PrivateKey priv;
  private final PublicKey pub;

  public JwtService(JwtProperties props, RsaKeyLoader loader) {
    this.props = props;
    this.priv = loader.loadPrivate(props.getPrivatePem());
    this.pub  = loader.loadPublic(props.getPublicPem());
  }

  public String signAccess(String userId, String role) {
    Instant now = Instant.now();
    Instant exp = now.plus(props.getAccessTtl());
    return Jwts.builder()
        .subject(userId)
        .issuer(props.getIss())
        .audience().add(props.getAud()).and()
        .issuedAt(Date.from(now))
        .expiration(Date.from(exp))
        .claims(Map.of("role", role))
        .header().add("kid", props.getKid()).and()
        .signWith(priv, Jwts.SIG.RS256)
        .compact();
  }

  public String signRefresh(String userId, String jti) {
    Instant now = Instant.now();
    Instant exp = now.plus(props.getRefreshTtl());
    return Jwts.builder()
        .subject(userId)
        .issuer(props.getIss())
        .audience().add(props.getAud()).and()
        .issuedAt(Date.from(now))
        .expiration(Date.from(exp))
        .id(jti)
        .header().add("kid", props.getKid()).and()
        .signWith(priv, Jwts.SIG.RS256)
        .compact();
  }

  public Claims verify(String token) {
    // clock skew ~60s
    return Jwts.parser()
        .requireIssuer(props.getIss())
        .requireAudience(props.getAud())
        .clockSkewSeconds(60)
        .verifyWith(pub)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  public Instant expiresAt(String jwt) {
    Claims c = Jwts.parser().verifyWith(pub).build().parseSignedClaims(jwt).getPayload();
    return c.getExpiration().toInstant();
  }
}
