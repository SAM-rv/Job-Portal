package com.example.Job_Portal.controller;

import com.example.Job_Portal.dto.request.CandidateProfileRequest;
import com.example.Job_Portal.dto.response.CandidateProfileResponse;
import com.example.Job_Portal.exception.UserNotLoggedInException;
import com.example.Job_Portal.repository.CandidateProfileRepository;
import com.example.Job_Portal.service.CandidateService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/job-portal")
@CrossOrigin(origins = "http://127.0.0.1:5500", allowCredentials = "true")
public class CandidateController {

    private CandidateService candidateService;

    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @PostMapping("/candidate/profile/create")
    public ResponseEntity<CandidateProfileResponse> createProfile(@Valid @RequestBody CandidateProfileRequest candidateProfileRequest, HttpServletRequest request){
        HttpSession session=request.getSession(false);
        if(session==null)
            throw new UserNotLoggedInException("User Is Not logged In");

        Long id= (Long) session.getAttribute("user_id");
        CandidateProfileResponse response= candidateService.createProfile(candidateProfileRequest,id);
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @GetMapping("candidate/profile/view")
    public ResponseEntity<CandidateProfileResponse> viewProfile(HttpServletRequest request){
        HttpSession session=request.getSession(false);

        if(session==null)
            throw new UserNotLoggedInException("User Not Logged In");

        Long id= (Long) session.getAttribute("user_id");
        CandidateProfileResponse response=candidateService.viewprofile(id);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PutMapping("candidate/profile/update")
    public ResponseEntity<CandidateProfileResponse> updateProfile(@Valid @RequestBody CandidateProfileRequest candidateProfileRequest, HttpServletRequest request){
        HttpSession session=request.getSession(false);
        if(session==null)
            throw new UserNotLoggedInException("User Is Not logged In");

        Long id= (Long) session.getAttribute("user_id");
        CandidateProfileResponse response= candidateService.updateProfile(candidateProfileRequest,id);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
