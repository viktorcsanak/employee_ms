package com.example.userservice.notification;

public enum EmailTemplate {
  WELCOME_EMAIL("email/html/welcome-email"),

  PASSWORD_CHANGED("email/html/password-changed");

  private final String htmlTemplatePath;

  EmailTemplate(String htmlTemplatePath) {
    this.htmlTemplatePath = htmlTemplatePath;
  }

  public String htmlTemplatePath() {
    return htmlTemplatePath;
  }
}
