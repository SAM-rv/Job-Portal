package com.example.Job_Portal.entity;

import com.example.Job_Portal.enums.ApplicationStatus;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Table(name = "JobApplications")

@EntityListeners(AuditingEntityListener.class)
public class JobApplicationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "CandidateProfileEntity_id",unique = true)
    private CandidateProfileEntity candidateProfile;

    @ManyToOne
    @JoinColumn(name = "JobEntity_id",unique = true)
    private JobEntity job;

    @CreatedDate
    private LocalDate appliedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    public JobApplicationEntity() {
    }

    public JobApplicationEntity(Long id, CandidateProfileEntity candidateProfile, JobEntity job, LocalDate appliedDate, ApplicationStatus status) {
        this.id = id;
        this.candidateProfile = candidateProfile;
        this.job = job;
        this.appliedDate = appliedDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CandidateProfileEntity getCandidateProfile() {
        return candidateProfile;
    }

    public void setCandidateProfile(CandidateProfileEntity candidateProfile) {
        this.candidateProfile = candidateProfile;
    }

    public JobEntity getJob() {
        return job;
    }

    public void setJob(JobEntity job) {
        this.job = job;
    }

    @CreatedDate
    public LocalDate getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(LocalDate appliedDate) {
        this.appliedDate = appliedDate;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }
}
