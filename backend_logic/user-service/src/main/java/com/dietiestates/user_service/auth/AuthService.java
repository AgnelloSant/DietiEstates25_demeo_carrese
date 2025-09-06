package com.dietiestates.user_service.auth;

import io.jsonwebtoken.Claims;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class AuthService {
  private final JwtService jwt;
  private final JwtProperties props;
  private final RefreshTokenRepository repo;

  public AuthService(JwtService jwt, JwtProperties props, RefreshTokenRepository repo) {
    this.jwt = jwt; this.props = props; this.repo = repo;
  }

  public record Tokens(String access, ResponseCookie refreshCookie) {}

  public Tokens issueTokens(Long userId, String role, String uaHash, String ipHash) {
    String access = jwt.signAccess(userId.toString(), role);

    // jti = identificatore univoco del refresh token
    String jti = java.util.UUID.randomUUID().toString().replace("-", "");
    String refresh = jwt.signRefresh(userId.toString(), jti);
    Instant exp = jwt.expiresAt(refresh);

    RefreshToken rt = new RefreshToken();
    rt.setJti(jti);
    rt.setUserId(userId);    // 👈 adesso Long
    rt.setExp(exp);
    rt.setCreatedAt(Instant.now());
    rt.setUaHash(uaHash);
    rt.setIpHash(ipHash);
    repo.save(rt);

    Duration maxAge = Duration.between(Instant.now(), exp);
    ResponseCookie cookie = ResponseCookie.from(props.getRefreshCookieName(), refresh)
        .httpOnly(true)
        .secure(props.isRefreshCookieSecure())
        .sameSite(props.getRefreshCookieSameSite())
        .path(props.getRefreshCookiePath())
        .maxAge(maxAge.isNegative()? Duration.ZERO : maxAge)
        .build();

    return new Tokens(access, cookie);
  }

  public Tokens rotate(String refreshJwt, String uaHash, String ipHash) {
    Claims c = jwt.verify(refreshJwt); // valida firma/iss/aud/exp
    String jti = c.getId();
    Long userId = Long.valueOf(c.getSubject()); // 👈 convertiamo da String a Long

    RefreshToken entry = repo.findById(jti).orElse(null);
    if (entry == null || entry.getRevokedAt()!=null) {
      // reuse detection: revoca tutti i token attivi dell'utente
      revokeAll(userId);
      throw new IllegalStateException("refresh reuse detected");
    }

    // Rotazione: revoca vecchio e crea nuovo
    entry.setRevokedAt(Instant.now());
    repo.save(entry);

    return issueTokens(userId, /*role*/ "USER", uaHash, ipHash);
  }

  public void logout(String refreshJwt) {
    try {
      String jti = jwt.verify(refreshJwt).getId();
      repo.findById(jti).ifPresent(rt -> {
        rt.setRevokedAt(Instant.now());
        repo.save(rt);
      });
    } catch (Exception ignored) {}
  }

  public void revokeAll(Long userId) {
    List<RefreshToken> list = repo.findByUserId(userId); // 👈 repository deve usare Long
    Instant now = Instant.now();
    for (RefreshToken rt : list) {
      if (rt.getRevokedAt()==null) {
        rt.setRevokedAt(now);
      }
    }
    repo.saveAll(list);
  }
}
