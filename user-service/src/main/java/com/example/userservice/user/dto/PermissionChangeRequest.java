package com.example.userservice.user.dto;

import jakarta.validation.constraints.NotNull;

public record PermissionChangeRequest(
    @NotNull Boolean adminPrivileges, @NotNull Boolean hrManagementAccess) {}
