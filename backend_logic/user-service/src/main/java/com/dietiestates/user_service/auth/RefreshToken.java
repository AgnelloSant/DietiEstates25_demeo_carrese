package com.dietiestates.user_service.auth;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name = "refresh_tokens")
public class RefreshToken {
  @Id private String jti;
  @Column(nullable=false) private Long userId;
  @Column(nullable=false) private Instant exp;
  private Instant revokedAt;
  @Column(nullable=false) private Instant createdAt;
  private String uaHash;
  private String ipHash;


    public RefreshToken() {}
    public RefreshToken(String jti, Long userId, Instant exp, Instant createdAt, String uaHash, String ipHash) {
        this.jti = jti;
        this.userId = userId;
        this.exp = exp;
        this.createdAt = createdAt;
        this.uaHash = uaHash;
        this.ipHash = ipHash;
    }
    public String getJti() { return jti; }
    public void setJti(String jti) { this.jti = jti; }
    public Long getUserId() { return userId; }  
    public void setUserId(Long userId) { this.userId = userId; }
    public Instant getExp() { return exp; }
    public void setExp(Instant exp) { this.exp = exp; }
    public Instant getRevokedAt() { return revokedAt; }
    public void setRevokedAt(Instant revokedAt) { this.revokedAt = revokedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public String getUaHash() { return uaHash; }
    public void setUaHash(String uaHash) { this.uaHash = uaHash; }
    public void setIpHash(String ipHash) { this.ipHash = ipHash; }
    public String getIpHash() { return ipHash; }

}
