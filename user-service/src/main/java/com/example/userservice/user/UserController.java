package com.example.userservice.user;

import com.example.userservice.user.dto.AdminUserResponse;
import com.example.userservice.user.dto.HrUserResponse;
import com.example.userservice.user.dto.PasswordChangeRequest;
import com.example.userservice.user.dto.PermissionChangeRequest;
import com.example.userservice.user.dto.RegisterRequest;
import com.example.userservice.user.dto.UserProfileResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {
  private static final Logger log = LoggerFactory.getLogger(UserController.class);

  private final UserFacade userFacade;

  public UserController(UserFacade userFacade) {
    this.userFacade = userFacade;
  }

  @GetMapping
  @PreAuthorize("hasRole('BASIC')")
  public UserProfileResponse getUser(@CookieValue(name = "token") String token) {
    return userFacade.getProfileFromToken(token);
  }

  @PostMapping("/management/admin/register")
  @PreAuthorize("hasRole('ADMIN')")
  public void register(@Valid @RequestBody RegisterRequest request) {
    userFacade.register(request);
  }

  @GetMapping("/management/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public List<AdminUserResponse> getAllUsersForAdmin() {
    return userFacade.getAllUsersForAdmin();
  }

  @PutMapping("management/admin/permissions/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void grantOrRevokePermissions(
      @PathVariable @NotNull Integer id, @Valid @RequestBody PermissionChangeRequest request) {
    userFacade.grantOrRevokePermissions(id, request);
  }

  @PutMapping("management/admin/password/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void changePassword(
      @PathVariable @NotNull Integer id, @Valid @RequestBody PasswordChangeRequest request) {
    userFacade.changePassword(id, request);
  }

  @DeleteMapping("/management/admin/delete/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void removeUser(@PathVariable @NotNull Integer id) {
    userFacade.deleteUser(id);
  }

  @GetMapping("/management/hr")
  @PreAuthorize("hasRole('HR')")
  public List<HrUserResponse> getAllUsersForHR() {
    return userFacade.getAllUsersForHr();
  }
}
