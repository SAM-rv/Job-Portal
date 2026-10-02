package com.example.Job_Portal.dto.request;

import jakarta.validation.constraints.NotNull;

public class JobApplicationRequest {

    @NotNull
    private Long job_Id;

    public JobApplicationRequest() {
    }

    public JobApplicationRequest(Long job_Id) {
        this.job_Id = job_Id;
    }

    public Long getJob_Id() {
        return job_Id;
    }

    public void setJob_Id(Long job_Id) {
        this.job_Id = job_Id;
    }
}


