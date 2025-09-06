package com.dietiestates.user_service.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
  Optional<RefreshToken> findById(String jti);
  List<RefreshToken> findByUserIdAndRevokedAtIsNull(Long userId);
  List<RefreshToken> findByUserId(Long userId);
  long deleteByExpBefore(Instant now);
}
