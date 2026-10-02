    package com.example.Job_Portal.repository;

    import com.example.Job_Portal.entity.CandidateProfileEntity;
    import com.example.Job_Portal.entity.UserEntity;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.stereotype.Repository;

    @Repository
    public interface CandidateProfileRepository extends JpaRepository<CandidateProfileEntity,Long> {

        CandidateProfileEntity findByUser(UserEntity user);
    }
