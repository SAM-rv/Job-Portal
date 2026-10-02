package com.example.Job_Portal.service;

import com.example.Job_Portal.dto.request.JobRequest;
import com.example.Job_Portal.dto.response.JobResponse;
import com.example.Job_Portal.entity.JobEntity;
import com.example.Job_Portal.entity.RecruiterProfileEntity;
import com.example.Job_Portal.entity.UserEntity;
import com.example.Job_Portal.enums.EmploymentTypes;
import com.example.Job_Portal.enums.JobStatus;
import com.example.Job_Portal.enums.Role;
import com.example.Job_Portal.exception.ExpiredDeadlineException;
import com.example.Job_Portal.exception.DataNotFoundException;
import com.example.Job_Portal.exception.UnAuthorizedOperationException;
import com.example.Job_Portal.exception.UserNotFoundException;
import com.example.Job_Portal.repository.JobRepository;
import com.example.Job_Portal.repository.RecruiterProfileRepository;
import com.example.Job_Portal.repository.UserRepository;
import com.example.Job_Portal.specification.JobSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;

    public JobService(JobRepository jobRepository, UserRepository userRepository, RecruiterProfileRepository recruiterProfileRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
    }

    public JobResponse createJob(JobRequest jobRequest, Long user_id){
        UserEntity user=userRepository.findById(user_id).orElseThrow(()-> new UserNotFoundException("User Not Found"));
        if(user.getRole()!= Role.Recruiter)
            throw new UnAuthorizedOperationException("You Are Not Recruiter");

        RecruiterProfileEntity recruiterProfile=recruiterProfileRepository.findByUser(user);
        if(recruiterProfile==null)
            throw new UnAuthorizedOperationException("Complete Profile");

        if(LocalDate.now().isAfter(jobRequest.getDeadline()))
            throw new ExpiredDeadlineException("DeadLine Is Past Posted Date");

        JobEntity job1=jobRepository.save(entityMapping(jobRequest,new JobEntity(),recruiterProfile,JobStatus.Open));
        return responseMapping(job1);
    }

    public JobResponse updateJob(JobRequest jobRequest,Long user_id,Long job_id,JobStatus status){
        UserEntity user=userRepository.findById(user_id).orElseThrow(()-> new UserNotFoundException("User Not Found"));
        if(user.getRole()!= Role.Recruiter)
            throw new UnAuthorizedOperationException("You Are Not Recruiter");

        RecruiterProfileEntity recruiterProfile=recruiterProfileRepository.findByUser(user);
        if(recruiterProfile==null)
            throw new UnAuthorizedOperationException("Complete Profile");

        JobEntity job=jobRepository.findById(job_id).orElseThrow(()->new DataNotFoundException("Job Is Not Present"));

        if(job.getRecruiterProfile().getId()!=recruiterProfile.getId())
            throw new UnAuthorizedOperationException("This Job Is Not Created By you");

        if(LocalDate.now().isAfter(jobRequest.getDeadline()))
            throw new ExpiredDeadlineException("DeadLine Is Past Today");

        JobEntity job1=jobRepository.save(entityMapping(jobRequest,job,recruiterProfile,status));
        return responseMapping(job1);
    }

    public List<JobResponse> viewAllJobs(){
        return jobListMapping(jobRepository.findAll());
    }

    public List<JobResponse> viewJobs(Long user_id){
        UserEntity user=userRepository.findById(user_id).orElseThrow(()-> new UserNotFoundException("User Not Found"));
        if(user.getRole()!=Role.Recruiter)
            throw new UnAuthorizedOperationException("You Are Not Recruiter");
        RecruiterProfileEntity recruiterProfile=recruiterProfileRepository.findByUser(user);
        return jobListMapping(jobRepository.findByRecruiterProfile(recruiterProfile));
    }

    public List<JobResponse> searchFilterJobs(String title, String location, String skill, EmploymentTypes emp_type,Integer min_exp,Integer salary){
        Specification<JobEntity> specification = Specification.unrestricted();
        if(title!=null)
            specification=specification.and((JobSpecification.hasTitle(title)));
        if(location!=null)
            specification=specification.and(JobSpecification.hasLocation(location));
        if(skill!=null)
            specification=specification.and(JobSpecification.hasSkill(skill));
        if(emp_type!=null)
            specification=specification.and(JobSpecification.hasEmpType(emp_type));
        if(min_exp!=null)
            specification=specification.and(JobSpecification.hasMinExp(min_exp));
        if(salary!=null)
            specification=specification.and(JobSpecification.hasSalary(salary));

        return jobListMapping(jobRepository.findAll(specification));
    }

    private JobEntity entityMapping(JobRequest jobRequest,JobEntity job,RecruiterProfileEntity recruiterProfile,JobStatus status){
        job.setTitle(jobRequest.getTitle());
        job.setDescription(jobRequest.getDescription());
        job.setCompany(jobRequest.getCompany());
        job.setLocation(jobRequest.getLocation());
        job.setMinSalary(jobRequest.getMinSalary());
        job.setMaxSalary(jobRequest.getMaxSalary());
        job.setSkills(jobRequest.getSkills());
        job.setMinExperience(jobRequest.getMinExperience());
        job.setEmpType(jobRequest.getEmpType());
        job.setApplicationDeadline(jobRequest.getDeadline());
        job.setStatus(status);
        job.setRecruiterProfile(recruiterProfile);

        return job;
    }

    private JobResponse responseMapping(JobEntity job1){
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

    private List<JobResponse> jobListMapping(List<JobEntity> jobs){
        List<JobResponse> response=new ArrayList<>();
        for(JobEntity job:jobs ) {
            response.add(new JobResponse(
                    job.getId(),
                    job.getTitle(),
                    job.getDescription(),
                    job.getCompany(),
                    job.getLocation(),
                    job.getMinSalary(),
                    job.getMaxSalary(),
                    job.getSkills(),
                    job.getMinExperience(),
                    job.getEmpType(),
                    job.getPostedDate(),
                    job.getApplicationDeadline(),
                    job.getStatus(),
                    job.getRecruiterProfile().getId(),
                    job.getRecruiterProfile().getUser().getFullname(),
                    job.getRecruiterProfile().getUser().getEmail()
            ));
        }
        return response;
    }

}