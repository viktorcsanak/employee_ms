package com.example.userservice.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.userservice.auth.JwtService;
import com.example.userservice.config.security.dto.AuthenticatedUserPrincipal;
import com.example.userservice.session.SessionService;
import com.example.userservice.user.dto.PasswordChangeRequest;
import com.example.userservice.user.dto.UserProfileResponse;
import jakarta.servlet.http.Cookie;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(UserController.class)
class UserControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private UserFacade userFacade;
  @MockBean private JwtService jwtService;
  @MockBean private SessionService sessionService;

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void getUser_success() throws Exception {
    when(userFacade.getProfileFromToken("token")).thenReturn(mock(UserProfileResponse.class));

    mockMvc
        .perform(get("/api/user").cookie(new Cookie("token", "token")))
        .andExpect(status().isOk());

    verify(userFacade).getProfileFromToken("token");
  }

  @Test
  void register_callsFacade() throws Exception {
    String body =
        """
        {
          "firstName": "John",
          "middleName": null,
          "lastName": "Doe",
          "dateOfBirth": "2000-01-01",
          "startOfEmployment": "2023-01-01",
          "email": "test@test.com",
          "gender": "M",
          "password": "secret123",
          "confirmPassword": "secret123",
          "placeOfResidence": {
            "city": "Novi Sad",
            "postalCode": "21000"
          },
          "address": "Street 1",
          "position": "DEV",
          "adminPrivileges": true,
          "hrManagementAccess": false
        }
        """;

    mockMvc
        .perform(
            post("/api/user/management/admin/register")
                .contentType("application/json")
                .content(body))
        .andExpect(status().isOk());

    verify(userFacade).register(any());
  }

  @Test
  void getAllUsers_admin_success() throws Exception {
    when(userFacade.getAllUsersForAdmin()).thenReturn(List.of());

    mockMvc.perform(get("/api/user/management/admin")).andExpect(status().isOk());

    verify(userFacade).getAllUsersForAdmin();
  }

  @Test
  void grantPermissions_callsFacade() throws Exception {
    String body =
        """
        {
          "adminPrivileges": true,
          "hrManagementAccess": false
        }
        """;

    mockMvc
        .perform(
            put("/api/user/management/admin/permissions/1")
                .contentType("application/json")
                .content(body))
        .andExpect(status().isOk());

    verify(userFacade).grantOrRevokePermissions(eq(1), any());
  }

  @Test
  void changePassword_callsFacade() throws Exception {
    String body = """
        {
          "password": "newpass123"
        }
        """;

    AuthenticatedUserPrincipal authUser = new AuthenticatedUserPrincipal(2, "admin@mail.com");

    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(
            authUser, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    mockMvc
        .perform(
            put("/api/user/management/admin/password/1")
                .contentType("application/json")
                .content(body))
        .andExpect(status().isOk());

    verify(userFacade).changePassword(eq(1), any(PasswordChangeRequest.class), same(authUser));
  }

  @Test
  void deleteUser_callsFacade() throws Exception {
    mockMvc.perform(delete("/api/user/management/admin/delete/1")).andExpect(status().isOk());

    verify(userFacade).deleteUser(1);
  }

  @Test
  void getAllUsers_hr_success() throws Exception {
    when(userFacade.getAllUsersForHr()).thenReturn(List.of());

    mockMvc.perform(get("/api/user/management/hr")).andExpect(status().isOk());

    verify(userFacade).getAllUsersForHr();
  }
}
