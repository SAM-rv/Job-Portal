package com.example.Job_Portal.service;

import com.example.Job_Portal.dto.request.RecruiterProfileRequest;
import com.example.Job_Portal.dto.response.RecruiterProfileResponse;
import com.example.Job_Portal.entity.RecruiterProfileEntity;
import com.example.Job_Portal.entity.UserEntity;
import com.example.Job_Portal.enums.Role;
import com.example.Job_Portal.exception.UnAuthorizedOperationException;
import com.example.Job_Portal.exception.UserNotFoundException;
import com.example.Job_Portal.repository.RecruiterProfileRepository;
import com.example.Job_Portal.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class RecruiterService {

    private final RecruiterProfileRepository recruiterProfileRepository;
    private final UserRepository userRepository;

    public RecruiterService(RecruiterProfileRepository recruiterProfileRepository, UserRepository userRepository) {
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.userRepository = userRepository;
    }

    public RecruiterProfileResponse createProfile(RecruiterProfileRequest recruiterProfileRequest, Long user_id){
        UserEntity user=userRepository.findById(user_id).orElseThrow(()->new UserNotFoundException("User Not Found"));

        if(user.getRole()!= Role.Recruiter)
            throw new UnAuthorizedOperationException("You Are Not Recruiter");

        RecruiterProfileEntity profile=recruiterProfileRepository.findByUser(user);
        if(profile!=null)
            throw new UnAuthorizedOperationException("Profile Already Present");

        RecruiterProfileEntity recruiterProfile1=entityMapping(recruiterProfileRequest,user,new RecruiterProfileEntity());
        return responseMapping(recruiterProfileRepository.save(recruiterProfile1));
    }

    public RecruiterProfileResponse updateProfile(RecruiterProfileRequest recruiterProfileRequest,Long user_id){
        UserEntity user=userRepository.findById(user_id).orElseThrow(()->new UserNotFoundException("User Not Found"));

        if(user.getRole()!= Role.Recruiter)
            throw new UnAuthorizedOperationException("You Are Not Recruiter");

        RecruiterProfileEntity profile=recruiterProfileRepository.findByUser(user);
        if(profile==null)
            throw new UnAuthorizedOperationException("Profile Not Present");

        entityMapping(recruiterProfileRequest,user,profile);
        RecruiterProfileEntity recruiterProfile1=recruiterProfileRepository.save(profile);
        return responseMapping(recruiterProfile1);
    }

    public RecruiterProfileResponse viewprofile(Long user_id){
        UserEntity user=userRepository.findById(user_id).orElseThrow(()->new UserNotFoundException("User Not Found"));

        RecruiterProfileEntity recruiterProfile1=recruiterProfileRepository.findByUser(user);
        if(recruiterProfile1==null)
            throw new UserNotFoundException("Profile Not Present");

        return responseMapping(recruiterProfile1);
    }

    private RecruiterProfileEntity entityMapping(RecruiterProfileRequest recruiterProfileRequest,UserEntity user,RecruiterProfileEntity recruiterProfile){
        recruiterProfile.setCompanyName(recruiterProfileRequest.getCompanyName());
        recruiterProfile.setCompanyDescription(recruiterProfileRequest.getCompanyDescription());
        recruiterProfile.setCompanyLocation(recruiterProfileRequest.getCompanyLocation());
        recruiterProfile.setUser(user);

        return recruiterProfile;
    }

    private RecruiterProfileResponse responseMapping(RecruiterProfileEntity recruiterProfile1){
        RecruiterProfileResponse response=new RecruiterProfileResponse(
                recruiterProfile1.getUser().getId(),
                recruiterProfile1.getUser().getFullname(),
                recruiterProfile1.getUser().getEmail(),
                recruiterProfile1.getUser().getRole(),
                recruiterProfile1.getCompanyName(),
                recruiterProfile1.getCompanyDescription(),
                recruiterProfile1.getCompanyLocation()
        );
        return response;
    }
}
