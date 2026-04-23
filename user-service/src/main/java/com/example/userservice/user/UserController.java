package com.example.userservice.user;

import com.example.userservice.auth.JwtService;
import com.example.userservice.auth.RegisterRequest;
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

  private final UserService userService;
  private final JwtService jwtService;

  public UserController(UserService userService, JwtService jwtService) {
    this.userService = userService;
    this.jwtService = jwtService;
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

  @PostMapping("/register")
  public User register(@RequestBody RegisterRequest request) {
    return userService.register(request);
  }

  @GetMapping
  @PreAuthorize("hasRole('BASIC')")
  public UserProfileResponse getUser(@CookieValue(name = "token") String token) {
    final Integer id = jwtService.verifyToken(token);
    return userService.getUserProfile(id);
  }

  @GetMapping("/management/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public List<AdminUserResponse> getAllUsersForAdmin(@CookieValue(name = "token") String token) {
    return userService.getAllUsersForAdmin();
  }

  @PutMapping("/{id}")
  public void updateUser(@PathVariable Integer id, @RequestBody UserUpdateRequest update) {
    userService.updateUser(id, update);
  }

  @DeleteMapping("/{id}")
  public void deleteUser(@PathVariable Integer id) {
    userService.deleteUser(id);
  }
}
