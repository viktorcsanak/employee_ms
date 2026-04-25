package com.example.userservice.auth;

import static org.junit.jupiter.api.Assertions.*;

import com.example.userservice.common.exception.UserUnauthorizedException;
import java.util.Date;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

  private final JwtService jwtService = new JwtService();

  @Test
  void generateAndVerify_tokenRoundTrip() {
    String token = jwtService.generateToken(123);

    Integer id = jwtService.verifyToken(token);

    assertEquals(123, id);
  }

  @Test
  void verify_invalidToken_throws() {
    assertThrows(
        UserUnauthorizedException.class, () -> jwtService.verifyToken("invalid.token.here"));
  }

  @Test
  void verify_expiredToken_throws() throws InterruptedException {
    JwtService shortLivedService =
        new JwtService() {
          @Override
          public String generateToken(Integer id) {
            return io.jsonwebtoken.Jwts.builder()
                .subject(id.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1)) // expires immediately
                .signWith(
                    io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                        "replace-this-with-a-long-256-bit-secret-key".getBytes()))
                .compact();
          }
        };

    String token = shortLivedService.generateToken(123);

    Thread.sleep(5); // ensure expiration

    assertThrows(UserUnauthorizedException.class, () -> shortLivedService.verifyToken(token));
  }

  @Test
  void verify_nullToken_throws() {
    assertThrows(Exception.class, () -> jwtService.verifyToken(null));
  }

  @Test
  void verify_tamperedToken_throws() {
    String token = jwtService.generateToken(123);

    String tampered = token.substring(0, token.length() - 2) + "xx";

    assertThrows(UserUnauthorizedException.class, () -> jwtService.verifyToken(tampered));
  }
}
