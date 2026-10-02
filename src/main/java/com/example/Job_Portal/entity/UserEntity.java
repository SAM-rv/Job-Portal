package com.example.Job_Portal.entity;
import com.example.Job_Portal.enums.Role;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Table(name = "Users")
@EntityListeners(AuditingEntityListener.class)
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullname;

    @Column(unique = true , nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,updatable = false)
    private Role role;

    @CreatedDate
    @Column(nullable = false)
    private LocalDate createdDate;

    @OneToOne(mappedBy = "user")
    private CandidateProfileEntity candidateProfile;

    @OneToOne(mappedBy = "user")
    private RecruiterProfileEntity recruiterProfile;

    public UserEntity() {
    }

    public UserEntity(Long id, String fullname, String email, String password, Role role, LocalDate createdDate, CandidateProfileEntity candidateProfile, RecruiterProfileEntity recruiterProfile) {
        this.id = id;
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdDate = createdDate;
        this.candidateProfile = candidateProfile;
        this.recruiterProfile = recruiterProfile;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public CandidateProfileEntity getCandidateProfile() {
        return candidateProfile;
    }

    public void setCandidateProfile(CandidateProfileEntity candidateProfile) {
        this.candidateProfile = candidateProfile;
    }

    public RecruiterProfileEntity getRecruiterProfile() {
        return recruiterProfile;
    }

    public void setRecruiterProfile(RecruiterProfileEntity recruiterProfile) {
        this.recruiterProfile = recruiterProfile;
    }
}


