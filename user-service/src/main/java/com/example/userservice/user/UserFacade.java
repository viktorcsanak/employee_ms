package com.example.userservice.user;

import com.example.userservice.auth.JwtService;
import com.example.userservice.auth.dto.RegisterRequest;
import com.example.userservice.user.dto.AdminUserResponse;
import com.example.userservice.user.dto.UserProfileResponse;
import com.example.userservice.user.model.User;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserFacade {

  private final UserService userService;
  private final UserQueryService queryService;
  private final RegistrationService registrationService;
  private final UserMapper mapper;
  private final JwtService jwtService;

  public UserFacade(
      UserService userService,
      UserQueryService userQueryService,
      RegistrationService registrationService,
      UserMapper mapper,
      JwtService jwtService) {
    this.userService = userService;
    this.queryService = userQueryService;
    this.registrationService = registrationService;
    this.mapper = mapper;
    this.jwtService = jwtService;
  }

  public User register(RegisterRequest request) {
    return registrationService.register(request);
  }

  public UserProfileResponse getProfile(Integer id) {
    return mapper.toProfile(userService.getUserById(id));
  }

  public UserProfileResponse getProfileFromToken(String token) {
    final Integer id = jwtService.verifyToken(token);
    return getProfile(id);
  }

  public List<AdminUserResponse> getAllUsersForAdmin() {
    return queryService.search(null).stream().map(mapper::toAdmin).toList();
  }
}
