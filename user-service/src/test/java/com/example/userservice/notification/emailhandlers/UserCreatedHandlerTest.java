package com.example.userservice.notification.emailhandlers;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.userservice.notification.EmailTemplate;
import com.example.userservice.notification.EmailTemplateVariables;
import com.example.userservice.notification.NotificationEventType;
import com.example.userservice.notification.dto.TemplatedEmailMessage;
import com.example.userservice.notification.dto.UserCreatedMessage;
import org.junit.jupiter.api.Test;

public class UserCreatedHandlerTest {
  private final UserCreatedHandler handler = new UserCreatedHandler();

  @Test
  void eventTypeReturnsUserCreated() {
    assertThat(handler.eventType()).isEqualTo(NotificationEventType.USER_CREATED);
  }

  @Test
  void payloadTypeReturnsUserCreatedMessageClass() {
    assertThat(handler.payloadType()).isEqualTo(UserCreatedMessage.class);
  }

  @Test
  void buildEmailMapsUserCreatedMessageToTemplatedEmailMessage() {
    UserCreatedMessage message = new UserCreatedMessage("john@email.com", "John");

    TemplatedEmailMessage result = handler.buildEmail(message);

    assertThat(result.to()).isEqualTo(message.email());
    assertThat(result.template()).isEqualTo(EmailTemplate.WELCOME_EMAIL);
    assertThat(result.templateData())
        .containsEntry(EmailTemplateVariables.FIRST_NAME, message.firstName())
        .containsEntry(EmailTemplateVariables.EMAIL, message.email());
  }
}
