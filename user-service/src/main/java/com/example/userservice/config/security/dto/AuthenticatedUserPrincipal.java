package com.example.userservice.config.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthenticatedUserPrincipal(@NotNull Integer id, @NotBlank String email) {}
