package com.dietiestates.user_service.auth;

import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Map;

@RestController
public class JwksController {
  private final JwtProperties props;
  private final RsaKeyLoader loader;

  public JwksController(JwtProperties props, RsaKeyLoader loader) {
    this.props = props; this.loader = loader;
  }

  @GetMapping(value="/users/jwks.json", produces=MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, Object>> jwks() {
    RSAPublicKey key = (RSAPublicKey) loader.loadPublic(props.getPublicPem());
    String n = base64Url(key.getModulus());
    String e = base64Url(key.getPublicExponent());
    Map<String,Object> jwk = Map.of(
        "kty","RSA","alg","RS256","use","sig","kid",props.getKid(),
        "n", n, "e", e
    );
    return ResponseEntity.ok()
        .cacheControl(CacheControl.maxAge(java.time.Duration.ofSeconds(props.getJwksMaxAgeSeconds())))
        .body(Map.of("keys", new Object[]{ jwk }));
  }

  private static String base64Url(BigInteger v) {
    var bytes = v.toByteArray();
    if (bytes[0]==0) { // strip leading zero
      byte[] tmp = new byte[bytes.length-1];
      System.arraycopy(bytes,1,tmp,0,tmp.length);
      bytes = tmp;
    }
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }
}