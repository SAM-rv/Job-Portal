package com.example.Job_Portal.repository;

import com.example.Job_Portal.dto.response.JobApplicationResponse;
import com.example.Job_Portal.entity.CandidateProfileEntity;
import com.example.Job_Portal.entity.JobApplicationEntity;
import com.example.Job_Portal.entity.JobEntity;
import com.example.Job_Portal.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplicationEntity,Long> {
    public List<JobApplicationEntity> findByCandidateProfile(CandidateProfileEntity candidateProfile);

    public List<JobApplicationEntity> findByJobAndStatusNot(JobEntity job, ApplicationStatus status);
}
