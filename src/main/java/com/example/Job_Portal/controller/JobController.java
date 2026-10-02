package com.example.Job_Portal.controller;

import com.example.Job_Portal.dto.request.JobRequest;
import com.example.Job_Portal.dto.response.JobResponse;
import com.example.Job_Portal.enums.EmploymentTypes;
import com.example.Job_Portal.enums.JobStatus;
import com.example.Job_Portal.exception.UserNotLoggedInException;
import com.example.Job_Portal.service.JobService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/job-portal")
@CrossOrigin(origins = "http://127.0.0.1:5500", allowCredentials = "true")
public class JobController{
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/job/create")
    public ResponseEntity<JobResponse> createJob(@Valid @RequestBody JobRequest jobRequest, HttpServletRequest request){
        HttpSession session=request.getSession(false);
        if(session==null)
            throw new UserNotLoggedInException("Login necessary");

        Long id= (Long) session.getAttribute("user_id");
        return new ResponseEntity<>(jobService.createJob(jobRequest,id),HttpStatus.CREATED);
    }

    @PutMapping("job/update")
    public ResponseEntity<JobResponse> updateJob(@Valid @RequestBody JobRequest jobRequest, @RequestParam Long job_id, @RequestParam JobStatus status, HttpServletRequest request){
        HttpSession session=request.getSession(false);
        if(session==null)
            throw new UserNotLoggedInException("Login necessary");

        Long id= (Long) session.getAttribute("user_id");
        return new ResponseEntity<>(jobService.updateJob(jobRequest,id,job_id,status),HttpStatus.OK);
    }

    @GetMapping("job/view/all")
    public ResponseEntity<List<JobResponse>> viewAllJobs(HttpServletRequest request) {
        HttpSession session=request.getSession(false);
        if(session==null)
            throw new UserNotLoggedInException("Login necessary");

        return new ResponseEntity<>(jobService.viewAllJobs(),HttpStatus.OK);

    }

    @GetMapping("/job/view")
    public ResponseEntity<List<JobResponse>> viewJobs(HttpServletRequest request) {
        HttpSession session=request.getSession(false);
        if(session==null)
            throw new UserNotLoggedInException("Login necessary");

        Long id= (Long) session.getAttribute("user_id");
        return new ResponseEntity<>(jobService.viewJobs(id),HttpStatus.OK);

    }

    @GetMapping("/job/search&filter")
    public ResponseEntity<List<JobResponse>> searchFilterJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) EmploymentTypes emp_type,
            @RequestParam(required = false) Integer min_exp,
            @RequestParam(required = false) Integer salary
            ){
        return new ResponseEntity<>(jobService.searchFilterJobs(title,location,skill,emp_type,min_exp,salary), HttpStatus.OK);
    }
}