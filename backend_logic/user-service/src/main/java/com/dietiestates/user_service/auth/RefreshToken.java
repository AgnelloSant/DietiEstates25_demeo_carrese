package com.dietiestates.user_service.auth;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;


@Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Entity @Table(name = "refresh_tokens")
public class RefreshToken {
  @Id private String jti;
  @Column(nullable=false) private Long userId;
  @Column(nullable=false) private Instant exp;
  private Instant revokedAt;
  @Column(nullable=false) private Instant createdAt;
  private String uaHash;
  private String ipHash;


}
