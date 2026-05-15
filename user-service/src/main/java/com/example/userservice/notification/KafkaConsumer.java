package com.example.userservice.notification;

import static com.example.userservice.config.kafka.KafkaConfig.PASSWORD_CHANGED_TOPIC;
import static com.example.userservice.config.kafka.KafkaConfig.USER_CREATED_TOPIC;

import com.example.userservice.notification.dto.PasswordChangedByAdministratorMessage;
import com.example.userservice.notification.dto.UserCreatedMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
  private final NotificationDispatcher notificationDispatcher;

  public KafkaConsumer(NotificationDispatcher notificationDispatcher) {
    this.notificationDispatcher = notificationDispatcher;
  }

  @KafkaListener(topics = USER_CREATED_TOPIC)
  public void listen(UserCreatedMessage message) {
    notificationDispatcher.dispatch(NotificationEventType.USER_CREATED, message);
  }

  @KafkaListener(topics = PASSWORD_CHANGED_TOPIC)
  public void listen(PasswordChangedByAdministratorMessage message) {
    System.out.println("Password change message consumed: " + message);
    notificationDispatcher.dispatch(NotificationEventType.PASSWORD_CHANGED, message);
  }
}
