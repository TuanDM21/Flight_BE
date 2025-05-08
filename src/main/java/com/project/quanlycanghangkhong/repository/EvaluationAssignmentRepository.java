package com.project.quanlycanghangkhong.repository;

import com.project.quanlycanghangkhong.model.EvaluationAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluationAssignmentRepository extends JpaRepository<EvaluationAssignment, Integer> {
}
