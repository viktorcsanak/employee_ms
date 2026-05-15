package com.example.userservice.notification;

import static com.example.userservice.config.kafka.KafkaConfig.PASSWORD_CHANGED_TOPIC_DLT;
import static com.example.userservice.config.kafka.KafkaConfig.USER_CREATED_TOPIC_DLT;

import com.example.userservice.config.kafka.KafkaErrorHandlingConfig;
import com.example.userservice.notification.dto.PasswordChangedByAdministratorMessage;
import com.example.userservice.notification.dto.UserCreatedMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationDltConsumer {
  public NotificationDltConsumer() {}

  @KafkaListener(topics = USER_CREATED_TOPIC_DLT)
  public void listen(UserCreatedMessage message) {
    System.out.println(
        "Failed to send "
            + UserCreatedMessage.class.getName()
            + " after "
            + KafkaErrorHandlingConfig.RETRIES
            + " retries: "
            + message);
  }

  @KafkaListener(topics = PASSWORD_CHANGED_TOPIC_DLT)
  public void listen(PasswordChangedByAdministratorMessage message) {
    System.out.println(
        "Failed to send "
            + PasswordChangedByAdministratorMessage.class.getName()
            + " after "
            + KafkaErrorHandlingConfig.RETRIES
            + " retries: "
            + message);
  }
}
