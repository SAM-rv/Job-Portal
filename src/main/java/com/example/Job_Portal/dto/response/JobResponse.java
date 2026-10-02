package com.example.Job_Portal.dto.response;

import com.example.Job_Portal.enums.EmploymentTypes;
import com.example.Job_Portal.enums.JobStatus;

import java.time.LocalDate;
import java.util.List;

public record JobResponse(
        Long Job_Id,
        String title,
        String description,
        String company,
        String location,
        Integer minSalary,
        Integer maxSalary,
        List<String> skills,
        Integer minExperience,
        EmploymentTypes empType,
        LocalDate postedDate,
        LocalDate deadline,
        JobStatus status,
        Long recruiter_id,
        String recruiterFullName,
        String recruiterEmail
) {
}
