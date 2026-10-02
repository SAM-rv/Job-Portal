package com.example.Job_Portal.dto.response;

import com.example.Job_Portal.enums.Role;

import java.time.LocalDate;

public record RegistrationResponse(
        Long id,
        String fullName,
        String email,
        Role role,
        LocalDate created_At
) {
}
