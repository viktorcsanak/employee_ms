package com.example.userservice.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository
    extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
  Optional<User> findByEmail(String email);

  Optional<User> findBySessions_TokenAndSessions_ActiveTrue(String token);
}
