package com.example.userservice.auth;

import com.example.userservice.user.User;

import io.jsonwebtoken.JwtException;

import com.example.userservice.user.UserExistsException;
import com.example.userservice.user.UserNotFoundException;
import com.example.userservice.common.ApiResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final JwtService jwtService;
    private final SessionService sessionService;


    public AuthController(AuthService authService, JwtService jwtService, SessionService sessionService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.sessionService = sessionService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest loginRequest) {
        User user = authService.login(loginRequest);

        Session session = sessionService.createSession(user);

        ResponseCookie cookie = ResponseCookie.from("token", session.getToken())
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
    public ResponseEntity<ApiResponse> logout(@CookieValue(name = "token", required = false) String token) {

        if (token != null) {
            sessionService.invalidateSession(token);
        }

        ResponseCookie expiredCookie = ResponseCookie.from("token", "")
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(0)
            .sameSite("Strict")
            .build();

        return ResponseEntity.ok().header("Set-Cookie", expiredCookie.toString()).body(ApiResponse.builder().message("Logged out successfully").build());
    }

    @GetMapping("/verify-token")
    public ResponseEntity<ApiResponse> verifyToken(@CookieValue(name = "token", required = false) String token) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.builder().errorMessage("Missing token").build());
        }

        jwtService.verifyToken(token);

        return ResponseEntity.ok().body(ApiResponse.builder().isAuthenticated(true).build());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(UserExistsException.class)
    public ResponseEntity<ApiResponse> handleException(UserExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.builder().errorMessage(ex.getMessage()).build());
    }
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse> handleException(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.builder().errorMessage(ex.getMessage()).build());
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().errorMessage("Invalid request").build());
    }
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse> handleException(JwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.builder().errorMessage(ex.getMessage()).build());
    }
}
