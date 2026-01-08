package com.dietiestates.shared.security;

import org.springframework.boot.context.properties.ConfigurationProperties;


import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

//@Configuration
@Getter @Setter
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


}

