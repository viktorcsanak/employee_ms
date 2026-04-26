package com.example.userservice.session;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.userservice.auth.JwtService;
import com.example.userservice.common.exception.SessionNotFoundException;
import com.example.userservice.common.exception.UserUnauthorizedException;
import com.example.userservice.user.model.User;
import java.time.Instant;
import java.util.Optional;
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
    when(jwtService.getValidDate("token")).thenReturn(java.time.Instant.now().plusSeconds(3600));

    when(sessionRepository.save(any(Session.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Session session = sessionService.createSession(user);

    assertEquals("token", session.getToken());
    assertTrue(session.getActive());
  }

  @Test
  void verifySession_validSession_doesNotThrow() {

    when(jwtService.verifyToken("token")).thenReturn(1);

    Session session = mock(Session.class);

    when(sessionRepository.findByTokenAndActiveTrueAndValidUntilAfter(
            eq("token"), any(Instant.class)))
        .thenReturn(Optional.of(session));

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

    when(sessionRepository.findByTokenAndActiveTrueAndValidUntilAfter(
            eq("token"), any(Instant.class)))
        .thenReturn(Optional.empty());

    assertThrows(UserUnauthorizedException.class, () -> sessionService.verifySession("token"));
  }

  @Test
  void getValidSession_returnsSession() {

    Session session = mock(Session.class);

    when(sessionRepository.findByTokenAndActiveTrueAndValidUntilAfter(
            eq("token"), any(Instant.class)))
        .thenReturn(Optional.of(session));

    Session result = sessionService.getValidSession("token");

    assertEquals(session, result);
  }

  @Test
  void getValidSession_missing_throwsException() {

    when(sessionRepository.findByTokenAndActiveTrueAndValidUntilAfter(
            eq("token"), any(Instant.class)))
        .thenReturn(Optional.empty());

    assertThrows(SessionNotFoundException.class, () -> sessionService.getValidSession("token"));
  }

  @Test
  void validateAndGetSession_returnsSession() {

    when(jwtService.verifyToken("token")).thenReturn(1);

    Session session = mock(Session.class);

    when(sessionRepository.findByTokenAndActiveTrueAndValidUntilAfter(
            eq("token"), any(Instant.class)))
        .thenReturn(Optional.of(session));

    Session result = sessionService.validateAndGetSession("token");

    assertEquals(session, result);
  }

  @Test
  void invalidateSession_marksInactive() {

    Session session = mock(Session.class);

    when(sessionRepository.findByTokenAndActiveTrue("token")).thenReturn(Optional.of(session));

    sessionService.invalidateSession("token");

    verify(session).setActive(false);
    verify(sessionRepository).saveAndFlush(session);
  }

  @Test
  void invalidateAllUserSessions_callsRepository() {

    sessionService.invalidateAllUserSessions(1);

    verify(sessionRepository).findByUserIdAndActiveTrue(1);
  }

  @Test
  void removeAllUserSessions_deletesSessions() {

    sessionService.removeAllUserSessions(1);

    verify(sessionRepository).deleteAllByUserId(1);
  }
}
