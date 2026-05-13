package com.example.userservice.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.userservice.common.exception.UserExistsException;
import com.example.userservice.common.exception.UserNotFoundException;
import com.example.userservice.kafka.KafkaProducer;
import com.example.userservice.user.model.User;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository repo;

  @Mock private KafkaProducer producer;

  @InjectMocks private UserService userService;

  @Test
  void register_success() {
    User user = User.builder().email("test@test.com").build();

    when(repo.findByEmail("test@test.com")).thenReturn(Optional.empty());

    when(repo.save(user)).thenReturn(user);

    User result = userService.register(user);

    assertEquals("test@test.com", result.getEmail());

    verify(repo).findByEmail("test@test.com");
    verify(repo).save(user);
  }

  @Test
  void register_userAlreadyExists_throws() {
    User user = User.builder().email("test@test.com").build();

    when(repo.findByEmail("test@test.com")).thenReturn(Optional.of(new User()));

    assertThrows(UserExistsException.class, () -> userService.register(user));

    verify(repo).findByEmail("test@test.com");
    verify(repo, never()).save(any());
  }

  @Test
  void getUserById_success() {
    User user = User.builder().id(1).email("test@test.com").build();

    when(repo.findById(1)).thenReturn(Optional.of(user));

    User result = userService.getUserById(1);

    assertEquals("test@test.com", result.getEmail());

    verify(repo).findById(1);
  }

  @Test
  void getUserById_notFound() {
    when(repo.findById(1)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.getUserById(1));

    verify(repo).findById(1);
  }
}
