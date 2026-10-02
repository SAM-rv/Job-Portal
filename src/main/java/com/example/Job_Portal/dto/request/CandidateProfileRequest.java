package com.example.Job_Portal.dto.request;

import jakarta.validation.constraints.*;

import java.util.List;

public class CandidateProfileRequest {

    @Size(min = 10,max = 10,message = "Phone Number Should Be Of 10 Digit")
    @Pattern(regexp = "^[0-9]{10}$",message = "Should Only Contain Digits")
    private String phoneNumber;

    @NotBlank
    private String location;

    @NotBlank
    private String education;

    @NotNull
    private List<@NotBlank String> skills;

    @Min(0)
    private Integer yearOfExperience;

    private String resumeUrl;

    public CandidateProfileRequest() {
    }

    public CandidateProfileRequest(String phoneNumber, String location, String education, List<@NotBlank String> skills, Integer yearOfExperience, String resumeUrl) {
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.education = education;
        this.skills = skills;
        this.yearOfExperience = yearOfExperience;
        this.resumeUrl = resumeUrl;
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

    public Integer getYearOfExperience() {
        return yearOfExperience;
    }

    public void setYearOfExperience(Integer yearOfExperience) {
        this.yearOfExperience = yearOfExperience;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }
}
