package com.example.userservice.notification.config;

import com.example.userservice.notification.EmailTemplate;
import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "email-notifications")
public record EmailNotificationProperties(Map<EmailTemplate, TemplateProperties> templates) {
  public TemplateProperties getRequiredTemplate(EmailTemplate template) {
    final TemplateProperties properties = templates.get(template);

    if (properties == null) {
      throw new IllegalArgumentException("No email template configuration found for: " + template);
    }

    return properties;
  }

  public record TemplateProperties(
      String subject, String htmlTemplatePath, List<String> requiredVariables) {}
}
