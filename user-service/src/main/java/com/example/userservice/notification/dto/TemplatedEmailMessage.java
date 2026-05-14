package com.example.userservice.notification.dto;

import com.example.userservice.notification.EmailTemplate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

public record TemplatedEmailMessage(
    @NotBlank String to,
    @NotBlank String subject,
    @NotNull EmailTemplate template,
    @NotEmpty Map<String, Object> templateData) {}
