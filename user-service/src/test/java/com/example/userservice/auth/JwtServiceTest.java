package com.example.userservice.auth;

import static org.junit.jupiter.api.Assertions.*;

import io.jsonwebtoken.JwtException;
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
    assertThrows(JwtException.class, () -> jwtService.verifyToken("invalid.token.here"));
  }
}
