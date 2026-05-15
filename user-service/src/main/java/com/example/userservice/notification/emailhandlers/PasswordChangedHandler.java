package com.example.userservice.notification.emailhandlers;

import com.example.userservice.notification.EmailTemplate;
import com.example.userservice.notification.EmailTemplateVariables;
import com.example.userservice.notification.NotificationEventType;
import com.example.userservice.notification.dto.PasswordChangedByAdministratorMessage;
import com.example.userservice.notification.dto.TemplatedEmailMessage;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class PasswordChangedHandler
    implements EmailNotificationHandler<PasswordChangedByAdministratorMessage> {
  @Override
  public NotificationEventType eventType() {
    return NotificationEventType.PASSWORD_CHANGED;
  }

  @Override
  public Class<PasswordChangedByAdministratorMessage> payloadType() {
    return PasswordChangedByAdministratorMessage.class;
  }

  @Override
  public TemplatedEmailMessage buildEmail(PasswordChangedByAdministratorMessage message) {
    return new TemplatedEmailMessage(
        message.email(),
        EmailTemplate.PASSWORD_CHANGED,
        Map.of(
            EmailTemplateVariables.FIRST_NAME,
            message.firstName(),
            EmailTemplateVariables.EMAIL,
            message.email(),
            EmailTemplateVariables.ADMIN_EMAIL,
            message.adminEmail()));
  }
}
