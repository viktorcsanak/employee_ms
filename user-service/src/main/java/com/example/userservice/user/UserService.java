package com.example.userservice.user;

import com.example.userservice.auth.RegisterRequest;
import com.example.userservice.permissions.Role;
import com.example.userservice.residence.PlaceOfResidence;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final UserRepository repo;
  private final PasswordEncoder passwordEncoder;

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  public UserService(UserRepository repo, PasswordEncoder passwordEncoder) {
    this.repo = repo;
    this.passwordEncoder = passwordEncoder;
  }

  public User register(RegisterRequest request) {

    if (repo.findByEmail(request.email()).isPresent()) {
      throw new UserExistsException("User already exists");
    }

    final PlaceOfResidence residence =
        repo.findAll().stream()
            .map(User::getPlaceOfResidence)
            .filter(
                r ->
                    r.getCity().equals(request.city())
                        && r.getPostalCode().equals(request.postalCode()))
            .findFirst()
            .orElseGet(
                () ->
                    PlaceOfResidence.builder()
                        .city(request.city())
                        .postalCode(request.postalCode())
                        .build());

    final User user =
        User.builder()
            .firstName(request.firstName())
            .lastName(request.lastName())
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .dateOfBirth(request.dateOfBirth())
            .address(request.address())
            .placeOfResidence(residence)
            .roles(request.roles() == null ? Set.of(Role.BASIC) : request.roles())
            .build();

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
