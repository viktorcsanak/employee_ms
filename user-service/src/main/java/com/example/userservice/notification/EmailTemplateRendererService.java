package com.example.userservice.notification;

import com.example.userservice.notification.config.EmailNotificationProperties;
import com.example.userservice.notification.config.EmailNotificationProperties.TemplateProperties;
import com.example.userservice.notification.dto.RenderedEmail;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailTemplateRendererService {
  private final TemplateEngine templateEngine;
  private final EmailNotificationProperties emailNotificationProperties;

  public EmailTemplateRendererService(
      TemplateEngine templateEngine, EmailNotificationProperties emailNotificationProperties) {
    this.templateEngine = templateEngine;
    this.emailNotificationProperties = emailNotificationProperties;
  }

  public RenderedEmail render(EmailTemplate template, Map<String, Object> variables) {
    final TemplateProperties templateProperties =
        emailNotificationProperties.getRequiredTemplate(template);
    validateRequiredVariables(template, templateProperties, variables);

    final Context context = new Context();
    variables.forEach(context::setVariable);

    final String html = templateEngine.process(templateProperties.htmlTemplatePath(), context);

    return new RenderedEmail(html, templateProperties.subject());
  }

  public void validateRequiredVariables(
      EmailTemplate template,
      TemplateProperties templateProperties,
      Map<String, Object> variables) {
    for (String key : templateProperties.requiredVariables()) {
      if (!variables.containsKey(key)) {
        throw new IllegalArgumentException(
            "Missing required variable '"
                + key
                + "'', for template"
                + template
                + " email can not be rendered!");
      }
    }
  }
}
