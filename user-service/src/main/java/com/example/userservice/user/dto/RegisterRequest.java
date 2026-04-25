package com.example.userservice.user.dto;

import com.example.userservice.user.residence.PlaceOfResidence;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record RegisterRequest(
    @NotBlank String firstName,
    String middleName,
    @NotBlank String lastName,
    @NotNull @Past(message = "Date of birth must be in the past") LocalDate dateOfBirth,
    @NotNull LocalDate startOfEmployment,
    @NotBlank @Email String email,
    @NotBlank String gender,
    @NotBlank @Size(min = 6, message = "Password must be at least 6 characters") String password,
    @NotNull PlaceOfResidence placeOfResidence,
    @NotBlank String address,
    @NotBlank String position,
    @NotNull boolean adminPrivileges,
    @NotNull boolean hrManagementAccess) {}
