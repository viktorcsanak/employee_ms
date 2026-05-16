package com.example.userservice.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.userservice.notification.config.EmailNotificationProperties;
import com.example.userservice.notification.dto.TemplatedEmailMessage;
import jakarta.mail.BodyPart;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@SpringBootTest(
    classes = {
      EmailSenderService.class,
      EmailTemplateRendererService.class,
      EmailSenderServiceIT.TestConfig.class
    },
    properties = {
      "mail.sender.email=admin@mail.com",
      "spring.config.import=classpath:email-notifications.yml"
    })
@EnableConfigurationProperties(EmailNotificationProperties.class)
public class EmailSenderServiceIT {
  @MockBean private JavaMailSender mailSender;

  @Autowired private EmailSenderService emailSenderService;

  private TemplatedEmailMessage templatedEmailMessage;

  @BeforeEach
  void setUp() {
    templatedEmailMessage =
        new TemplatedEmailMessage(
            "john@mail.com",
            EmailTemplate.WELCOME_EMAIL,
            Map.of(
                EmailTemplateVariables.EMAIL,
                "john@mail.com",
                EmailTemplateVariables.FIRST_NAME,
                "John"));
  }

  @Test
  void sendWelcomeEmailUsesRealTemplateFile() throws Exception {
    MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));

    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

    emailSenderService.send(templatedEmailMessage);
    verify(mailSender).send(mimeMessage);

    assertThat(mimeMessage.getAllRecipients()[0].toString()).isEqualTo(templatedEmailMessage.to());
    assertThat(mimeMessage.getSubject()).isEqualTo("Welcome!");
    assertThat(getTextFromMimeMessage(mimeMessage))
        .contains("Welcome")
        .contains("John")
        .contains("john@mail.com")
        .contains("Your account has been created successfully");
  }

  private String getTextFromMimeMessage(MimeMessage mimeMessage) throws Exception {
    return extractText(mimeMessage);
  }

  private String extractText(Object part) throws Exception {
    if (part instanceof MimeMessage message) {
      return extractText(message.getContent());
    }

    if (part instanceof String text) {
      return text;
    }

    if (part instanceof Multipart multipart) {
      StringBuilder result = new StringBuilder();

      for (int i = 0; i < multipart.getCount(); i++) {
        BodyPart bodyPart = multipart.getBodyPart(i);
        result.append(extractText(bodyPart));
      }

      return result.toString();
    }

    if (part instanceof BodyPart bodyPart) {
      return extractText(bodyPart.getContent());
    }

    return "";
  }

  @TestConfiguration
  static class TestConfig {

    @Bean
    TemplateEngine templateEngine() {
      ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();

      resolver.setPrefix("templates/");
      resolver.setSuffix(".html");
      resolver.setTemplateMode(TemplateMode.HTML);
      resolver.setCharacterEncoding("UTF-8");
      resolver.setCacheable(false);
      resolver.setCheckExistence(true);

      SpringTemplateEngine engine = new SpringTemplateEngine();
      engine.setTemplateResolver(resolver);

      return engine;
    }
  }
}
