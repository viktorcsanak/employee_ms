package com.example.userservice.notification;

import com.example.userservice.notification.dto.RenderedEmail;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailTemplateRendererService {
  private final TemplateEngine templateEngine;

  public EmailTemplateRendererService(TemplateEngine templateEngine) {
    this.templateEngine = templateEngine;
  }

  public RenderedEmail render(EmailTemplate template, Map<String, Object> variables) {
    final Context context = new Context();
    variables.forEach(context::setVariable);

    final String html = templateEngine.process(template.htmlTemplatePath(), context);

    return new RenderedEmail(html);
  }
}
