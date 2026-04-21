package com.example.userservice.user;

import lombok.Builder;

@Builder
public record UserUpdateRequest(
    String firstName,
    String lastName,
    String dateOfBirth
) {}
