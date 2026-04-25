package com.example.userservice.session;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    when(sessionRepository.save(any(Session.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Session session = sessionService.createSession(user);

    assertEquals("token", session.getToken());
    assertTrue(session.getActive());
  }

  @Test
  void verifySession_validJwtAndActiveSession_doesNotThrow() {
    Session session = mock(Session.class);
    User user = mock(User.class);

    when(jwtService.verifyToken("token")).thenReturn(1);
    when(sessionRepository.findByTokenAndActiveTrue("token")).thenReturn(Optional.of(session));

    assertDoesNotThrow(() -> sessionService.verifySession("token"));
  }

  @Test
  void verifySession_invalidJwt_throwsUnauthorized() {
    when(jwtService.verifyToken("token")).thenThrow(new UserUnauthorizedException("expired"));

    assertThrows(UserUnauthorizedException.class, () -> sessionService.verifySession("token"));
  }

  @Test
  void verifySession_missingSession_throwsUnauthorized() {
    when(jwtService.verifyToken("token")).thenReturn(1);
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
    when(user.getRoles()).thenReturn(Set.of(Role.ADMIN, Role.HR));

    Session session = mock(Session.class);
    when(session.getUser()).thenReturn(user);

    when(sessionRepository.findByTokenAndActiveTrue("token")).thenReturn(Optional.of(session));

    Set<Role> roles = sessionService.getUserRoles("token");

    assertEquals(2, roles.size());
    assertTrue(roles.contains(Role.ADMIN));
    assertTrue(roles.contains(Role.HR));
  }

  @Test
  void invalidateSession_marksSessionInactive() {
    Session session = mock(Session.class);

    when(sessionRepository.findByTokenAndActiveTrue("token")).thenReturn(Optional.of(session));

    sessionService.invalidateSession("token");

    verify(session).setActive(false);
    verify(sessionRepository).saveAndFlush(session);
  }

  @Test
  void invalidateAllUserSessions_deletesByUserId() {
    sessionService.invalidateAllUserSessions(1);

    verify(sessionRepository).deleteAllByUserId(1);
  }
}
