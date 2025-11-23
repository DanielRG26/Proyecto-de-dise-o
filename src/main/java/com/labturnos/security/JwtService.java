package com.labturnos.security;

import com.labturnos.domain.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Component
public class JwtService {
  private final Key key;
  private final long expirationMinutes;

  public JwtService(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration.minutes}") long expirationMinutes) {
    byte[] keyBytes = secret.length() >= 32 ? secret.getBytes() : Decoders.BASE64.decode(java.util.Base64.getEncoder().encodeToString(secret.getBytes()));
    this.key = Keys.hmacShaKeyFor(keyBytes);
    this.expirationMinutes = expirationMinutes;
  }

  public String generate(String subject, Role role) {
    Instant now = Instant.now();
    return Jwts.builder()
      .setSubject(subject)
      .addClaims(Map.of("role", role.name()))
      .setIssuedAt(Date.from(now))
      .setExpiration(Date.from(now.plus(expirationMinutes, ChronoUnit.MINUTES)))
      .signWith(key, SignatureAlgorithm.HS256)
      .compact();
  }

  public Claims parse(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
  }
}