package com.example.userservice.notification;

import com.example.userservice.notification.dto.TemplatedEmailMessage;
import com.example.userservice.notification.emailhandlers.EmailNotificationHandler;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class NotificationDispatcher {

  private final Map<NotificationEventType, EmailNotificationHandler<?>> handlers;
  private final EmailSenderService emailSenderService;

  public NotificationDispatcher(
      List<EmailNotificationHandler<?>> handlers, EmailSenderService emailSenderService) {
    this.handlers =
        handlers.stream()
            .collect(Collectors.toMap(EmailNotificationHandler::eventType, Function.identity()));

    this.emailSenderService = emailSenderService;
  }

  public <T> void dispatch(NotificationEventType eventType, T payload) {
    final EmailNotificationHandler<?> rawHandler = handlers.get(eventType);

    if (rawHandler == null) {
      throw new IllegalArgumentException(
          "No email handler registered for event type: " + eventType);
    }

    dispatchToHandler(rawHandler, payload);
  }

  private <T> void dispatchToHandler(EmailNotificationHandler<?> rawHandler, T payload) {
    final EmailNotificationHandler<T> handler = castHandler(rawHandler, payload);

    final TemplatedEmailMessage emailRequest = handler.buildEmail(payload);

    emailSenderService.send(emailRequest);
  }

  @SuppressWarnings("unchecked")
  private <T> EmailNotificationHandler<T> castHandler(
      EmailNotificationHandler<?> rawHandler, T payload) {
    if (!rawHandler.payloadType().isInstance(payload)) {
      throw new IllegalArgumentException(
          "Invalid payload type. Expected: "
              + rawHandler.payloadType().getSimpleName()
              + ", got: "
              + payload.getClass().getSimpleName());
    }

    return (EmailNotificationHandler<T>) rawHandler;
  }
}
