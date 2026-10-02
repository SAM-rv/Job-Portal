package com.example.Job_Portal.repository;

import com.example.Job_Portal.entity.JobEntity;
import com.example.Job_Portal.entity.RecruiterProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<JobEntity,Long>, JpaSpecificationExecutor<JobEntity> {
    List<JobEntity> findByRecruiterProfile(RecruiterProfileEntity recruiterProfile);
}
