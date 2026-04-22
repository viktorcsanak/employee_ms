package com.example.userservice.auth;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
  Optional<Session> findByToken(String token);

  Optional<Session> findByTokenAndActiveTrue(String token);

  void deleteByToken(String token);

  void deleteAllByUserId(Integer userId);
}
