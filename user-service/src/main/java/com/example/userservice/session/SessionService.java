package com.example.userservice.session;

import com.example.userservice.auth.JwtService;
import com.example.userservice.common.exception.SessionNotFoundException;
import com.example.userservice.common.exception.UserUnauthorizedException;
import com.example.userservice.permissions.Role;
import com.example.userservice.user.model.User;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SessionService {

  private final SessionRepository sessionRepository;
  private final JwtService jwtService;

  public SessionService(SessionRepository sessionRepository, JwtService jwtService) {
    this.sessionRepository = sessionRepository;
    this.jwtService = jwtService;
  }

  @Transactional
  public Session createSession(User user) {

    final String token = jwtService.generateToken(user.getId());

    final Session session = Session.builder().token(token).user(user).active(true).build();

    return sessionRepository.save(session);
  }

  @Transactional
  public void invalidateSession(String token) {
    sessionRepository
        .findByTokenAndActiveTrue(token)
        .ifPresent(
            session -> {
              session.setActive(false);
              sessionRepository.save(session);
            });
  }

  public void verifySession(String token) {
    if (!sessionRepository.findByTokenAndActiveTrue(token).isPresent()) {
      throw new UserUnauthorizedException("Invalid token");
    }
  }

  public Integer getSessionUserId(String token) {
    final Session session =
        sessionRepository
            .findByTokenAndActiveTrue(token)
            .orElseThrow(() -> new SessionNotFoundException("Session not found"));

    return session.getUser().getId();
  }

  public Set<Role> getUserRoles(String token) {
    final Session session =
        sessionRepository
            .findByTokenAndActiveTrue(token)
            .orElseThrow(() -> new SessionNotFoundException("Session not found"));

    return session.getUser().getRoles();
  }
}
