package com.dietiestates.shared.security;
import java.security.PublicKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JwtValidator {
  private final PublicKey pub;
  private final JwtProperties props;

  public JwtValidator(JwtProperties props, RsaKeyLoader loader) {
    this.props = props;
    this.pub  = loader.loadPublic(props.getPublicPem());
  }

  public Claims verify(String token) {
    return Jwts.parser()
        .requireIssuer(props.getIss())
        .requireAudience(props.getAud())
        .clockSkewSeconds(60)
        .verifyWith(pub)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}

