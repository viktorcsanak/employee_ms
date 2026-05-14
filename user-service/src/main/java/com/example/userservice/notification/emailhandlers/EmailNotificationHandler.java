package com.example.userservice.notification.emailhandlers;

import com.example.userservice.notification.NotificationEventType;
import com.example.userservice.notification.dto.TemplatedEmailMessage;

public interface EmailNotificationHandler<T> {
  NotificationEventType eventType();

  Class<T> payloadType();

  TemplatedEmailMessage buildEmail(T payload);
}
