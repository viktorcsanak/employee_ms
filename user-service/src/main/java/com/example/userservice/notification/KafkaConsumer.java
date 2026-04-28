package com.example.userservice.notification;

import static com.example.userservice.config.kafka.KafkaConfig.USER_CREATED_TOPIC;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

  @KafkaListener(topics = USER_CREATED_TOPIC)
  public void listen(String message) {
    System.out.println(message);
  }
}
