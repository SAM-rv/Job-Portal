package com.example.Job_Portal.entity;

import com.example.Job_Portal.enums.EmploymentTypes;
import com.example.Job_Portal.enums.JobStatus;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Jobs")
public class JobEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private String location;


    private Integer minSalary;
    private Integer maxSalary;

    @ElementCollection
    private List<String> skills;

    private Integer minExperience;

    @Enumerated(EnumType.STRING )
    private EmploymentTypes empType;

    @CreatedDate
    @Column(updatable = false)
    private LocalDate postedDate;

    private LocalDate applicationDeadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status;

    @ManyToOne
    @JoinColumn(name = "RecruiterProfileEntity_id")
    private RecruiterProfileEntity recruiterProfile;

    @OneToMany(mappedBy = "job")
    private List<JobApplicationEntity> jobApplication;

    public JobEntity() {
    }

    public JobEntity(Long id, String title, String description, String company, String location, Integer minSalary, Integer maxSalary, List<String> skills, Integer minExperience, EmploymentTypes empType, LocalDate postedDate, LocalDate applicationDeadline, JobStatus status, RecruiterProfileEntity recruiterProfile, List<JobApplicationEntity> jobApplication) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.company = company;
        this.location = location;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
        this.skills = skills;
        this.minExperience = minExperience;
        this.empType = empType;
        this.postedDate = postedDate;
        this.applicationDeadline = applicationDeadline;
        this.status = status;
        this.recruiterProfile = recruiterProfile;
        this.jobApplication = jobApplication;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(Integer minSalary) {
        this.minSalary = minSalary;
    }

    public Integer getMaxSalary() {
        return maxSalary;
    }

    public void setMaxSalary(Integer maxSalary) {
        this.maxSalary = maxSalary;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public Integer getMinExperience() {
        return minExperience;
    }

    public void setMinExperience(Integer minExperience) {
        this.minExperience = minExperience;
    }

    public EmploymentTypes getEmpType() {
        return empType;
    }

    public void setEmpType(EmploymentTypes empType) {
        this.empType = empType;
    }

    public LocalDate getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(LocalDate postedDate) {
        this.postedDate = postedDate;
    }

    public LocalDate getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(LocalDate applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public RecruiterProfileEntity getRecruiterProfile() {
        return recruiterProfile;
    }

    public void setRecruiterProfile(RecruiterProfileEntity recruiterProfile) {
        this.recruiterProfile = recruiterProfile;
    }

    public List<JobApplicationEntity> getJobApplication() {
        return jobApplication;
    }

    public void setJobApplication(List<JobApplicationEntity> jobApplication) {
        this.jobApplication = jobApplication;
    }
}
