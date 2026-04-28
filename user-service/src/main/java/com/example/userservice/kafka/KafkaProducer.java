package com.example.userservice.kafka;

import static com.example.userservice.config.kafka.KafkaConfig.USER_CREATED_TOPIC;

import com.example.userservice.notification.dto.UserCreatedMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {

  private final KafkaTemplate<String, String> kafkaTemplate;

  public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void sendUserCreatedMessage(UserCreatedMessage message) {
    kafkaTemplate.send(USER_CREATED_TOPIC, message.toString());
  }
}
