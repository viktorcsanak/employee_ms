package com.example.userservice.config.kafka;

import com.example.userservice.notification.dto.PasswordChangedByAdministratorMessage;
import com.example.userservice.notification.dto.UserCreatedMessage;
import io.micrometer.common.lang.NonNull;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;

@Configuration
@EnableKafka
public class KafkaConfig {

  public static final String USER_CREATED_TOPIC = "user.account.created";
  public static final String USER_CREATED_TOPIC_DLT = USER_CREATED_TOPIC + ".DLT";
  public static final String PASSWORD_CHANGED_TOPIC = "user.account.password.chaged";
  public static final String PASSWORD_CHANGED_TOPIC_DLT = PASSWORD_CHANGED_TOPIC + ".DLT";

  @Bean
  public NewTopic userCreatedTopic() {
    return new NewTopic(USER_CREATED_TOPIC, 1, (short) 1);
  }

  @Bean
  public NewTopic passwordChangedTopic() {
    return new NewTopic(PASSWORD_CHANGED_TOPIC, 1, (short) 1);
  }

  @Bean
  public NewTopic userCreatedDltTopic() {
    return new NewTopic(USER_CREATED_TOPIC, 1, (short) 1);
  }

  @Bean
  public NewTopic passwordChangedDltTopic() {
    return new NewTopic(PASSWORD_CHANGED_TOPIC, 1, (short) 1);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, UserCreatedMessage>
      userCreatedKafkaListenerContainerFactory(
          @NonNull ConsumerFactory<String, UserCreatedMessage> consumerFactory,
          @NonNull DefaultErrorHandler kafkaErrorHandler) {

    final ConcurrentKafkaListenerContainerFactory<String, UserCreatedMessage> factory =
        new ConcurrentKafkaListenerContainerFactory<>();

    factory.setConsumerFactory(consumerFactory);
    factory.setCommonErrorHandler(kafkaErrorHandler);
    return factory;
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, PasswordChangedByAdministratorMessage>
      passwordChangedByAdminKafkaListenerContainerFactory(
          @NonNull ConsumerFactory<String, PasswordChangedByAdministratorMessage> consumerFactory,
          @NonNull DefaultErrorHandler kafkaErrorHandler) {

    final ConcurrentKafkaListenerContainerFactory<String, PasswordChangedByAdministratorMessage>
        factory = new ConcurrentKafkaListenerContainerFactory<>();

    factory.setConsumerFactory(consumerFactory);
    factory.setCommonErrorHandler(kafkaErrorHandler);
    return factory;
  }
}
