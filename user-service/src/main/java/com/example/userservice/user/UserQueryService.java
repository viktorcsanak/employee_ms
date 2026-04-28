package com.example.userservice.user;

import com.example.userservice.user.dto.UserSearchRequest;
import com.example.userservice.user.model.User;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class UserQueryService {
  private final UserRepository repo;

  public UserQueryService(UserRepository repo) {
    this.repo = repo;
  }

  public List<User> search(UserSearchRequest request) {

    if (request == null) {
      return repo.findAll();
    }

    Specification<User> spec = (root, query, cb) -> cb.conjunction();

    if (request.firstName() != null) {
      spec = spec.and((r, q, cb) -> cb.equal(r.get("firstName"), request.firstName()));
    }

    if (request.email() != null) {
      spec = spec.and((r, q, cb) -> cb.equal(r.get("email"), request.email()));
    }

    return repo.findAll(spec);
  }
}
