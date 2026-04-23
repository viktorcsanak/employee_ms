package com.example.userservice.session;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.userservice.auth.JwtService;
import com.example.userservice.common.exception.UserUnauthorizedException;
import com.example.userservice.permissions.Role;
import com.example.userservice.user.model.User;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

  @Mock private SessionRepository sessionRepository;

  @Mock private JwtService jwtService;

  @InjectMocks private SessionService sessionService;

  @Test
  void createSession_savesSession() {
    User user = mock(User.class);
    when(user.getId()).thenReturn(1);

    when(jwtService.generateToken(1)).thenReturn("token");

    when(sessionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    Session session = sessionService.createSession(user);

    assertEquals("token", session.getToken());
    assertTrue(session.getActive());
  }

  @Test
  void verifySession_valid_doesNotThrow() {
    when(sessionRepository.findByTokenAndActiveTrue("token"))
        .thenReturn(Optional.of(mock(Session.class)));

    assertDoesNotThrow(() -> sessionService.verifySession("token"));
  }

  @Test
  void verifySession_invalid_throws() {
    when(sessionRepository.findByTokenAndActiveTrue("token")).thenReturn(Optional.empty());

    assertThrows(UserUnauthorizedException.class, () -> sessionService.verifySession("token"));
  }

  @Test
  void getSessionUserId_returnsUserId() {
    User user = mock(User.class);
    when(user.getId()).thenReturn(42);

    Session session = mock(Session.class);
    when(session.getUser()).thenReturn(user);

    when(sessionRepository.findByTokenAndActiveTrue("token")).thenReturn(Optional.of(session));

    assertEquals(42, sessionService.getSessionUserId("token"));
  }

  @Test
  void getUserRoles_returnsRoles() {
    User user = mock(User.class);
    when(user.getRoles()).thenReturn(Set.of(Role.ADMIN));

    Session session = mock(Session.class);
    when(session.getUser()).thenReturn(user);

    when(sessionRepository.findByTokenAndActiveTrue("token")).thenReturn(Optional.of(session));

    assertEquals(1, sessionService.getUserRoles("token").size());
  }
}
