package com.example.Job_Portal.controller;

import com.example.Job_Portal.dto.request.JobApplicationRequest;
import com.example.Job_Portal.dto.response.JobApplicationResponse;
import com.example.Job_Portal.enums.ApplicationStatus;
import com.example.Job_Portal.exception.UserNotLoggedInException;
import com.example.Job_Portal.service.JobApplicationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/job-portal")
@CrossOrigin(origins = "http://127.0.0.1:5500", allowCredentials = "true")
public class JobApplicationController {
    private final JobApplicationService jobApplicationService;

    public JobApplicationController(JobApplicationService jobApplicationService) {
        this.jobApplicationService = jobApplicationService;
    }

    @PostMapping("/job/application/create")
    public ResponseEntity<JobApplicationResponse> createApplication(@RequestParam Long job_id, HttpServletRequest request) {
        HttpSession session=request.getSession(false);
        if(session==null)
            throw new UserNotLoggedInException("Login Necessary");

        Long id= (Long) session.getAttribute("user_id");
        return new ResponseEntity<>(jobApplicationService.createApplication(job_id,id), HttpStatus.CREATED);
    }

    @GetMapping("/job/application/recruiter/view")
    public ResponseEntity<List<JobApplicationResponse>> viewRecruiterApplication(@RequestParam Long job_id,HttpServletRequest request) {
        HttpSession session=request.getSession(false);
        if(session==null)
            throw new UserNotLoggedInException("Login Necessary");
        Long id= (Long) session.getAttribute("user_id");
        return new ResponseEntity<>(jobApplicationService.viewJobApplications(job_id,id),HttpStatus.OK);
    }

    @GetMapping("/job/application/candidate/view")
    public ResponseEntity<List<JobApplicationResponse>> viewCandidateApplication(HttpServletRequest request) {
        HttpSession session=request.getSession(false);
        if(session==null)
            throw new UserNotLoggedInException("Login Necessary");
        Long id= (Long) session.getAttribute("user_id");

        return new ResponseEntity<>(jobApplicationService.viewCandidateApplication(id),HttpStatus.OK);
    }

    @PatchMapping("/job/application/recruiter/update")
    public ResponseEntity<JobApplicationResponse> updateStatus(@RequestParam Long application_id, @RequestParam ApplicationStatus status,HttpServletRequest request){
        HttpSession session=request.getSession(false);
        if(session==null)
            throw new UserNotLoggedInException("Login Necessary");
        Long id= (Long) session.getAttribute("user_id");

        return new ResponseEntity<>(jobApplicationService.updateStatus(application_id,status,id),HttpStatus.OK);
    }

    @PatchMapping("/job/application/candidate/withdraw")
    public ResponseEntity<JobApplicationResponse> withdrawApplication(@RequestParam Long application_id,HttpServletRequest request){
        HttpSession session=request.getSession(false);
        if(session==null)
            throw new UserNotLoggedInException("Login Necessary");
        Long id= (Long) session.getAttribute("user_id");

        return new ResponseEntity<>(jobApplicationService.withdrawApplication(application_id,id),HttpStatus.OK);
    }
}
