package com.example.userservice.user;

import com.example.userservice.permissions.Role;
import com.example.userservice.user.dto.RegisterRequest;
import com.example.userservice.user.model.User;
import com.example.userservice.user.residence.PlaceOfResidenceService;
import java.util.HashSet;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final PlaceOfResidenceService residenceService;

  public RegistrationService(
      UserService userService,
      PasswordEncoder passwordEncoder,
      PlaceOfResidenceService residenceService) {
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
    this.residenceService = residenceService;
  }

  public User register(RegisterRequest request) {
    final Set<Role> roles = new HashSet<>();
    roles.add(Role.BASIC);
    if (request.adminPrivileges()) {
      roles.add(Role.ADMIN);
    }
    if (request.hrManagementAccess()) {
      roles.add(Role.HR);
    }
    final User user =
        User.builder()
            .firstName(request.firstName())
            .middleName(request.middleName())
            .lastName(request.lastName())
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .dateOfBirth(request.dateOfBirth())
            .startOfEmployment(request.startOfEmployment())
            .address(request.address())
            .placeOfResidence(
                residenceService.resolve(
                    request.placeOfResidence().getCity(),
                    request.placeOfResidence().getPostalCode()))
            .roles(roles)
            .gender(request.gender())
            .position(request.position())
            .build();

    return userService.register(user);
  }
}
