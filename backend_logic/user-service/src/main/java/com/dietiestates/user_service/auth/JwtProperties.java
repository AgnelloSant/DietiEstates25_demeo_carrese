package com.dietiestates.user_service.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
  private String iss;
  private String aud;
  private Duration accessTtl;
  private Duration refreshTtl;
  private String kid;
  private String privatePem;
  private String publicPem;

  private String refreshCookieName;
  private String refreshCookiePath;
  private String refreshCookieSameSite;
  private boolean refreshCookieSecure;
  private int jwksMaxAgeSeconds;

    public String getIss() { return iss; }
    public void setIss(String iss) { this.iss = iss; }
    public String getAud() { return aud; }
    public void setAud(String aud) { this.aud = aud; }
    public Duration getAccessTtl() { return accessTtl; }
    public void setAccessTtl(Duration accessTtl) { this.accessTtl = accessTtl; }
    public Duration getRefreshTtl() { return refreshTtl; }
    public void setRefreshTtl(Duration refreshTtl) { this.refreshTtl = refreshTtl; }
    public String getKid() { return kid; }
    public void setKid(String kid) { this.kid = kid; }
    public String getPrivatePem() { return privatePem; }
    public void setPrivatePem(String privatePem) { this.privatePem = privatePem; }
    public String getPublicPem() { return publicPem; }
    public void setPublicPem(String publicPem) { this.publicPem = publicPem; }
    public String getRefreshCookieName() { return refreshCookieName; }
    public void setRefreshCookieName(String refreshCookieName) { this.refreshCookieName = refreshCookieName; }
    public String getRefreshCookiePath() { return refreshCookiePath; }
    public void setRefreshCookiePath(String refreshCookiePath) { this.refreshCookiePath = refreshCookiePath; }
    public String getRefreshCookieSameSite() { return refreshCookieSameSite; }
    public void setRefreshCookieSameSite(String refreshCookieSameSite) { this.refreshCookieSameSite = refreshCookieSameSite; }
    public boolean isRefreshCookieSecure() { return refreshCookieSecure; }
    public void setRefreshCookieSecure(boolean refreshCookieSecure) { this.refreshCookieSecure = refreshCookieSecure; }
    public int getJwksMaxAgeSeconds() { return jwksMaxAgeSeconds; }
    public void setJwksMaxAgeSeconds(int jwksMaxAgeSeconds) { this.jwksMaxAgeSeconds = jwksMaxAgeSeconds; } 
}
