package com.example.userservice.user.dto;

public record AdminUserResponse(
    Integer id,
    String email,
    String firstName,
    String middleName,
    String lastName,
    Boolean adminPrivileges,
    Boolean hrManagementAccess) {}
