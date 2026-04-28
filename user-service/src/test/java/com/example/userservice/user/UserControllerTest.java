package com.example.userservice.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.userservice.auth.JwtService;
import com.example.userservice.session.SessionService;
import com.example.userservice.user.dto.UserProfileResponse;
import jakarta.servlet.http.Cookie;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.*;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(UserController.class)
class UserControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private UserFacade userFacade;
  @MockBean private JwtService jwtService;
  @MockBean private SessionService sessionService;

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

    mockMvc
        .perform(
            put("/api/user/management/admin/password/1")
                .contentType("application/json")
                .content(body))
        .andExpect(status().isOk());

    verify(userFacade).changePassword(eq(1), any());
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
