package com.example.userservice.common.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.userservice.config.security.JwtSessionFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest(
    controllers = GlobalExceptionHandlerTest.ExceptionThrowingController.class,
    excludeFilters =
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtSessionFilter.class))
@Import(GlobalExceptionHandlerTest.ExceptionThrowingController.class)
@AutoConfigureMockMvc(addFilters = false)
class GlobalExceptionHandlerTest {

  @Autowired private MockMvc mockMvc;

  // --- test-only controller ---
  @RestController
  static class ExceptionThrowingController {

    @GetMapping("/test/not-found")
    void notFound() {
      throw new UserNotFoundException("ignored");
    }

    @GetMapping("/test/exists")
    void exists() {
      throw new UserExistsException("ignored");
    }

    @GetMapping("/test/unauthorized")
    void unauthorized() {
      throw new UserUnauthorizedException("ignored");
    }

    @GetMapping("/test/jwt")
    void jwt() {
      throw new io.jsonwebtoken.JwtException("bad token");
    }

    @GetMapping("/test/illegal")
    void illegal() {
      throw new IllegalArgumentException();
    }

    @GetMapping("/test/null")
    void nullEx() {
      throw new NullPointerException("missing value");
    }

    @GetMapping("/test/db")
    void db() {
      throw new DataAccessException("db failure") {};
    }

    @GetMapping("/test/generic")
    void generic() throws Exception {
      throw new Exception("boom");
    }
  }

  @Test
  void userNotFound_returns404() throws Exception {
    mockMvc
        .perform(get("/test/not-found"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorMessage").value("Invalid credentials!"));
  }

  @Test
  void userExists_returns409() throws Exception {
    mockMvc
        .perform(get("/test/exists"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.errorMessage").value("Email is already taken!"));
  }

  @Test
  void unauthorized_returns401() throws Exception {
    mockMvc
        .perform(get("/test/unauthorized"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.errorMessage").value("Invalid or expired token"));
  }

  @Test
  void jwt_returns401() throws Exception {
    mockMvc
        .perform(get("/test/jwt"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.errorMessage").value("Invalid or expired token"));
  }

  @Test
  void illegalArgument_returns400() throws Exception {
    mockMvc
        .perform(get("/test/illegal"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorMessage").value("Invalid request"));
  }

  @Test
  void nullPointer_returns400_withMessage() throws Exception {
    mockMvc
        .perform(get("/test/null"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorMessage").value("missing value"));
  }

  @Test
  void dataAccess_returns500() throws Exception {
    mockMvc
        .perform(get("/test/db"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.errorMessage").value("Internal server error"));
  }

  @Test
  void genericException_returns500() throws Exception {
    mockMvc
        .perform(get("/test/generic"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.errorMessage").value("Internal server error"));
  }
}
