package com.example.userservice.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.userservice.auth.JwtService;
import com.example.userservice.user.dto.*;
import com.example.userservice.user.model.User;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserFacadeTest {

  @Mock private UserService userService;
  @Mock private UserQueryService queryService;
  @Mock private RegistrationService registrationService;
  @Mock private UserMapper mapper;
  @Mock private JwtService jwtService;
  @Mock private UserAdministrationService adminService;

  @InjectMocks private UserFacade facade;

  @Test
  void getProfileFromToken_delegatesCorrectly() {
    when(jwtService.verifyToken("token")).thenReturn(1);

    User user = new User();
    when(userService.getUserById(1)).thenReturn(user);

    UserProfileResponse response = mock(UserProfileResponse.class);
    when(mapper.toProfile(user)).thenReturn(response);

    UserProfileResponse result = facade.getProfileFromToken("token");

    assertEquals(response, result);
  }

  @Test
  void getAllUsersForAdmin_mapsUsers() {
    User user = new User();

    when(queryService.search(null)).thenReturn(List.of(user));

    AdminUserResponse dto = mock(AdminUserResponse.class);
    when(mapper.toAdmin(user)).thenReturn(dto);

    List<AdminUserResponse> result = facade.getAllUsersForAdmin();

    assertEquals(1, result.size());
    assertEquals(dto, result.get(0));
  }

  @Test
  void register_delegates() {
    RegisterRequest req = mock(RegisterRequest.class);
    User user = new User();

    when(registrationService.register(req)).thenReturn(user);

    assertEquals(user, facade.register(req));
  }

  @Test
  void adminCalls_delegateToService() {
    PermissionChangeRequest p = mock(PermissionChangeRequest.class);
    PasswordChangeRequest pw = mock(PasswordChangeRequest.class);

    facade.grantOrRevokePermissions(1, p);
    facade.changePassword(1, pw);
    facade.deleteUser(1);

    verify(adminService).grantOrRevokePermissions(1, p);
    verify(adminService).changePassword(1, pw);
    verify(adminService).deleteUser(1);
  }
}
