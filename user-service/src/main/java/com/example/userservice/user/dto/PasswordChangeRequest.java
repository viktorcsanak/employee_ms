package com.example.userservice.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordChangeRequest(
    @NotBlank @Size(min = 6, message = "Password must be at least 6 characters long")
        String password) {}
