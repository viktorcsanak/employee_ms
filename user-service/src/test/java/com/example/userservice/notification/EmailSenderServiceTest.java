package com.example.userservice.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.userservice.common.exception.EmailSendingException;
import com.example.userservice.notification.dto.RenderedEmail;
import com.example.userservice.notification.dto.TemplatedEmailMessage;
import jakarta.mail.BodyPart;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
public class EmailSenderServiceTest {
  @Mock private JavaMailSender mailSender;
  @Mock private EmailTemplateRendererService templateRendererService;

  private EmailSenderService emailSenderService;
  private EmailSenderService invalidEmailSenderService;
  private TemplatedEmailMessage templatedEmailMessage;
  private RenderedEmail renderedEmail;

  @BeforeEach
  void setUp() {
    emailSenderService =
        new EmailSenderService(mailSender, templateRendererService, "admin@mail.com");
    invalidEmailSenderService =
        new EmailSenderService(mailSender, templateRendererService, "admin@@mail.com");

    templatedEmailMessage =
        new TemplatedEmailMessage(
            "john@mail.com",
            EmailTemplate.WELCOME_EMAIL,
            Map.of(
                EmailTemplateVariables.EMAIL,
                "john@mail.com",
                EmailTemplateVariables.FIRST_NAME,
                "John"));

    renderedEmail =
        new RenderedEmail(
            """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="UTF-8">
              <title>Welcome</title>
            </head>
            <body>
              <h1>Welcome, <span>John</span>!</h1>
              <p>Your account has been created successfully.</p>
              <p>You can now log in with:<strong>john@mail.com</strong></p>
            </body>
            </html>""",
            "Welcome!");
  }

  @Test
  void sendCreatesAndSendsMimeMessage() throws Exception {
    Session session = Session.getInstance(new Properties());
    MimeMessage mimeMessage = new MimeMessage(session);

    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    when(templateRendererService.render(
            templatedEmailMessage.template(), templatedEmailMessage.templateData()))
        .thenReturn(renderedEmail);

    emailSenderService.send(templatedEmailMessage);
    verify(mailSender).send(mimeMessage);

    assertThat(mimeMessage.getAllRecipients()[0].toString()).isEqualTo(templatedEmailMessage.to());
    assertThat(mimeMessage.getSubject()).isEqualTo(renderedEmail.subject());
    assertThat(getTextFromMimeMessage(mimeMessage)).isEqualTo(renderedEmail.html());
  }

  @Test
  void sendThrowsEmailSendingExceptionWhenMailSenderFails() throws Exception {
    Session session = Session.getInstance(new Properties());
    MimeMessage mimeMessage = new MimeMessage(session);

    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    when(templateRendererService.render(
            templatedEmailMessage.template(), templatedEmailMessage.templateData()))
        .thenReturn(renderedEmail);

    Mockito.doThrow(new MailSendException("SMTP failed")).when(mailSender).send(mimeMessage);

    assertThatThrownBy(() -> emailSenderService.send(templatedEmailMessage))
        .isInstanceOf(EmailSendingException.class)
        .hasMessageContaining("Failed to send email message");
  }

  @Test
  void sendThrowsEmailSendingExceptionOnMessagingException() throws Exception {
    Session session = Session.getInstance(new Properties());
    MimeMessage mimeMessage = new MimeMessage(session);

    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    when(templateRendererService.render(
            templatedEmailMessage.template(), templatedEmailMessage.templateData()))
        .thenReturn(renderedEmail);

    assertThatThrownBy(() -> invalidEmailSenderService.send(templatedEmailMessage))
        .isInstanceOf(EmailSendingException.class)
        .hasMessageContaining("Failed to render email message");
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
}
