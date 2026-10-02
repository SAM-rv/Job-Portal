package com.example.Job_Portal.controller;

import com.example.Job_Portal.dto.request.CandidateProfileRequest;
import com.example.Job_Portal.dto.request.RecruiterProfileRequest;
import com.example.Job_Portal.dto.response.CandidateProfileResponse;
import com.example.Job_Portal.dto.response.RecruiterProfileResponse;
import com.example.Job_Portal.exception.UserNotLoggedInException;
import com.example.Job_Portal.service.CandidateService;
import com.example.Job_Portal.service.RecruiterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/job-portal")
@CrossOrigin(origins = "http://127.0.0.1:5500", allowCredentials = "true")
public class RecruiterController {
    private RecruiterService recruiterService;

    public RecruiterController(RecruiterService recruiterService) {
        this.recruiterService = recruiterService;
    }

    @PostMapping("/recruiter/profile/create")
    public ResponseEntity<RecruiterProfileResponse> createProfile(@Valid @RequestBody RecruiterProfileRequest recruiterProfileRequest, HttpServletRequest request){
        HttpSession session=request.getSession(false);
        if(session==null)
            throw new UserNotLoggedInException("User Is Not logged In");

        Long id= (Long) session.getAttribute("user_id");
        RecruiterProfileResponse response= recruiterService.createProfile(recruiterProfileRequest,id);
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @GetMapping("recruiter/profile/view")
    public ResponseEntity<RecruiterProfileResponse> viewProfile(HttpServletRequest request){
        HttpSession session=request.getSession(false);

        if(session==null)
            throw new UserNotLoggedInException("User Not Logged In");

        Long id= (Long) session.getAttribute("user_id");
        RecruiterProfileResponse response=recruiterService.viewprofile(id);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PutMapping("recruiter/profile/update")
    public ResponseEntity<RecruiterProfileResponse> updateProfile(@Valid @RequestBody RecruiterProfileRequest recruiterProfileRequest, HttpServletRequest request){
        HttpSession session=request.getSession(false);
        if(session==null)
            throw new UserNotLoggedInException("User Is Not logged In");

        Long id= (Long) session.getAttribute("user_id");
        RecruiterProfileResponse response= recruiterService.updateProfile(recruiterProfileRequest,id);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
