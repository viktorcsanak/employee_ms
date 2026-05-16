package com.example.userservice.config.kafka;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KafkaConfigTest {
  private final KafkaConfig kafkaConfig = new KafkaConfig();

  @Test
  void userCreatedDltTopicUsesDltName() {
    NewTopic topic = kafkaConfig.userCreatedDltTopic();

    assertThat(topic.name()).isEqualTo(KafkaConfig.USER_CREATED_TOPIC_DLT);
  }

  @Test
  void passwordChangedDltTopicUsesDltName() {
    NewTopic topic = kafkaConfig.passwordChangedDltTopic();

    assertThat(topic.name()).isEqualTo(KafkaConfig.PASSWORD_CHANGED_TOPIC_DLT);
  }

  @Test
  void passwordChangedTopicNameIsSpelledCorrectly() {
    assertThat(KafkaConfig.PASSWORD_CHANGED_TOPIC).isEqualTo("user.account.password.changed");
  }
}
