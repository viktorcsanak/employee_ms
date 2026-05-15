package com.example.userservice.config.kafka;

import com.example.userservice.common.exception.EmailSendingException;
import jakarta.validation.constraints.NotNull;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorHandlingConfig {
  public static final Integer RETRIES = 3;
  public static final Integer RETRY_DELAY = 5000;

  @Bean
  public DefaultErrorHandler kafkaErrorHandler(
      @NotNull KafkaTemplate<Object, Object> kafkaTemplate) {
    final DeadLetterPublishingRecoverer recoverer =
        new DeadLetterPublishingRecoverer(
            kafkaTemplate,
            (record, exception) -> new TopicPartition(record.topic() + ".DLT", record.partition()));

    final FixedBackOff backOff = new FixedBackOff(RETRY_DELAY, RETRIES);

    final DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);

    errorHandler.addRetryableExceptions(EmailSendingException.class);
    errorHandler.addNotRetryableExceptions(
        IllegalArgumentException.class, MessageConversionException.class);

    return errorHandler;
  }
}
