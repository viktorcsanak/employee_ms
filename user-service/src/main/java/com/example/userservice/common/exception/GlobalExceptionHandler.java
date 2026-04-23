package com.example.userservice.common.exception;

import com.example.userservice.common.api.ApiResponse;
import io.jsonwebtoken.JwtException;
import java.time.format.DateTimeParseException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ApiResponse> handleUserNotFoundException(UserNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiResponse.builder().errorMessage("Invalid credentials!").build());
  }

  @ExceptionHandler(UserExistsException.class)
  public ResponseEntity<ApiResponse> handleUserExistsException(UserExistsException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ApiResponse.builder().errorMessage("Email is already taken!").build());
  }

  @ExceptionHandler(UserUnauthorizedException.class)
  public ResponseEntity<ApiResponse> handleUserUnauthorizedException(UserUnauthorizedException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ApiResponse.builder().errorMessage("Invalid or expired token").build());
  }

  @ExceptionHandler(JwtException.class)
  public ResponseEntity<ApiResponse> handleJwt(JwtException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ApiResponse.builder().errorMessage("Invalid or expired token").build());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.builder().errorMessage("Invalid request").build());
  }

  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<ApiResponse> handleNullPointerException(NullPointerException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.builder().errorMessage(ex.getMessage()).build());
  }

  @ExceptionHandler(DateTimeParseException.class)
  public ResponseEntity<ApiResponse> handleDateTimeParseException(DateTimeParseException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.builder().errorMessage(ex.getMessage()).build());
  }

  @ExceptionHandler(DataAccessException.class)
  public ResponseEntity<ApiResponse> handleDataAccessException(DataAccessException ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.builder().errorMessage("Internal server error").build());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse> handleGeneric(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.builder().errorMessage("Internal server error").build());
  }
}
