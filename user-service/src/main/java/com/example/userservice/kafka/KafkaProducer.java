package com.example.userservice.kafka;

import static com.example.userservice.config.kafka.KafkaConfig.PASSWORD_CHANGED_TOPIC;
import static com.example.userservice.config.kafka.KafkaConfig.USER_CREATED_TOPIC;

import com.example.userservice.notification.dto.PasswordChangedByAdministratorMessage;
import com.example.userservice.notification.dto.UserCreatedMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {

  private final KafkaTemplate<String, UserCreatedMessage> userCreatedTemplate;
  private final KafkaTemplate<String, PasswordChangedByAdministratorMessage>
      passwordChangedTemplate;

  public KafkaProducer(
      KafkaTemplate<String, UserCreatedMessage> userCreatedTemplate,
      KafkaTemplate<String, PasswordChangedByAdministratorMessage> passwordChangedTemplate) {
    this.userCreatedTemplate = userCreatedTemplate;
    this.passwordChangedTemplate = passwordChangedTemplate;
  }

  public void sendUserCreatedMessage(UserCreatedMessage message) {
    userCreatedTemplate.send(USER_CREATED_TOPIC, message);
  }

  public void sendPasswordChangedByAdminMessage(PasswordChangedByAdministratorMessage message) {
    passwordChangedTemplate.send(PASSWORD_CHANGED_TOPIC, message);
  }
}
