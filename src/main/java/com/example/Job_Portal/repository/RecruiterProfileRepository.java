package com.example.Job_Portal.repository;

import com.example.Job_Portal.entity.RecruiterProfileEntity;
import com.example.Job_Portal.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecruiterProfileRepository extends JpaRepository<RecruiterProfileEntity,Long> {
    RecruiterProfileEntity findByUser(UserEntity user);
}
