package com.example.userservice.user;

import com.example.userservice.common.exception.UserExistsException;
import com.example.userservice.common.exception.UserNotFoundException;
import com.example.userservice.permissions.Role;
import com.example.userservice.user.dto.AdminUserResponse;
import com.example.userservice.user.dto.ResidenceResponse;
import com.example.userservice.user.dto.UserProfileResponse;
import com.example.userservice.user.dto.UserSearchRequest;
import com.example.userservice.user.dto.UserUpdateRequest;
import com.example.userservice.user.model.User;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final UserRepository repo;

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  public UserService(UserRepository repo) {
    this.repo = repo;
  }

  public User register(User user) {
    if (repo.findByEmail(user.getEmail()).isPresent()) {
      throw new UserExistsException("User already exists");
    }
    return repo.save(user);
  }

  public User getUserById(Integer id) {
    return repo.findById(id).orElseThrow(() -> new UserNotFoundException("User not found: " + id));
  }

  public UserProfileResponse getUserProfile(Integer id) {
    final User user = getUserById(id);

    return new UserProfileResponse(
        user.getEmail(),
        user.getFirstName(),
        user.getMiddleName(),
        user.getLastName(),
        user.getDateOfBirth().toString(),
        user.getGender(),
        new ResidenceResponse(
            user.getPlaceOfResidence().getCity(),
            user.getPlaceOfResidence().getPostalCode(),
            user.getAddress()),
        user.getPosition(),
        user.getStartOfEmployment() != null ? user.getStartOfEmployment().toString() : null,
        user.getRoles().contains(Role.ADMIN),
        user.getRoles().contains(Role.HR));
  }

  public List<AdminUserResponse> getAllUsersForAdmin() {
    return repo.findAll().stream()
        .map(
            user ->
                new AdminUserResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getMiddleName(),
                    user.getLastName(),
                    user.getRoles().contains(Role.ADMIN),
                    user.getRoles().contains(Role.HR)))
        .toList();
  }

  public void deleteUser(Integer id) {
    if (!repo.existsById(id)) {
      throw new UserNotFoundException("User not found: " + id);
    }
    repo.deleteById(id);
  }

  public User updateUser(Integer id, UserUpdateRequest request) {
    final User user = getUserById(id);

    if (request.firstName() != null && !request.firstName().isBlank()) {
      user.setFirstName(request.firstName());
    }

    if (request.lastName() != null && !request.lastName().isBlank()) {
      user.setLastName(request.lastName());
    }

    if (request.dateOfBirth() != null && !request.dateOfBirth().isBlank()) {
      final LocalDate dob = LocalDate.parse(request.dateOfBirth(), FORMATTER);
      user.setDateOfBirth(dob);
    }

    return repo.save(user);
  }

  public List<User> getUsers(UserSearchRequest request) {
    Specification<User> spec = (root, query, cb) -> cb.conjunction();

    if (request == null) {
      return repo.findAll();
    }

    if (request.firstName() != null && !request.firstName().isBlank()) {
      spec = spec.and((root, query, cb) -> cb.equal(root.get("firstName"), request.firstName()));
    }

    if (request.lastName() != null && !request.lastName().isBlank()) {
      spec = spec.and((root, query, cb) -> cb.equal(root.get("lastName"), request.lastName()));
    }

    if (request.dateOfBirth() != null) {
      spec =
          spec.and((root, query, cb) -> cb.equal(root.get("dateOfBirth"), request.dateOfBirth()));
    }

    if (request.email() != null && !request.email().isBlank()) {
      spec = spec.and((root, query, cb) -> cb.equal(root.get("email"), request.email()));
    }

    return repo.findAll(spec);
  }

  public long countUsers() {
    return repo.count();
  }
}
