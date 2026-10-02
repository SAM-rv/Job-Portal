package com.example.Job_Portal.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="Recruiter")
public class RecruiterProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String companyDescription;

    @Column(nullable = false)
    private String companyLocation;

    @OneToOne
    @JoinColumn(name = "UserEntity_id",unique = true)
    private UserEntity user;

    @OneToMany(mappedBy = "recruiterProfile")
    private List<JobEntity> jobs;

    public RecruiterProfileEntity() {
    }

    public RecruiterProfileEntity(Long id, String companyName, String companyDescription, String companyLocation, UserEntity user, List<JobEntity> jobs) {
        this.id = id;
        this.companyName = companyName;
        this.companyDescription = companyDescription;
        this.companyLocation = companyLocation;
        this.user = user;
        this.jobs = jobs;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyDescription() {
        return companyDescription;
    }

    public void setCompanyDescription(String companyDescription) {
        this.companyDescription = companyDescription;
    }

    public String getCompanyLocation() {
        return companyLocation;
    }

    public void setCompanyLocation(String companyLocation) {
        this.companyLocation = companyLocation;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public List<JobEntity> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobEntity> jobs) {
        this.jobs = jobs;
    }
}
