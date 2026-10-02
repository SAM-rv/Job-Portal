package com.example.Job_Portal.dto.response;

import com.example.Job_Portal.enums.Role;

import java.util.List;

public record CandidateProfileResponse(
        Long id,
        String fullName,
        String email,
        Role role,


        String phoneNumber,
        String location,
        String education,
        List<String>skills,
        Integer yearOfExperience,
        String resumeUrl
) {
}
