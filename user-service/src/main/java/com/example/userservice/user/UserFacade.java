package com.example.userservice.user;

import com.example.userservice.auth.JwtService;
import com.example.userservice.config.security.dto.AuthenticatedUserPrincipal;
import com.example.userservice.kafka.KafkaProducer;
import com.example.userservice.notification.dto.PasswordChangedByAdministratorMessage;
import com.example.userservice.notification.dto.UserCreatedMessage;
import com.example.userservice.user.dto.AdminUserResponse;
import com.example.userservice.user.dto.HrUserResponse;
import com.example.userservice.user.dto.PasswordChangeRequest;
import com.example.userservice.user.dto.PermissionChangeRequest;
import com.example.userservice.user.dto.RegisterRequest;
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
  private final UserAdministrationService adminService;
  private final KafkaProducer producer;

  public UserFacade(
      UserService userService,
      UserQueryService userQueryService,
      RegistrationService registrationService,
      UserMapper mapper,
      JwtService jwtService,
      UserAdministrationService adminService,
      KafkaProducer producer) {
    this.userService = userService;
    this.queryService = userQueryService;
    this.registrationService = registrationService;
    this.mapper = mapper;
    this.jwtService = jwtService;
    this.adminService = adminService;
    this.producer = producer;
  }

  public User register(RegisterRequest request) {
    final User user = registrationService.register(request);
    // TODO: implement outbox pattern
    producer.sendUserCreatedMessage(new UserCreatedMessage(user.getEmail(), user.getFirstName()));
    return user;
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

  public List<HrUserResponse> getAllUsersForHr() {
    return queryService.search(null).stream().map(mapper::toHr).toList();
  }

  public void grantOrRevokePermissions(Integer id, PermissionChangeRequest request) {
    adminService.grantOrRevokePermissions(id, request);
  }

  public void changePassword(
      Integer id, PasswordChangeRequest request, AuthenticatedUserPrincipal authUser) {
    final User user = adminService.changePassword(id, request);
    producer.sendPasswordChangedByAdminMessage(
        new PasswordChangedByAdministratorMessage(
            user.getEmail(), user.getFirstName(), authUser.email()));
  }

  public void deleteUser(Integer id) {
    adminService.deleteUser(id);
  }
}
