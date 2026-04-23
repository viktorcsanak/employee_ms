package com.example.userservice.user;

import static org.junit.jupiter.api.Assertions.*;

import com.example.userservice.user.model.User;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class UserRepositoryTest {

  @Autowired private UserRepository repo;

  @Test
  void findByEmail_works() {
    User user = User.builder().email("test@test.com").password("x").build();

    repo.save(user);

    Optional<User> found = repo.findByEmail("test@test.com");

    assertTrue(found.isPresent());
  }
}
