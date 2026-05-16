package com.example.userservice.user;

import com.example.userservice.common.exception.UserNotFoundException;
import com.example.userservice.permissions.Role;
import com.example.userservice.session.SessionService;
import com.example.userservice.user.dto.PasswordChangeRequest;
import com.example.userservice.user.dto.PermissionChangeRequest;
import com.example.userservice.user.model.User;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserAdministrationService {
  private static final Logger log = LoggerFactory.getLogger(UserController.class);
  private final UserRepository repo;
  private final PasswordEncoder passwordEncoder;
  private final SessionService sessionService;

  public UserAdministrationService(
      UserRepository repo, PasswordEncoder passwordEncoder, SessionService sessionService) {
    this.repo = repo;
    this.passwordEncoder = passwordEncoder;
    this.sessionService = sessionService;
  }

  public void grantOrRevokePermissions(Integer id, PermissionChangeRequest request) {
    final User user = repo.getReferenceById(id);

    final Set<Role> roles = user.getRoles();
    if (request.adminPrivileges()) {
      roles.add(Role.ADMIN);
      log.info("Admin permission granted for userId({})", id);
    } else {
      roles.remove(Role.ADMIN);
      log.info("Admin permission revoked for userId({})", id);
    }
    if (request.hrManagementAccess()) {
      roles.add(Role.HR);
      log.info("HR permission granted for userId({})", id);
    } else {
      roles.remove(Role.HR);
      log.info("HR permission revoked for userId({})", id);
    }

    roles.add(Role.BASIC);

    user.setRoles(roles);
    repo.save(user);
  }

  public void deleteUser(Integer id) {
    if (!repo.existsById(id)) {
      throw new UserNotFoundException("User not found: " + id);
    }
    repo.deleteById(id);
    sessionService.removeAllUserSessions(id);
  }

  public User changePassword(Integer id, PasswordChangeRequest request) {
    if (!repo.existsById(id)) {
      throw new UserNotFoundException("User not found: " + id);
    }

    final User user = repo.getReferenceById(id);
    user.setPassword(passwordEncoder.encode(request.password()));
    return repo.saveAndFlush(user);
  }
}
