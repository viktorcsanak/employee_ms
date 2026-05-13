package com.example.userservice.user;

import static org.junit.jupiter.api.Assertions.*;

import com.example.userservice.user.model.User;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryIT {
  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:16-alpine")
          .withDatabaseName("employee_ms_db")
          .withUsername("userapp")
          .withPassword("userapp");

  @Autowired private UserRepository repo;

  @Test
  void findByEmail_works() {
    User user =
        User.builder()
            .email("test@test.com")
            .password("x")
            .firstName("John")
            .lastName("Doe")
            .gender("M")
            .position("DEV")
            .dateOfBirth(LocalDate.of(2000, 1, 1))
            .startOfEmployment(LocalDate.of(2020, 1, 1))
            .address("Street 1")
            .build();

    repo.save(user);

    Optional<User> found = repo.findByEmail("test@test.com");

    assertTrue(found.isPresent());
    assertEquals("test@test.com", found.get().getEmail());
  }
}
