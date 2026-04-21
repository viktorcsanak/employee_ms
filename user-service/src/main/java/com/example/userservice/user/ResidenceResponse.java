package com.example.userservice.user;

public record ResidenceResponse(
    String city,
    String postalCode,
    String address
) {}