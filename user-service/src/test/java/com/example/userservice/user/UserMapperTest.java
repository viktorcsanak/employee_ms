package com.example.userservice.user;

import static org.junit.jupiter.api.Assertions.*;

import com.example.userservice.permissions.Role;
import com.example.userservice.user.dto.AdminUserResponse;
import com.example.userservice.user.dto.HrUserResponse;
import com.example.userservice.user.dto.UserProfileResponse;
import com.example.userservice.user.model.User;
import com.example.userservice.user.residence.PlaceOfResidence;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;

class UserMapperTest {

  private final UserMapper mapper = new UserMapper();

  @Test
  void toProfile_mapsCorrectly() {
    User user =
        User.builder()
            .email("test@test.com")
            .firstName("John")
            .middleName("M")
            .lastName("Doe")
            .dateOfBirth(LocalDate.of(2000, 1, 1))
            .gender("M")
            .position("Dev")
            .startOfEmployment(LocalDate.of(2020, 1, 1))
            .roles(Set.of(Role.BASIC, Role.ADMIN))
            .build();

    UserProfileResponse res = mapper.toProfile(user);

    assertEquals("test@test.com", res.email());
    assertTrue(res.adminPrivileges());
  }

  @Test
  void toAdmin_mapsCorrectly() {
    User user =
        User.builder()
            .id(1)
            .email("test@test.com")
            .firstName("John")
            .middleName("M")
            .lastName("Doe")
            .roles(Set.of(Role.ADMIN, Role.HR))
            .build();

    AdminUserResponse res = mapper.toAdmin(user);

    assertEquals(1, res.id());
    assertTrue(res.adminPrivileges());
    assertTrue(res.hrManagementAccess());
  }

  @Test
  void toHr_mapsCorrectly() {
    PlaceOfResidence place = new PlaceOfResidence();
    place.setCity("Novi Sad");
    place.setPostalCode("21000");

    User user =
        User.builder()
            .email("test@test.com")
            .firstName("John")
            .middleName("M")
            .lastName("Doe")
            .dateOfBirth(LocalDate.of(2000, 1, 1))
            .startOfEmployment(LocalDate.of(2020, 1, 1))
            .position("Engineer")
            .gender("MALE")
            .address("Street 1")
            .placeOfResidence(place)
            .build();

    HrUserResponse res = mapper.toHr(user);

    assertEquals("test@test.com", res.email());
    assertEquals("John", res.firstName());
    assertEquals("Doe", res.lastName());
    assertEquals(LocalDate.of(2000, 1, 1), res.dateOfBirth());
    assertEquals(LocalDate.of(2020, 1, 1), res.startOfEmployment());
    assertEquals("Engineer", res.position());
    assertEquals("MALE", res.gender());
    assertEquals("Street 1", res.address());
    assertEquals(place, res.placeOfResidence());
  }
}
