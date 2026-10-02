package com.example.Job_Portal.controller;

import com.example.Job_Portal.dto.request.LoginRequest;
import com.example.Job_Portal.dto.request.RegistrationRequest;
import com.example.Job_Portal.dto.response.LoginResponse;
import com.example.Job_Portal.dto.response.RegistrationResponse;
import com.example.Job_Portal.entity.UserEntity;
import com.example.Job_Portal.service.AuthenticationService;
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
public class AuthenticationController {

    private AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request){
        LoginResponse response=authenticationService.loginUser(loginRequest);
        if(response!=null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("user_id",response.id());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/auth/register")
    public ResponseEntity<RegistrationResponse> createUser(@Valid @RequestBody RegistrationRequest registrationRequest){
        return new ResponseEntity<>(authenticationService.createUser(registrationRequest),HttpStatus.CREATED);
    }

    @GetMapping("/auth/users")
    public ResponseEntity<List<RegistrationResponse>> getAllUsers(){
        return new ResponseEntity<>(authenticationService.getAllusers(),HttpStatus.OK);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<String> logoutUser(HttpServletRequest request){
        HttpSession session=request.getSession(false);
        if(session!=null) {
            session.invalidate();
            return new ResponseEntity<>("LogOut SuccessFul",HttpStatus.OK);
        }
        else
            return new ResponseEntity<>("You Are Not LoggedIN",HttpStatus.BAD_REQUEST);
    }



}
