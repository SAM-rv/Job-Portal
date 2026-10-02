package com.example.Job_Portal.dto.response;

import com.example.Job_Portal.enums.Role;

public record RecruiterProfileResponse(
        Long id,
        String fullName,
        String email,
        Role role,

        String companyName,
        String companyDescription,
        String companyLocation
) {
}
