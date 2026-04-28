package com.example.userservice.auth;

import com.example.userservice.auth.dto.LoginRequest;
import com.example.userservice.common.exception.UserNotFoundException;
import com.example.userservice.common.exception.UserUnauthorizedException;
import com.example.userservice.session.SessionService;
import com.example.userservice.user.UserRepository;
import com.example.userservice.user.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final SessionService sessionService;

  public AuthService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      SessionService sessionService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.sessionService = sessionService;
  }

  public String login(LoginRequest loginRequest) {
    final User user =
        userRepository
            .findByEmail(loginRequest.email())
            .orElseThrow(() -> new UserNotFoundException("Invalid credentials"));

    if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
      throw new UserUnauthorizedException("Invalid credentials");
    }

    return sessionService.createSession(user).getToken();
  }

  public void logout(String sessionToken) {
    if (sessionToken == null) {
      throw new UserUnauthorizedException("Missing session token.");
    }
    sessionService.invalidateSession(sessionToken);
  }

  public void logoutAllUserSessions(Integer id) {
    sessionService.invalidateAllUserSessions(id);
  }

  public void verifyToken(String sessionToken) {
    sessionService.verifySession(sessionToken);
  }
}
