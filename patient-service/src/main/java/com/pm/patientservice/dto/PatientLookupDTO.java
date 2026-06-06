package com.pm.patientservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PatientLookupDTO(
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be a valid email address")
        String email
) {}
