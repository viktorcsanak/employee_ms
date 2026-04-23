package com.example.userservice.user;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.userservice.auth.JwtService;
import com.example.userservice.user.dto.UserProfileResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
class UserControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private UserService userService;

  @MockitoBean private JwtService jwtService;

  @Test
  void getUser_success() throws Exception {
    when(jwtService.verifyToken("token")).thenReturn(1);

    when(userService.getUserProfile(1)).thenReturn(mock(UserProfileResponse.class));

    mockMvc
        .perform(get("/api/user").cookie(new jakarta.servlet.http.Cookie("token", "token")))
        .andExpect(status().isOk());
  }

  @Test
  void deleteUser_callsService() throws Exception {
    mockMvc.perform(delete("/api/user/1")).andExpect(status().isOk());

    verify(userService).deleteUser(1);
  }
}
