package com.example.userservice.session;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
  Optional<Session> findByToken(String token);

  Optional<Session> findByTokenAndActiveTrue(String token);

  Optional<Session> findByTokenAndActiveTrueAndValidUntilAfter(String token, Instant now);

  List<Session> findByUserIdAndActiveTrue(Integer id);

  void deleteByToken(String token);

  void deleteAllByUserId(Integer userId);
}
