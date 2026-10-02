package com.example.Job_Portal.dto.response;

import com.example.Job_Portal.enums.ApplicationStatus;

import java.time.LocalDate;

public record JobApplicationResponse(
        Long AppliCation_Id,
        CandidateProfileResponse candidateProfileResponse,
        JobResponse job,
        LocalDate Applieddate,
        ApplicationStatus status
) {
}
