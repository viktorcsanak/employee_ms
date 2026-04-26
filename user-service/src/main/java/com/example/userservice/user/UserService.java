package com.example.userservice.user;

import com.example.userservice.common.exception.UserExistsException;
import com.example.userservice.common.exception.UserNotFoundException;
import com.example.userservice.kafka.KafkaProducer;
import com.example.userservice.notification.dto.UserCreatedMessage;
import com.example.userservice.user.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final UserRepository repo;
  private final KafkaProducer producer;

  public UserService(UserRepository repo, KafkaProducer producer) {
    this.repo = repo;
    this.producer = producer;
  }

  public User register(User user) {
    if (repo.findByEmail(user.getEmail()).isPresent()) {
      throw new UserExistsException("User already exists");
    }
    repo.save(user);
    producer.sendUserCreatedMessage(
        new UserCreatedMessage(user.getEmail(), user.getFirstName(), user.getLastName()));
    return user;
  }

  public User getUserById(Integer id) {
    return repo.findById(id).orElseThrow(() -> new UserNotFoundException("User not found: " + id));
  }
}
