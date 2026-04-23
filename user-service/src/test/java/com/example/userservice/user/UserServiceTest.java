package com.example.userservice.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.userservice.auth.dto.RegisterRequest;
import com.example.userservice.common.exception.UserExistsException;
import com.example.userservice.common.exception.UserNotFoundException;
import com.example.userservice.permissions.Role;
import com.example.userservice.user.model.User;
import java.time.LocalDate;
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
  void register_success() {
    RegisterRequest req =
        new RegisterRequest(
            "John",
            "Doe",
            LocalDate.of(2000, 1, 1),
            "john@test.com",
            "pw",
            "Novi Sad",
            "21000",
            "Street 1",
            null);

    when(repo.findByEmail("john@test.com")).thenReturn(Optional.empty());
    when(passwordEncoder.encode("pw")).thenReturn("hashed");

    when(repo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

    User saved = userService.register(req);

    assertEquals("john@test.com", saved.getEmail());
    assertEquals("hashed", saved.getPassword());
    assertTrue(saved.getRoles().contains(Role.BASIC));
  }

  @Test
  void register_existingEmail_throws() {
    RegisterRequest req = mock(RegisterRequest.class);
    when(req.email()).thenReturn("taken@test.com");

    when(repo.findByEmail("taken@test.com")).thenReturn(Optional.of(new User()));

    assertThrows(UserExistsException.class, () -> userService.register(req));
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
