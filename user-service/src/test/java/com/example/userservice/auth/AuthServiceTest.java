package com.example.userservice.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.userservice.common.exception.UserNotFoundException;
import com.example.userservice.common.exception.UserUnauthorizedException;
import com.example.userservice.user.User;
import com.example.userservice.user.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private SessionService sessionService;

  @InjectMocks private AuthService authService;

  @Test
  void login_success_returnsToken() {
    User user = mock(User.class);

    when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
    when(user.getPassword()).thenReturn("hashed");
    when(passwordEncoder.matches("raw", "hashed")).thenReturn(true);

    Session session = Session.builder().token("token123").user(user).build();
    when(sessionService.createSession(user)).thenReturn(session);

    String token = authService.login(new LoginRequest("test@test.com", "raw"));

    assertEquals("token123", token);
  }

  @Test
  void login_wrongPassword_throws() {
    User user = mock(User.class);

    when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
    when(user.getPassword()).thenReturn("hashed");
    when(passwordEncoder.matches(any(), any())).thenReturn(false);

    assertThrows(
        UserUnauthorizedException.class,
        () -> authService.login(new LoginRequest("test@test.com", "wrong")));
  }

  @Test
  void login_userNotFound_throws() {
    when(userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

    assertThrows(
        UserNotFoundException.class,
        () -> authService.login(new LoginRequest("missing@test.com", "pw")));
  }

  @Test
  void logout_nullToken_throws() {
    assertThrows(NullPointerException.class, () -> authService.logout(null));
  }

  @Test
  void verifyToken_delegatesToSessionService() {
    authService.verifyToken("abc");

    verify(sessionService).verifySession("abc");
  }
}
