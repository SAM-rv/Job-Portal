package com.example.Job_Portal.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "Candidate")
public class CandidateProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true,nullable = false)
    private String phoneNumber;
    private String location;
    private String education;

    @ElementCollection
    private List<String> skills;

    private Integer yearsOfExperience;
    private String resumeUrl;

    @OneToOne
    @JoinColumn(name="UserEntity_id",unique = true)
    private UserEntity user;

    @OneToMany(mappedBy = "candidateProfile")
    private List<JobApplicationEntity> jobApplication;

    public CandidateProfileEntity() {
    }

    public CandidateProfileEntity(Long id, String phoneNumber, String location, String education, List<String> skills, Integer yearsOfExperience, String resumeUrl, UserEntity user, List<JobApplicationEntity> jobApplication) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.education = education;
        this.skills = skills;
        this.yearsOfExperience = yearsOfExperience;
        this.resumeUrl = resumeUrl;
        this.user = user;
        this.jobApplication = jobApplication;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public List<JobApplicationEntity> getJobApplication() {
        return jobApplication;
    }

    public void setJobApplication(List<JobApplicationEntity> jobApplication) {
        this.jobApplication = jobApplication;
    }
}
