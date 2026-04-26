package com.example.userservice.session;

import com.example.userservice.auth.JwtService;
import com.example.userservice.common.exception.SessionNotFoundException;
import com.example.userservice.common.exception.UserUnauthorizedException;
import com.example.userservice.user.model.User;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SessionService {
  private static final Logger log = LoggerFactory.getLogger(SessionService.class);
  private final SessionRepository sessionRepository;
  private final JwtService jwtService;

  public SessionService(SessionRepository sessionRepository, JwtService jwtService) {
    this.sessionRepository = sessionRepository;
    this.jwtService = jwtService;
  }

  @Transactional
  public Session createSession(User user) {

    final String token = jwtService.generateToken(user.getId());

    final Session session =
        Session.builder()
            .token(token)
            .user(user)
            .active(true)
            .validUntil(jwtService.getValidDate(token))
            .build();

    return sessionRepository.save(session);
  }

  @Transactional
  public void invalidateSession(String token) {
    sessionRepository
        .findByTokenAndActiveTrue(token)
        .ifPresent(
            session -> {
              session.setActive(false);
              sessionRepository.saveAndFlush(session);
            });
  }

  @Transactional
  public void removeAllUserSessions(Integer id) {
    sessionRepository.deleteAllByUserId(id);
  }

  @Transactional
  public void invalidateAllUserSessions(Integer id) {
    sessionRepository.findByUserIdAndActiveTrue(id).forEach(session -> session.setActive(false));
  }

  @Transactional
  public void verifySession(String token) {
    jwtService.verifyToken(token);
    sessionRepository
        .findByTokenAndActiveTrueAndValidUntilAfter(token, Instant.now())
        .orElseThrow(() -> new UserUnauthorizedException("Invalid or expired token"));
  }

  public Session getValidSession(String token) {
    return sessionRepository
        .findByTokenAndActiveTrueAndValidUntilAfter(token, Instant.now())
        .orElseThrow(() -> new SessionNotFoundException("Session not found"));
  }

  public Session validateAndGetSession(String token) {
    jwtService.verifyToken(token);
    return sessionRepository
        .findByTokenAndActiveTrueAndValidUntilAfter(token, Instant.now())
        .orElseThrow(() -> new SessionNotFoundException("Session not found"));
  }
}
