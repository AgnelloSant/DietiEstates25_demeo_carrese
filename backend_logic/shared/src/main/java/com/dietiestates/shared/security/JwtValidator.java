package com.dietiestates.shared.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import lombok.Setter;

import java.security.PublicKey;

@Getter
@Setter
public class JwtValidator {
  //L'ho reso una classe pura che riceve tutto nel costruttore, non sa dove si trovano gli altri file
  private final PublicKey pub;
  private final String issuer;
  private final String audience;

  public JwtValidator(PublicKey pub, String issuer, String audience) {
    this.pub = pub;
    this.issuer = issuer;
    this.audience = audience;
  }

  public Claims verify(String token) {
    return Jwts.parser()
        .requireIssuer(issuer)      
        .requireAudience(audience) 
        .clockSkewSeconds(60)
        .verifyWith(pub)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}