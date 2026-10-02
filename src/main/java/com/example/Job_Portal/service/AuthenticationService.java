package com.example.Job_Portal.service;

import com.example.Job_Portal.dto.request.LoginRequest;
import com.example.Job_Portal.dto.request.RegistrationRequest;
import com.example.Job_Portal.dto.response.LoginResponse;
import com.example.Job_Portal.dto.response.RegistrationResponse;
import com.example.Job_Portal.entity.UserEntity;
import com.example.Job_Portal.exception.DuplicateEmailException;
import com.example.Job_Portal.exception.UserNotFoundException;
import com.example.Job_Portal.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthenticationService {
    private UserRepository userRepository;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public RegistrationResponse createUser(RegistrationRequest registrationRequest){
        if(!duplicateEmail(registrationRequest.getEmail()))
            throw new DuplicateEmailException("Email Already Exist");
        UserEntity user=new UserEntity();
        user.setFullname(registrationRequest.getFullname());
        user.setEmail(registrationRequest.getEmail());
        user.setPassword(registrationRequest.getPassword());
        user.setRole(registrationRequest.getRole());
        UserEntity userEntity= userRepository.save(user);
        RegistrationResponse response=new RegistrationResponse(
                userEntity.getId(),
                userEntity.getFullname(),
                userEntity.getEmail(),
                userEntity.getRole(),
                userEntity.getCreatedDate()
        );
        return response;
    }

    public LoginResponse loginUser(LoginRequest loginRequest){
        UserEntity user=userRepository.findByEmail(loginRequest.getEmail());
        if(user!=null && loginRequest.getPassword().equals(user.getPassword())){
                return new LoginResponse(user.getId(),user.getFullname(),user.getEmail(),user.getRole());
        }
        else
            throw new UserNotFoundException("Email Or password is Incorrect");
    }

    public List<RegistrationResponse> getAllusers(){
        List<UserEntity> userEntityList=userRepository.findAll();
        List<RegistrationResponse> responses=new ArrayList<>();
        for(UserEntity user:userEntityList){
            responses.add(
                    new RegistrationResponse(
                            user.getId(),
                            user.getFullname(),
                            user.getEmail(),
                            user.getRole(),
                            user.getCreatedDate()
                            )
            );
        }
        return responses;
    }

    public boolean duplicateEmail(String email){
        UserEntity user=userRepository.findByEmail(email);
        if(user!=null)
            return false;
        return true;
    }
}
