package com.example.userservice.kafka;

import static com.example.userservice.config.kafka.KafkaConfig.USER_CREATED_TOPIC;

import com.example.userservice.notification.dto.UserCreatedMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {

  private final KafkaTemplate<String, UserCreatedMessage> userCreatedTemplate;

  public KafkaProducer(KafkaTemplate<String, UserCreatedMessage> userCreatedTemplate) {
    this.userCreatedTemplate = userCreatedTemplate;
  }

  public void sendUserCreatedMessage(UserCreatedMessage message) {
    userCreatedTemplate.send(USER_CREATED_TOPIC, message);
  }
}
