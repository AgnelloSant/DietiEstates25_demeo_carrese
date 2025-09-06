package com.dietiestates.user_service.auth;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class RsaKeyLoader {

  public PrivateKey loadPrivate(String value) {
    try {
      byte[] der = toDer(value, true);
      return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(der));
    } catch (Exception e) { throw new IllegalStateException("Invalid private PEM", e); }
  }

  public PublicKey loadPublic(String value) {
    try {
      byte[] der = toDer(value, false);
      return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(der));
    } catch (Exception e) { throw new IllegalStateException("Invalid public PEM", e); }
  }

  private static byte[] toDer(String input, boolean isPrivate) throws Exception {
  if (input == null || input.isBlank()) throw new IllegalArgumentException("empty key");
  String val = input.trim();

  // file:...  oppure path assoluto /...  oppure relativo ./  ../
  if (val.startsWith("file:") || val.startsWith("/") || val.startsWith("./") || val.startsWith("../")) {
    java.nio.file.Path p = val.startsWith("file:") ? java.nio.file.Path.of(val.substring(5)) : java.nio.file.Path.of(val);
    String text = java.nio.file.Files.readString(p, java.nio.charset.StandardCharsets.US_ASCII).trim();
    return pemToDer(text, isPrivate);
  }

  if (val.startsWith("-----BEGIN")) {
    return pemToDer(val, isPrivate);
  }

  byte[] decoded = java.util.Base64.getDecoder().decode(val);
  String maybeText = new String(decoded, java.nio.charset.StandardCharsets.US_ASCII);
  if (maybeText.contains("-----BEGIN")) return pemToDer(maybeText, isPrivate);
  return decoded;
}


  private static byte[] pemToDer(String pem, boolean isPrivate) {
    String body = pem
        .replaceAll("-----BEGIN (RSA )?PRIVATE KEY-----", "")
        .replaceAll("-----END (RSA )?PRIVATE KEY-----", "")
        .replaceAll("-----BEGIN (RSA )?PUBLIC KEY-----", "")
        .replaceAll("-----END (RSA )?PUBLIC KEY-----", "")
        .replaceAll("\\s", "");
    byte[] der = Base64.getDecoder().decode(body);
    if (isPrivate && pem.contains("BEGIN RSA PRIVATE KEY")) {
      der = wrapPkcs1ToPkcs8(der); // converti PKCS#1 → PKCS#8
    }
    return der;
  }

  private static byte[] wrapPkcs1ToPkcs8(byte[] pkcs1) {
    // PrivateKeyInfo = SEQ(version=0, AlgId(rsaEncryption,NULL), OCTET STRING (pkcs1))
    byte[] oid = new byte[]{0x06,0x09,0x2a,(byte)0x86,0x48,(byte)0x86,(byte)0xf7,0x0d,0x01,0x01,0x01};
    byte[] algId = seq(concat(oid, new byte[]{0x05,0x00}));
    byte[] version = new byte[]{0x02,0x01,0x00};
    byte[] octet = tlv(0x04, pkcs1);
    return seq(concat(version, algId, octet));
  }

  private static byte[] seq(byte[] content) { return tlv(0x30, content); }
  private static byte[] tlv(int tag, byte[] content) {
    byte[] len = encLen(content.length);
    byte[] out = new byte[1 + len.length + content.length];
    out[0] = (byte) tag;
    System.arraycopy(len, 0, out, 1, len.length);
    System.arraycopy(content, 0, out, 1 + len.length, content.length);
    return out;
  }
  private static byte[] encLen(int len) {
    if (len < 128) return new byte[]{ (byte) len };
    if (len < 256) return new byte[]{ (byte)0x81, (byte)len };
    return new byte[]{ (byte)0x82, (byte)(len >> 8), (byte)(len & 0xff) };
  }
  private static byte[] concat(byte[]... arrs) {
    int n=0; for (byte[] a:arrs) n+=a.length;
    byte[] out=new byte[n]; int p=0;
    for (byte[] a:arrs){ System.arraycopy(a,0,out,p,a.length); p+=a.length; }
    return out;
  }
}
