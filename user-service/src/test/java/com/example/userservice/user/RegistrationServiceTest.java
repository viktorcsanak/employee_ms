package com.example.userservice.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.userservice.permissions.Role;
import com.example.userservice.user.dto.RegisterRequest;
import com.example.userservice.user.model.User;
import com.example.userservice.user.residence.PlaceOfResidence;
import com.example.userservice.user.residence.PlaceOfResidenceService;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

  @Mock private UserService userService;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private PlaceOfResidenceService residenceService;

  @InjectMocks private RegistrationService registrationService;

  @Test
  void register_basicUser_onlyBasicRole() {

    PlaceOfResidence inputResidence =
        PlaceOfResidence.builder().city("Novi Sad").postalCode("21000").build();

    RegisterRequest request =
        new RegisterRequest(
            "John",
            "Middle",
            "Doe",
            LocalDate.of(2000, 1, 1),
            LocalDate.of(2024, 1, 1),
            "john@test.com",
            "male",
            "password123",
            inputResidence,
            "Street 1",
            "Developer",
            false,
            false);

    PlaceOfResidence resolved =
        PlaceOfResidence.builder().id(1).city("Novi Sad").postalCode("21000").build();

    when(residenceService.resolve("Novi Sad", "21000")).thenReturn(resolved);
    when(passwordEncoder.encode("password123")).thenReturn("hashed");

    when(userService.register(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

    User result = registrationService.register(request);

    verify(userService).register(captor.capture());

    User saved = captor.getValue();

    assertEquals("john@test.com", saved.getEmail());
    assertEquals("hashed", saved.getPassword());
    assertTrue(saved.getRoles().contains(Role.BASIC));
    assertEquals(resolved, saved.getPlaceOfResidence());

    assertSame(saved, result);
  }

  @Test
  void register_adminAndHR_addsRoles() {

    RegisterRequest request =
        new RegisterRequest(
            "John",
            "Middle",
            "Doe",
            LocalDate.of(2000, 1, 1),
            LocalDate.of(2024, 1, 1),
            "john@test.com",
            "male",
            "password123",
            PlaceOfResidence.builder().city("Belgrade").postalCode("11000").build(),
            "Street 1",
            "Developer",
            true,
            true);

    when(residenceService.resolve(any(), any()))
        .thenReturn(PlaceOfResidence.builder().city("Belgrade").postalCode("11000").build());

    when(passwordEncoder.encode(any())).thenReturn("hashed");

    when(userService.register(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    User result = registrationService.register(request);

    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(userService).register(captor.capture());

    User saved = captor.getValue();

    assertTrue(saved.getRoles().contains(Role.BASIC));
    assertTrue(saved.getRoles().contains(Role.ADMIN));
    assertTrue(saved.getRoles().contains(Role.HR));
    assertEquals(3, saved.getRoles().size());

    assertNotNull(result);
  }
}
