package com.example.userservice.auth;

import com.example.userservice.common.exception.UserUnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private final SecretKey key =
      Keys.hmacShaKeyFor("replace-this-with-a-long-256-bit-secret-key".getBytes());

  public String generateToken(Integer id) {
    return Jwts.builder()
        .subject(id.toString())
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + 86400000))
        .signWith(key)
        .compact();
  }

  public Instant getValidDate(String token) {
    if (token == null) {
      throw new UserUnauthorizedException("Token is null");
    }
    try {
      final Claims claims =
          Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

      return claims.getExpiration().toInstant();

    } catch (JwtException e) {
      throw new UserUnauthorizedException("Invalid token");
    }
  }

  public Integer verifyToken(String token) {
    if (token == null) {
      throw new UserUnauthorizedException("Invalid or expired token");
    }

    try {
      final Claims claims =
          Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

      return Integer.parseInt(claims.getSubject()); // returns Id

    } catch (JwtException e) {
      throw new UserUnauthorizedException("Invalid or expired token");
    }
  }
}
