package com.example.Job_Portal.dto.request;

import jakarta.validation.constraints.NotBlank;

public class RecruiterProfileRequest {

    @NotBlank
    private String companyName;

    @NotBlank
    private String companyDescription;

    @NotBlank
    private String companyLocation;

    public RecruiterProfileRequest() {
    }

    public RecruiterProfileRequest(String companyName, String companyDescription, String companyLocation) {
        this.companyName = companyName;
        this.companyDescription = companyDescription;
        this.companyLocation = companyLocation;
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
}
