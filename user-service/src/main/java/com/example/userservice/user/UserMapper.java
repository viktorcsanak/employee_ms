package com.example.userservice.user;

import com.example.userservice.permissions.Role;
import com.example.userservice.user.dto.AdminUserResponse;
import com.example.userservice.user.dto.HrUserResponse;
import com.example.userservice.user.dto.UserProfileResponse;
import com.example.userservice.user.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public UserProfileResponse toProfile(User user) {
    return new UserProfileResponse(
        user.getEmail(),
        user.getFirstName(),
        user.getMiddleName(),
        user.getLastName(),
        user.getDateOfBirth().toString(),
        user.getGender(),
        null,
        user.getPosition(),
        user.getStartOfEmployment() != null ? user.getStartOfEmployment().toString() : null,
        user.getRoles().contains(Role.ADMIN),
        user.getRoles().contains(Role.HR));
  }

  public AdminUserResponse toAdmin(User user) {
    return new AdminUserResponse(
        user.getId(),
        user.getEmail(),
        user.getFirstName(),
        user.getMiddleName(),
        user.getLastName(),
        user.getRoles().contains(Role.ADMIN),
        user.getRoles().contains(Role.HR));
  }

  public HrUserResponse toHr(User user) {
    return new HrUserResponse(
        user.getEmail(),
        user.getFirstName(),
        user.getMiddleName(),
        user.getLastName(),
        user.getDateOfBirth(),
        user.getStartOfEmployment(),
        user.getPosition(),
        user.getGender(),
        user.getAddress(),
        user.getPlaceOfResidence());
  }
}
