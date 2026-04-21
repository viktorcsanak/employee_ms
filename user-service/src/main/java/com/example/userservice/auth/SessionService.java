package com.example.userservice.auth;

import com.example.userservice.user.User;
import com.example.userservice.permissions.Role;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final JwtService jwtService;

    public SessionService(SessionRepository sessionRepository,
                          JwtService jwtService) {
        this.sessionRepository = sessionRepository;
        this.jwtService = jwtService;
    }

    @Transactional
    public Session createSession(User user) {

        String token = jwtService.generateToken(user.getId());

        Session session = Session.builder()
                .token(token)
                .user(user)
                .active(true)
                .build();

        return sessionRepository.save(session);
    }

    @Transactional
    public void invalidateSession(String token) {
        sessionRepository.findByTokenAndActiveTrue(token)
                .ifPresent(session -> {
                    session.setActive(false);
                    sessionRepository.save(session);
                });
    }

    public boolean isValid(String token) {
        return sessionRepository.findByTokenAndActiveTrue(token).isPresent();
    }

    public Integer getSessionUserId(String token) {
        Session session = sessionRepository.findByTokenAndActiveTrue(token)
                .orElseThrow(() -> new SessionNotFoundException("Session not found"));

        return session.getUser().getId();
    }

    public Set<Role> getUserRoles(String token) {
        Session session = sessionRepository.findByTokenAndActiveTrue(token)
                .orElseThrow(() -> new SessionNotFoundException("Session not found"));

        return session.getUser().getRoles();
    }
}