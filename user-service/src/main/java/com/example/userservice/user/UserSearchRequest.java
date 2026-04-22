package com.example.userservice.user;

import lombok.Builder;

@Builder
public record UserSearchRequest(
    String firstName, String lastName, String dateOfBirth, String email) {}
