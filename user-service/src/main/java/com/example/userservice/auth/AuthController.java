package com.example.userservice.auth;

import com.example.userservice.common.api.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private static final Logger log = LoggerFactory.getLogger(AuthController.class);

  private final AuthService authService;
  private final JwtService jwtService;
  private final SessionService sessionService;

  public AuthController(
      AuthService authService, JwtService jwtService, SessionService sessionService) {
    this.authService = authService;
    this.jwtService = jwtService;
    this.sessionService = sessionService;
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest loginRequest) {
    final String token = authService.login(loginRequest);

    final ResponseCookie cookie =
        ResponseCookie.from("token", token)
            .httpOnly(true)
            .secure(false) // true in HTTPS production
            .path("/")
            .maxAge(86400)
            .sameSite("Strict")
            .build();

    return ResponseEntity.ok()
        .header("Set-Cookie", cookie.toString())
        .body(ApiResponse.builder().message("Login Successfull").build());
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse> logout(
      @CookieValue(name = "token", required = false) String token) {

    if (token != null) {
      sessionService.invalidateSession(token);
    }

    final ResponseCookie expiredCookie =
        ResponseCookie.from("token", "")
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(0)
            .sameSite("Strict")
            .build();

    return ResponseEntity.ok()
        .header("Set-Cookie", expiredCookie.toString())
        .body(ApiResponse.builder().message("Logged out successfully").build());
  }

  @GetMapping("/verify-token")
  public ResponseEntity<ApiResponse> verifyToken(
      @CookieValue(name = "token", required = false) String token) {
    if (token == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ApiResponse.builder().errorMessage("Missing token").build());
    }

    jwtService.verifyToken(token);

    return ResponseEntity.ok().body(ApiResponse.builder().isAuthenticated(true).build());
  }

  @GetMapping("/verify-admin")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse> verifyAdminRole() {
    return ResponseEntity.ok().body(ApiResponse.builder().isAuthenticated(true).build());
  }

  @GetMapping("/verify-hr")
  @PreAuthorize("hasRole('HR')")
  public ResponseEntity<ApiResponse> verifyHrRole() {
    return ResponseEntity.ok().body(ApiResponse.builder().isAuthenticated(true).build());
  }
}
