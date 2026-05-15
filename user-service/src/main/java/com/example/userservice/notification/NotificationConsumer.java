package com.example.userservice.notification;

import static com.example.userservice.config.kafka.KafkaConfig.PASSWORD_CHANGED_TOPIC;
import static com.example.userservice.config.kafka.KafkaConfig.USER_CREATED_TOPIC;

import com.example.userservice.notification.dto.PasswordChangedByAdministratorMessage;
import com.example.userservice.notification.dto.UserCreatedMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {
  private final NotificationDispatcher notificationDispatcher;

  public NotificationConsumer(NotificationDispatcher notificationDispatcher) {
    this.notificationDispatcher = notificationDispatcher;
  }

  @KafkaListener(
      topics = USER_CREATED_TOPIC,
      containerFactory = "userCreatedKafkaListenerContainerFactory")
  public void listen(UserCreatedMessage message) {
    notificationDispatcher.dispatch(NotificationEventType.USER_CREATED, message);
  }

  @KafkaListener(
      topics = PASSWORD_CHANGED_TOPIC,
      containerFactory = "passwordChangedByAdminKafkaListenerContainerFactory")
  public void listen(PasswordChangedByAdministratorMessage message) {
    notificationDispatcher.dispatch(NotificationEventType.PASSWORD_CHANGED, message);
  }
}
