package com.example.userservice.kafka;

import static org.mockito.Mockito.verify;

import com.example.userservice.config.kafka.KafkaConfig;
import com.example.userservice.notification.dto.PasswordChangedByAdministratorMessage;
import com.example.userservice.notification.dto.UserCreatedMessage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;

class KafkaProducerTest {
  @Test
  void sendUserCreatedMessageSendsToUserCreatedTopic() {
    @SuppressWarnings("unchecked")
    KafkaTemplate<String, UserCreatedMessage> userCreatedTemplate =
        Mockito.mock(KafkaTemplate.class);

    @SuppressWarnings("unchecked")
    KafkaTemplate<String, PasswordChangedByAdministratorMessage> passwordChangedTemplate =
        Mockito.mock(KafkaTemplate.class);

    KafkaProducer producer = new KafkaProducer(userCreatedTemplate, passwordChangedTemplate);

    UserCreatedMessage message = new UserCreatedMessage("viktor@example.com", "Viktor");

    producer.sendUserCreatedMessage(message);

    verify(userCreatedTemplate).send(KafkaConfig.USER_CREATED_TOPIC, message.email(), message);
  }

  @Test
  void sendPasswordChangedMessageSendsToPasswordChangedTopic() {
    @SuppressWarnings("unchecked")
    KafkaTemplate<String, UserCreatedMessage> userCreatedTemplate =
        Mockito.mock(KafkaTemplate.class);

    @SuppressWarnings("unchecked")
    KafkaTemplate<String, PasswordChangedByAdministratorMessage> passwordChangedTemplate =
        Mockito.mock(KafkaTemplate.class);

    KafkaProducer producer = new KafkaProducer(userCreatedTemplate, passwordChangedTemplate);

    PasswordChangedByAdministratorMessage message =
        new PasswordChangedByAdministratorMessage(
            "viktor@example.com", "Viktor", "admin@example.com");

    producer.sendPasswordChangedByAdminMessage(message);

    verify(passwordChangedTemplate)
        .send(KafkaConfig.PASSWORD_CHANGED_TOPIC, message.email(), message);
  }
}
