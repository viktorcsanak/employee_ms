package com.example.userservice.user;

import com.example.userservice.common.exception.UserExistsException;
import com.example.userservice.common.exception.UserNotFoundException;
import com.example.userservice.user.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final UserRepository repo;

  public UserService(UserRepository repo) {
    this.repo = repo;
  }

  public User register(User user) {
    if (repo.findByEmail(user.getEmail()).isPresent()) {
      throw new UserExistsException("User already exists");
    }
    repo.save(user);
    return user;
  }

  public User getUserById(Integer id) {
    return repo.findById(id).orElseThrow(() -> new UserNotFoundException("User not found: " + id));
  }
}
