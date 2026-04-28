package com.example.userservice.user.dto;

import lombok.Builder;

@Builder
public record UserUpdateRequest(String firstName, String lastName, String dateOfBirth) {}
