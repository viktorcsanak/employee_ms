package com.example.userservice.user.dto;

public record PermissionChangeRequest(boolean adminPrivileges, boolean hrManagementAccess) {}
