package com.example.userservice.notification;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.userservice.notification.dto.PasswordChangedByAdministratorMessage;
import com.example.userservice.notification.dto.TemplatedEmailMessage;
import com.example.userservice.notification.dto.UserCreatedMessage;
import com.example.userservice.notification.emailhandlers.EmailNotificationHandler;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class NotificationDispatcherTest {
  @Test
  void dispatchUsesMatchingHandlerAndSendEmail() {
    EmailSenderService emailSenderService = Mockito.mock(EmailSenderService.class);

    @SuppressWarnings("unchecked")
    EmailNotificationHandler<UserCreatedMessage> handler =
        Mockito.mock(EmailNotificationHandler.class);

    UserCreatedMessage payload = new UserCreatedMessage("john@email.com", "John");

    TemplatedEmailMessage emailMessage =
        new TemplatedEmailMessage(
            "john@email.com",
            EmailTemplate.WELCOME_EMAIL,
            Map.of(
                EmailTemplateVariables.EMAIL,
                payload.email(),
                EmailTemplateVariables.FIRST_NAME,
                payload.firstName()));

    when(handler.eventType()).thenReturn(NotificationEventType.USER_CREATED);
    when(handler.payloadType()).thenReturn(UserCreatedMessage.class);
    when(handler.buildEmail(payload)).thenReturn(emailMessage);

    NotificationDispatcher dispatcher =
        new NotificationDispatcher(List.of(handler), emailSenderService);

    dispatcher.dispatch(NotificationEventType.USER_CREATED, payload);

    verify(handler).buildEmail(payload);
    verify(emailSenderService).send(emailMessage);
  }

  @Test
  void dispatcherThrowsWhenNoHandlerIsRegistered() {
    EmailSenderService emailSenderService = Mockito.mock(EmailSenderService.class);
    NotificationDispatcher dispatcher = new NotificationDispatcher(List.of(), emailSenderService);

    UserCreatedMessage payload = new UserCreatedMessage("viktor@example.com", "Viktor");

    assertThatThrownBy(() -> dispatcher.dispatch(NotificationEventType.USER_CREATED, payload))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("No email handler registered");
  }

  @Test
  void dispatcherThrowsWhenPayloadDoesNotMatchHandler() {
    EmailSenderService emailSenderService = Mockito.mock(EmailSenderService.class);

    @SuppressWarnings("unchecked")
    EmailNotificationHandler<UserCreatedMessage> handler =
        Mockito.mock(EmailNotificationHandler.class);

    PasswordChangedByAdministratorMessage payload =
        new PasswordChangedByAdministratorMessage("john@email.com", "John", "admin@email.com");

    when(handler.eventType()).thenReturn(NotificationEventType.USER_CREATED);
    when(handler.payloadType()).thenReturn(UserCreatedMessage.class);

    NotificationDispatcher dispatcher =
        new NotificationDispatcher(List.of(handler), emailSenderService);

    assertThatThrownBy(() -> dispatcher.dispatch(NotificationEventType.USER_CREATED, payload))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid payload type");
  }

  @Test
  void dispatcherThrowsWhenPayloadDoesNotMatchHandlerString() {
    EmailSenderService emailSenderService = Mockito.mock(EmailSenderService.class);

    @SuppressWarnings("unchecked")
    EmailNotificationHandler<UserCreatedMessage> handler =
        Mockito.mock(EmailNotificationHandler.class);

    when(handler.eventType()).thenReturn(NotificationEventType.USER_CREATED);
    when(handler.payloadType()).thenReturn(UserCreatedMessage.class);

    NotificationDispatcher dispatcher =
        new NotificationDispatcher(List.of(handler), emailSenderService);

    assertThatThrownBy(
            () -> dispatcher.dispatch(NotificationEventType.USER_CREATED, "Wrong Payload"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid payload type");
  }
}
