package com.example.userservice.user;

import com.example.userservice.auth.dto.RegisterRequest;
import com.example.userservice.user.dto.AdminUserResponse;
import com.example.userservice.user.dto.PasswordChangeRequest;
import com.example.userservice.user.dto.PermissionChangeRequest;
import com.example.userservice.user.dto.UserProfileResponse;
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

  @PostMapping("/register")
  public void register(@RequestBody RegisterRequest request) {
    userFacade.register(request);
  }

  @GetMapping
  @PreAuthorize("hasRole('BASIC')")
  public UserProfileResponse getUser(@CookieValue(name = "token") String token) {
    return userFacade.getProfileFromToken(token);
  }

  @GetMapping("/management/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public List<AdminUserResponse> getAllUsersForAdmin() {
    return userFacade.getAllUsersForAdmin();
  }

  @PutMapping("management/admin/permissions/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void grantOrRevokePermissions(
      @PathVariable Integer id, @RequestBody PermissionChangeRequest request) {
    userFacade.grantOrRevokePermissions(id, request);
  }

  @PutMapping("management/admin/password/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void changePassword(@PathVariable Integer id, @RequestBody PasswordChangeRequest request) {
    userFacade.changePassword(id, request);
  }

  @DeleteMapping("/management/admin/delete/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void removeUser(@PathVariable Integer id) {
    userFacade.deleteUser(id);
  }

  /* @GetMapping
  public List<User> findUsers(@RequestParam UserSearchRequest request) {
    log.info(
        "firstName='{}', lastName='{}', dob='{}'",
        request.firstName(),
        request.lastName(),
        request.dateOfBirth());
    return userService.getUsers(request);
  } */

  /* @PutMapping("/{id}")
  public void updateUser(@PathVariable Integer id, @RequestBody UserUpdateRequest update) {
    userFacade.updateUser(id, update);
  }

  @DeleteMapping("/{id}")
  public void deleteUser(@PathVariable Integer id) {
    userFacade.deleteUser(id);
  } */
}
