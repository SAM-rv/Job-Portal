package com.example.Job_Portal.dto.request;
import com.example.Job_Portal.enums.EmploymentTypes;
import com.example.Job_Portal.enums.JobStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class JobRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String company;

    @NotBlank
    private String location;

    @Min(0)
    @NotNull
    private Integer minSalary;
    @Max(10000000)
    @NotNull
    private Integer maxSalary;

    @NotNull
    private List< @NotBlank String>skills;

    @Min(0)
    @NotNull
    private Integer minExperience;

    @NotNull
    private EmploymentTypes empType;

    @NotNull
    private LocalDate deadline;

    public JobRequest() {
    }

    public JobRequest(String title, String description, String company, String location, Integer minSalary, Integer maxSalary, List<@NotBlank String> skills, Integer minExperience, EmploymentTypes empType, LocalDate deadline) {
        this.title = title;
        this.description = description;
        this.company = company;
        this.location = location;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
        this.skills = skills;
        this.minExperience = minExperience;
        this.empType = empType;
        this.deadline = deadline;
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

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }
}
