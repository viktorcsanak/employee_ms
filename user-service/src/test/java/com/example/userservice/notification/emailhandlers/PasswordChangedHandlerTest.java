package com.example.userservice.notification.emailhandlers;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.userservice.notification.EmailTemplate;
import com.example.userservice.notification.EmailTemplateVariables;
import com.example.userservice.notification.NotificationEventType;
import com.example.userservice.notification.dto.PasswordChangedByAdministratorMessage;
import com.example.userservice.notification.dto.TemplatedEmailMessage;
import org.junit.jupiter.api.Test;

public class PasswordChangedHandlerTest {
  private final PasswordChangedHandler handler = new PasswordChangedHandler();

  @Test
  void eventTypeReturnsPasswordChanged() {
    assertThat(handler.eventType()).isEqualTo(NotificationEventType.PASSWORD_CHANGED);
  }

  @Test
  void payloadTypeReturnsPasswordChangedByAdministratorMessageClass() {
    assertThat(handler.payloadType()).isEqualTo(PasswordChangedByAdministratorMessage.class);
  }

  @Test
  void buildEmailMapsPasswordChangedByAdministratorMessageToTemplatedEmailMessage() {
    PasswordChangedByAdministratorMessage message =
        new PasswordChangedByAdministratorMessage("john@email.com", "John", "admin@email.com");

    TemplatedEmailMessage result = handler.buildEmail(message);

    assertThat(result.to()).isEqualTo(message.email());
    assertThat(result.template()).isEqualTo(EmailTemplate.PASSWORD_CHANGED);
    assertThat(result.templateData())
        .containsEntry(EmailTemplateVariables.FIRST_NAME, message.firstName())
        .containsEntry(EmailTemplateVariables.EMAIL, message.email())
        .containsEntry(EmailTemplateVariables.ADMIN_EMAIL, message.adminEmail());
  }
}
