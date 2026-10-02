package com.example.Job_Portal.dto.response;

import com.example.Job_Portal.enums.Role;

public record LoginResponse(
        Long id,
        String fullname,
        String email,
        Role role
) {
}
