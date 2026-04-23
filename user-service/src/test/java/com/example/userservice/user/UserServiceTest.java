package com.example.userservice.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.userservice.common.exception.UserNotFoundException;
import com.example.userservice.user.model.User;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository repo;

  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private UserService userService;

  @Test
  void getUserById_success() {
    User user = User.builder().id(1).email("test@test.com").build();

    when(repo.findById(1)).thenReturn(Optional.of(user));

    User result = userService.getUserById(1);

    assertEquals("test@test.com", result.getEmail());
  }

  @Test
  void getUserById_notFound() {
    when(repo.findById(1)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.getUserById(1));
  }

  @Test
  void deleteUser_success() {
    when(repo.existsById(1)).thenReturn(true);

    userService.deleteUser(1);

    verify(repo).deleteById(1);
  }

  @Test
  void deleteUser_missing_throws() {
    when(repo.existsById(1)).thenReturn(false);

    assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1));
  }
}
