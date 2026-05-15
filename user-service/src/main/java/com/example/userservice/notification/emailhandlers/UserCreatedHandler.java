package com.example.userservice.notification.emailhandlers;

import com.example.userservice.notification.EmailTemplate;
import com.example.userservice.notification.EmailTemplateVariables;
import com.example.userservice.notification.NotificationEventType;
import com.example.userservice.notification.dto.TemplatedEmailMessage;
import com.example.userservice.notification.dto.UserCreatedMessage;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class UserCreatedHandler implements EmailNotificationHandler<UserCreatedMessage> {
  @Override
  public NotificationEventType eventType() {
    return NotificationEventType.USER_CREATED;
  }

  @Override
  public Class<UserCreatedMessage> payloadType() {
    return UserCreatedMessage.class;
  }

  @Override
  public TemplatedEmailMessage buildEmail(UserCreatedMessage message) {
    return new TemplatedEmailMessage(
        message.email(),
        EmailTemplate.WELCOME_EMAIL,
        Map.of(
            EmailTemplateVariables.FIRST_NAME,
            message.firstName(),
            EmailTemplateVariables.EMAIL,
            message.email()));
  }
}
