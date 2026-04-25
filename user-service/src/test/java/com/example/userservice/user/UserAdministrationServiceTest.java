package com.example.userservice.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.userservice.common.exception.UserNotFoundException;
import com.example.userservice.permissions.Role;
import com.example.userservice.session.SessionService;
import com.example.userservice.user.dto.PasswordChangeRequest;
import com.example.userservice.user.dto.PermissionChangeRequest;
import com.example.userservice.user.model.User;
import java.util.EnumSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserAdministrationServiceTest {

  @Mock private UserRepository repo;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private SessionService sessionService;

  @InjectMocks private UserAdministrationService service;

  // -------------------------
  // grantOrRevokePermissions
  // -------------------------

  @Test
  void grantPermissions_addsAdminAndHR_andAlwaysBasic() {

    User user = User.builder().roles(EnumSet.noneOf(Role.class)).build();

    when(repo.getReferenceById(1)).thenReturn(user);

    PermissionChangeRequest request = mock(PermissionChangeRequest.class);
    when(request.adminPrivileges()).thenReturn(true);
    when(request.hrManagementAccess()).thenReturn(true);

    service.grantOrRevokePermissions(1, request);

    assertTrue(user.getRoles().contains(Role.ADMIN));
    assertTrue(user.getRoles().contains(Role.HR));
    assertTrue(user.getRoles().contains(Role.BASIC));

    verify(repo).save(user);
  }

  @Test
  void revokePermissions_removesRoles_butKeepsBasic() {

    User user = User.builder().roles(EnumSet.of(Role.ADMIN, Role.HR, Role.BASIC)).build();

    when(repo.getReferenceById(1)).thenReturn(user);

    PermissionChangeRequest request = mock(PermissionChangeRequest.class);
    when(request.adminPrivileges()).thenReturn(false);
    when(request.hrManagementAccess()).thenReturn(false);

    service.grantOrRevokePermissions(1, request);

    assertFalse(user.getRoles().contains(Role.ADMIN));
    assertFalse(user.getRoles().contains(Role.HR));
    assertTrue(user.getRoles().contains(Role.BASIC));

    verify(repo).save(user);
  }

  // -------------------------
  // deleteUser
  // -------------------------

  @Test
  void deleteUser_success_deletesUserAndSessions() {

    when(repo.existsById(1)).thenReturn(true);

    service.deleteUser(1);

    verify(repo).deleteById(1);
    verify(sessionService).invalidateAllUserSessions(1);
  }

  @Test
  void deleteUser_notFound_throws() {

    when(repo.existsById(1)).thenReturn(false);

    assertThrows(UserNotFoundException.class, () -> service.deleteUser(1));

    verify(repo, never()).deleteById(any());
    verifyNoInteractions(sessionService);
  }

  // -------------------------
  // changePassword
  // -------------------------

  @Test
  void changePassword_encodesAndSaves() {

    User user = User.builder().password("old").build();

    when(repo.existsById(1)).thenReturn(true);
    when(repo.getReferenceById(1)).thenReturn(user);
    when(passwordEncoder.encode("newPass")).thenReturn("hashed");

    PasswordChangeRequest request = mock(PasswordChangeRequest.class);
    when(request.password()).thenReturn("newPass");

    service.changePassword(1, request);

    assertEquals("hashed", user.getPassword());

    verify(repo).save(user);
  }

  @Test
  void changePassword_userNotFound_throws() {

    when(repo.existsById(1)).thenReturn(false);

    PasswordChangeRequest request = mock(PasswordChangeRequest.class);

    assertThrows(UserNotFoundException.class, () -> service.changePassword(1, request));

    verify(repo, never()).save(any());
    verifyNoInteractions(passwordEncoder);
  }
}
