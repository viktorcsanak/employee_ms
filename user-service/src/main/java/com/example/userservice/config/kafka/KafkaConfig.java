package com.example.userservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

@Configuration
@EnableKafka
public class KafkaConfig {

  public static final String USER_CREATED_TOPIC = "user.account.created";
  public static final String PASSWORD_CHANGED_TOPIC = "user.account.password.chaged";

  @Bean
  public NewTopic userCreatedTopic() {
    return new NewTopic(USER_CREATED_TOPIC, 1, (short) 1);
  }

  @Bean
  public NewTopic passwordChangedTopic() {
    return new NewTopic(PASSWORD_CHANGED_TOPIC, 1, (short) 1);
  }

  @Bean
  public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>>
      kafkaListenerContainerFactory(ConsumerFactory<String, String> consumerFactory) {

    final ConcurrentKafkaListenerContainerFactory<String, String> factory =
        new ConcurrentKafkaListenerContainerFactory<>();

    factory.setConsumerFactory(consumerFactory);
    return factory;
  }
}
