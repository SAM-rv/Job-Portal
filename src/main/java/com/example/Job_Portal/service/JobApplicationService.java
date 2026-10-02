package com.example.Job_Portal.service;

import com.example.Job_Portal.dto.response.CandidateProfileResponse;
import com.example.Job_Portal.dto.response.JobApplicationResponse;
import com.example.Job_Portal.dto.response.JobResponse;
import com.example.Job_Portal.entity.*;
import com.example.Job_Portal.enums.ApplicationStatus;
import com.example.Job_Portal.enums.JobStatus;
import com.example.Job_Portal.enums.Role;
import com.example.Job_Portal.exception.*;
import com.example.Job_Portal.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class JobApplicationService {
    private final JobApplicationRepository jobApplicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;

    public JobApplicationService(JobApplicationRepository jobApplicationRepository, JobRepository jobRepository, UserRepository userRepository, CandidateProfileRepository candidateProfileRepository, RecruiterProfileRepository recruiterProfileRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.candidateProfileRepository = candidateProfileRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
    }

    public JobApplicationResponse createApplication(Long job_id,Long user_id) {
        UserEntity user = userRepository.findById(user_id).orElseThrow(() -> new UserNotFoundException("User Not Found"));
        if (user.getRole() != Role.Candidate)
            throw new UnAuthorizedOperationException("You Are Not candidate");
        CandidateProfileEntity candidateProfile = candidateProfileRepository.findByUser(user);
        if (candidateProfile == null)
            throw new UnAuthorizedOperationException("profile Not Present , First Create Profile");

        JobEntity job = jobRepository.findById(job_id).orElseThrow(() -> new DataNotFoundException("Not Not Present"));

        List<JobApplicationEntity> applicationList=jobApplicationRepository.findByJobAndStatusNot(job,ApplicationStatus.Withdrawn);
        for(JobApplicationEntity application:applicationList){
            if(application.getCandidateProfile().getId()==candidateProfile.getId())
                throw new DuplicateApplicationException("Cant Apply To same Job Twice");
        }
        if(!job.getApplicationDeadline() .isAfter(LocalDate.now()))
            throw new ExpiredDeadlineException("The Job Application Deadline is past Today");

        if(job.getStatus()!= JobStatus.Open)
            throw new ClosedJobException("This Job is Closed");

        JobApplicationEntity jobApplication = new JobApplicationEntity();
        jobApplication.setCandidateProfile(candidateProfile);
        jobApplication.setJob(job);
        jobApplication.setStatus(ApplicationStatus.Applied);
        return responseMapping(jobApplicationRepository.save(jobApplication));
    }

    public List<JobApplicationResponse> viewCandidateApplication(Long user_id){
        UserEntity user=userRepository.findById(user_id).orElseThrow(()->new UserNotFoundException("user not Found"));
        if(user.getRole()!=Role.Candidate)
            throw new UnAuthorizedOperationException("You AreNot Candidate");

        CandidateProfileEntity candidateProfile=candidateProfileRepository.findByUser(user);
        if(candidateProfile==null)
            throw new UnAuthorizedOperationException("Profile Not Complete");

        List<JobApplicationEntity> applicationEntityList=jobApplicationRepository.findByCandidateProfile(candidateProfile);

        return jobApplicationListMapping(applicationEntityList);
    }

    public List<JobApplicationResponse> viewJobApplications(Long job_id,Long user_id){
        UserEntity user=userRepository.findById(user_id).orElseThrow(()->new UserNotFoundException("user not Found"));
        if(user.getRole()!=Role.Recruiter)
            throw new UnAuthorizedOperationException("You Are Not Recruiter");

        RecruiterProfileEntity recruiterProfile =recruiterProfileRepository.findByUser(user);
        if(recruiterProfile==null)
            throw new UnAuthorizedOperationException("Profile Not Complete");
        JobEntity job=jobRepository.findById(job_id).orElseThrow(()-> new DataNotFoundException("Job Not Found"));

        if(job.getRecruiterProfile().getId()!=recruiterProfile.getId())
            throw new UnAuthorizedOperationException("This is Not Your Job");

        List<JobApplicationEntity> jobApplicationList=jobApplicationRepository.findByJobAndStatusNot(job,ApplicationStatus.Withdrawn);
        return jobApplicationListMapping(jobApplicationList);
    }



    public JobApplicationResponse withdrawApplication(Long application_id,Long user_id){
        UserEntity user = userRepository.findById(user_id).orElseThrow(() -> new UserNotFoundException("User Not Found"));
        if (user.getRole() != Role.Candidate)
            throw new UnAuthorizedOperationException("You Are Not candidate");
        CandidateProfileEntity candidateProfile = candidateProfileRepository.findByUser(user);
        if (candidateProfile == null)
            throw new UnAuthorizedOperationException("profile Not Present , First Create Profile");

        JobApplicationEntity jobApplication=jobApplicationRepository.findById(application_id).orElseThrow(()->new DataNotFoundException("Application Not Present"));
        if(jobApplication.getCandidateProfile().getId()!=candidateProfile.getId())
            throw new UnAuthorizedOperationException("This Is Not Your Application");
        jobApplication.setStatus(ApplicationStatus.Withdrawn);
        return responseMapping(jobApplicationRepository.save(jobApplication));
    }

    public JobApplicationResponse updateStatus(Long application_id,ApplicationStatus status,Long user_id){
        UserEntity user = userRepository.findById(user_id).orElseThrow(() -> new UserNotFoundException("User Not Found"));
        if (user.getRole() != Role.Recruiter)
            throw new UnAuthorizedOperationException("You Are Not recruiter");
        RecruiterProfileEntity recruiterProfile = recruiterProfileRepository.findByUser(user);
        if (recruiterProfile== null)
            throw new UnAuthorizedOperationException("profile Not Present , First Create Profile");

        JobApplicationEntity jobApplication=jobApplicationRepository.findById(application_id).orElseThrow(()->new DataNotFoundException("Application Not Present"));
        if(recruiterProfile.getId()!=jobApplication.getJob().getRecruiterProfile().getId())
            throw new UnAuthorizedOperationException("This Application Is Not For Your Job");
        if(status==ApplicationStatus.Withdrawn)
            throw new UnAuthorizedOperationException("You cant Withdraw This Application");
        jobApplication.setStatus(status);
        return responseMapping(jobApplicationRepository.save(jobApplication));
    }


    private JobApplicationResponse responseMapping(JobApplicationEntity jobApplication1){
        JobApplicationResponse response = new JobApplicationResponse(
                jobApplication1.getId(),
                candidateResponseMapping(jobApplication1.getCandidateProfile()),
                jobResponseMapping(jobApplication1.getJob()),
                jobApplication1.getAppliedDate(),
                jobApplication1.getStatus()
        );
        return response;
    }

    private JobResponse jobResponseMapping(JobEntity job1){
        JobResponse response=new JobResponse(
                job1.getId(),
                job1.getTitle(),
                job1.getDescription(),
                job1.getCompany(),
                job1.getLocation(),
                job1.getMinSalary(),
                job1.getMaxSalary(),
                job1.getSkills(),
                job1.getMinExperience(),
                job1.getEmpType(),
                job1.getPostedDate(),
                job1.getApplicationDeadline(),
                job1.getStatus(),
                job1.getRecruiterProfile().getId(),
                job1.getRecruiterProfile().getUser().getFullname(),
                job1.getRecruiterProfile().getUser().getEmail()
        );
        return response;
    }

    private CandidateProfileResponse candidateResponseMapping(CandidateProfileEntity candidateProfile1){
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

    private List<JobApplicationResponse> jobApplicationListMapping(List<JobApplicationEntity> applicationEntityList){
        List<JobApplicationResponse> jobApplicationResponseList= new ArrayList<>();
        for(JobApplicationEntity jobApplication:applicationEntityList){
            jobApplicationResponseList.add(responseMapping(jobApplication));
        }
        return jobApplicationResponseList;
    }
}
