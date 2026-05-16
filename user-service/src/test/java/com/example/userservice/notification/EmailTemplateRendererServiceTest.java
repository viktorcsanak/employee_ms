package com.example.userservice.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.example.userservice.notification.config.EmailNotificationProperties;
import com.example.userservice.notification.config.EmailNotificationProperties.TemplateProperties;
import com.example.userservice.notification.dto.RenderedEmail;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@ExtendWith(MockitoExtension.class)
class EmailTemplateRendererServiceTest {

  @Mock private TemplateEngine templateEngine;

  private EmailNotificationProperties emailNotificationProperties;
  private EmailTemplateRendererService emailTemplateRendererService;

  private Map<String, Object> templateData;
  private TemplateProperties welcomeTemplateProperties;

  @BeforeEach
  void setUp() {
    templateData =
        Map.of(
            EmailTemplateVariables.FIRST_NAME,
            "Viktor",
            EmailTemplateVariables.EMAIL,
            "viktor@example.com");

    welcomeTemplateProperties =
        new TemplateProperties(
            "Welcome!",
            "email/html/welcome-email",
            List.of(EmailTemplateVariables.FIRST_NAME, EmailTemplateVariables.EMAIL));

    emailNotificationProperties =
        new EmailNotificationProperties(
            Map.of(EmailTemplate.WELCOME_EMAIL, welcomeTemplateProperties));

    emailTemplateRendererService =
        new EmailTemplateRendererService(templateEngine, emailNotificationProperties);
  }

  @Test
  void renderRendersHtmlAndSubject() {
    when(templateEngine.process(eq("email/html/welcome-email"), any(Context.class)))
        .thenReturn("<h1>Welcome, Viktor!</h1>");

    RenderedEmail result =
        emailTemplateRendererService.render(EmailTemplate.WELCOME_EMAIL, templateData);

    assertThat(result.subject()).isEqualTo("Welcome!");
    assertThat(result.html()).isEqualTo("<h1>Welcome, Viktor!</h1>");
  }

  @Test
  void renderThrowsWhenRequiredVariableIsMissing() {
    Map<String, Object> incompleteTemplateData =
        Map.of(EmailTemplateVariables.FIRST_NAME, "Viktor");

    assertThatThrownBy(
            () ->
                emailTemplateRendererService.render(
                    EmailTemplate.WELCOME_EMAIL, incompleteTemplateData))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Missing required variable")
        .hasMessageContaining(EmailTemplateVariables.EMAIL);
  }

  @Test
  void renderThrowsWhenTemplateConfigurationIsMissing() {
    EmailNotificationProperties emptyProperties = new EmailNotificationProperties(Map.of());

    EmailTemplateRendererService serviceWithMissingConfiguration =
        new EmailTemplateRendererService(templateEngine, emptyProperties);

    assertThatThrownBy(
            () -> serviceWithMissingConfiguration.render(EmailTemplate.WELCOME_EMAIL, templateData))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("No email template configuration found");
  }
}
