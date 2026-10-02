package com.example.Job_Portal.service;

import com.example.Job_Portal.dto.request.CandidateProfileRequest;
import com.example.Job_Portal.dto.response.CandidateProfileResponse;
import com.example.Job_Portal.entity.CandidateProfileEntity;
import com.example.Job_Portal.entity.UserEntity;
import com.example.Job_Portal.enums.Role;
import com.example.Job_Portal.exception.UnAuthorizedOperationException;
import com.example.Job_Portal.exception.UserNotFoundException;
import com.example.Job_Portal.repository.CandidateProfileRepository;
import com.example.Job_Portal.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class CandidateService {
    private final CandidateProfileRepository candidateProfileRepository;
    private final UserRepository userRepository;

    public CandidateService(CandidateProfileRepository candidateProfileRepository, UserRepository userRepository) {
        this.candidateProfileRepository = candidateProfileRepository;
        this.userRepository = userRepository;
    }

    public CandidateProfileResponse createProfile(CandidateProfileRequest candidateProfileRequest, Long user_id){
        UserEntity user=userRepository.findById(user_id).orElseThrow(()->new UserNotFoundException("User Not Found"));

        if(user.getRole()!=Role.Candidate)
            throw new UnAuthorizedOperationException("You Are Not Candidate");

        CandidateProfileEntity profile=candidateProfileRepository.findByUser(user);
        if(profile!=null)
            throw new UnAuthorizedOperationException("Profile Already Present");

        CandidateProfileEntity candidateProfile1=entityMapping(candidateProfileRequest,user,new CandidateProfileEntity());
        return responseMapping(candidateProfileRepository.save(candidateProfile1));
    }

    public CandidateProfileResponse updateProfile(CandidateProfileRequest candidateProfileRequest,Long user_id){
        UserEntity user=userRepository.findById(user_id).orElseThrow(()->new UserNotFoundException("User Not Found"));

        if(user.getRole()!=Role.Candidate)
            throw new UnAuthorizedOperationException("You Are Not Candidate");

        CandidateProfileEntity profile=candidateProfileRepository.findByUser(user);
        if(profile==null)
            throw new UnAuthorizedOperationException("Profile Not Present");
        entityMapping(candidateProfileRequest,user,profile);
        CandidateProfileEntity candidateProfile1=candidateProfileRepository.save(profile);
        return responseMapping(candidateProfile1);
    }

    public CandidateProfileResponse viewprofile(Long user_id){
        UserEntity user=userRepository.findById(user_id).orElseThrow(()->new UserNotFoundException("User Not Found"));

        CandidateProfileEntity candidateProfile1=candidateProfileRepository.findByUser(user);
        if(candidateProfile1==null)
            throw new UserNotFoundException("Profile Not Present");

        return responseMapping(candidateProfile1);
    }

    private CandidateProfileEntity entityMapping(CandidateProfileRequest candidateProfileRequest,UserEntity user,CandidateProfileEntity candidateProfile){
        candidateProfile.setPhoneNumber(candidateProfileRequest.getPhoneNumber());
        candidateProfile.setLocation(candidateProfileRequest.getLocation());
        candidateProfile.setEducation(candidateProfileRequest.getEducation());
        candidateProfile.setSkills(candidateProfileRequest.getSkills());
        candidateProfile.setYearsOfExperience(candidateProfileRequest.getYearOfExperience());
        candidateProfile.setResumeUrl(candidateProfileRequest.getResumeUrl());
        candidateProfile.setUser(user);
        return candidateProfile;
    }

    private CandidateProfileResponse responseMapping(CandidateProfileEntity candidateProfile1){
        CandidateProfileResponse response=new CandidateProfileResponse(
                candidateProfile1.getUser().getId(),
                candidateProfile1.getUser().getFullname(),
                candidateProfile1.getUser().getEmail(),
                candidateProfile1.getUser().getRole(),
                candidateProfile1.getPhoneNumber(),
                candidateProfile1.getLocation(),
                candidateProfile1.getEducation(),
                candidateProfile1.getSkills(),
                candidateProfile1.getYearsOfExperience(),
                candidateProfile1.getResumeUrl()
        );
        return response;
    }
}
