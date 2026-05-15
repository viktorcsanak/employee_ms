package com.example.userservice.notification;

import com.example.userservice.notification.dto.RenderedEmail;
import com.example.userservice.notification.dto.TemplatedEmailMessage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {
  private final JavaMailSender mailSender;
  private final EmailTemplateRendererService templateRendererService;
  private final String senderEmail;

  public EmailSenderService(
      JavaMailSender mailSender,
      EmailTemplateRendererService templateRendererService,
      @Value("${mail.sender.email}") String senderEmail) {
    this.mailSender = mailSender;
    this.templateRendererService = templateRendererService;
    this.senderEmail = senderEmail;
  }

  public void send(TemplatedEmailMessage message) {
    final RenderedEmail renderedEmail =
        templateRendererService.render(message.template(), message.templateData());

    try {
      final MimeMessage mimeMessage = mailSender.createMimeMessage();

      final MimeMessageHelper helper =
          new MimeMessageHelper(
              mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

      helper.setFrom(senderEmail);
      helper.setTo(message.to());
      helper.setSubject(renderedEmail.subject());

      // plain text fallback + HTML version
      helper.setText(renderedEmail.html(), true);

      mailSender.send(mimeMessage);

    } catch (MessagingException e) {
      System.out.println("Messaging exception " + e.getMessage());
    }
  }
}
