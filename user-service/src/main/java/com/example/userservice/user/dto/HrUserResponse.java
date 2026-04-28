package com.example.userservice.user.dto;

import com.example.userservice.user.residence.PlaceOfResidence;
import java.time.LocalDate;

public record HrUserResponse(
    String email,
    String firstName,
    String middleName,
    String lastName,
    LocalDate dateOfBirth,
    LocalDate startOfEmployment,
    String position,
    String gender,
    String address,
    PlaceOfResidence placeOfResidence) {}
