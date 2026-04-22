package com.example.userservice.auth;

import com.example.userservice.permissions.Role;
import java.time.LocalDate;
import java.util.Set;

public record RegisterRequest(
    String firstName,
    String lastName,
    LocalDate dateOfBirth,
    String email,
    String password,
    String city,
    String postalCode,
    String address,
    Set<Role> roles) {}
