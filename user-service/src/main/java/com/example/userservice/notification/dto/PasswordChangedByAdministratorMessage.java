package com.example.userservice.notification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PasswordChangedByAdministratorMessage(
    @NotNull @Email String email, @NotBlank String firstName, @NotBlank String adminEmail) {}
