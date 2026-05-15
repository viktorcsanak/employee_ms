package com.example.userservice.common.exception;

public class EmailSendingException extends RuntimeException {
  public EmailSendingException(String message, Throwable cause) {
    super(message, cause);
  }
}
