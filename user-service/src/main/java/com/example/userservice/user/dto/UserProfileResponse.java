package com.example.userservice.user.dto;

public record UserProfileResponse(
    String email,
    String firstName,
    String middleName,
    String lastName,
    String dateOfBirth,
    String gender,
    ResidenceResponse placeOfResidence,
    String position,
    String startOfEmployment,
    Boolean adminPrivileges,
    Boolean hrManagementAccess) {}
