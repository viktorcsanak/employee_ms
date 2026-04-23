package com.example.userservice.user;

import com.example.userservice.auth.dto.RegisterRequest;
import com.example.userservice.permissions.Role;
import com.example.userservice.user.model.User;
import com.example.userservice.user.residence.PlaceOfResidenceService;
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

    final User user =
        User.builder()
            .firstName(request.firstName())
            .lastName(request.lastName())
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .dateOfBirth(request.dateOfBirth())
            .address(request.address())
            .placeOfResidence(residenceService.resolve(request.city(), request.postalCode()))
            .roles(request.roles() == null ? Set.of(Role.BASIC) : request.roles())
            .build();

    return userService.register(user);
  }
}
