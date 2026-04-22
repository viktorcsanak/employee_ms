package com.example.userservice.user;

public record AdminUserResponse(
    Integer id,
    String email,
    String firstName,
    String middleName,
    String lastName,
    boolean adminPrivileges,
    boolean hrManagementAccess) {}
