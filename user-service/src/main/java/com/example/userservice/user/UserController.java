package com.example.userservice.user;

import com.example.userservice.auth.JwtService;
import com.example.userservice.auth.RegisterRequest;
import com.example.userservice.common.ApiResponse;
import io.jsonwebtoken.JwtException;
import java.time.format.DateTimeParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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

  @PutMapping("/{id}")
  public void updateUser(@PathVariable Integer id, @RequestBody UserUpdateRequest update) {
    userService.updateUser(id, update);
  }

  @DeleteMapping("/{id}")
  public void deleteUser(@PathVariable Integer id) {
    userService.deleteUser(id);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse> handleException(IllegalArgumentException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.builder().errorMessage("Invalid request").build());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<ApiResponse> handleException(NullPointerException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.builder().errorMessage(ex.getMessage()).build());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(DateTimeParseException.class)
  public ResponseEntity<ApiResponse> handleException(DateTimeParseException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.builder().errorMessage(ex.getMessage()).build());
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ApiResponse> handleException(UserNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiResponse.builder().errorMessage(ex.getMessage()).build());
  }

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler(JwtException.class)
  public ResponseEntity<ApiResponse> handleException(JwtException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiResponse.builder().errorMessage(ex.getMessage()).build());
  }
}
