package com.example.userservice.notification.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserCreatedMessage(
    @NotNull @Email String email, @NotBlank String firstName, @NotBlank String lastName) {}
