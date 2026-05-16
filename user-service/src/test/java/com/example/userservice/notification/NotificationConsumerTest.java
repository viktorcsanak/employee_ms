package com.example.userservice.notification;

import static org.mockito.Mockito.verify;

import com.example.userservice.notification.dto.PasswordChangedByAdministratorMessage;
import com.example.userservice.notification.dto.UserCreatedMessage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class NotificationConsumerTest {
  @Test
  void userCreatedListenerDispatchesUserCreatedEvent() {
    NotificationDispatcher dispatcher = Mockito.mock(NotificationDispatcher.class);
    NotificationConsumer consumer = new NotificationConsumer(dispatcher);

    UserCreatedMessage message = new UserCreatedMessage("viktor@example.com", "Viktor");

    consumer.listen(message);

    verify(dispatcher).dispatch(NotificationEventType.USER_CREATED, message);
  }

  @Test
  void passwordChangedListenerDispatchesPasswordChangedEvent() {
    NotificationDispatcher dispatcher = Mockito.mock(NotificationDispatcher.class);
    NotificationConsumer consumer = new NotificationConsumer(dispatcher);

    PasswordChangedByAdministratorMessage message =
        new PasswordChangedByAdministratorMessage(
            "viktor@example.com", "Viktor", "admin@example.com");

    consumer.listen(message);

    verify(dispatcher).dispatch(NotificationEventType.PASSWORD_CHANGED, message);
  }
}
