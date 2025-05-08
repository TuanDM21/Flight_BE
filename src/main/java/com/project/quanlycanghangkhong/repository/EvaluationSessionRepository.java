package com.project.quanlycanghangkhong.repository;

import com.project.quanlycanghangkhong.model.EvaluationSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluationSessionRepository extends JpaRepository<EvaluationSession, Integer> {
}