package com.example.Job_Portal.specification;

import com.example.Job_Portal.entity.JobEntity;
import com.example.Job_Portal.enums.EmploymentTypes;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;

public class JobSpecification {

    public static Specification<JobEntity> hasTitle(String title){
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.like(criteriaBuilder.lower(root.get("title")),"%"+title.toLowerCase()+"%");
    }

    public static Specification<JobEntity> hasLocation(String location){
        return (root, query, criteriaBuilder) -> (
                criteriaBuilder.like(criteriaBuilder.lower(root.get("location")),"%"+location.toLowerCase()+"%")
                );
    }

    public static Specification<JobEntity> hasSkill(String skill){
        return (root, query, criteriaBuilder) -> {
            Join<JobEntity, String> skillJoin = root.join("skills");

            return criteriaBuilder.like(
                    criteriaBuilder.lower(skillJoin), "%" + skill.toLowerCase() + "%"
            );
        };
    }

    public static Specification<JobEntity> hasEmpType(EmploymentTypes emp_type ){
        return (root, query, criteriaBuilder) -> (
                criteriaBuilder.equal(root.get("empType"),
                        emp_type)
                );
    }

    public static Specification<JobEntity> hasMinExp(Integer min_exp){
        return (root, query, criteriaBuilder) -> (
                criteriaBuilder.lessThanOrEqualTo(root.get("minExperience"),min_exp)
                );
    }

    public static Specification<JobEntity> hasSalary(Integer salary) {
        return (root, query, criteriaBuilder) -> (
                criteriaBuilder.between(criteriaBuilder.literal(salary), root.get("minSalary"), root.get("maxSalary"))
        );
    }

}




