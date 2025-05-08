package com.project.quanlycanghangkhong.repository;

import com.project.quanlycanghangkhong.model.EvaluationIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EvaluationIssueRepository extends JpaRepository<EvaluationIssue, Integer> {
    List<EvaluationIssue> findByEvaluationSession_Id(Integer evaluationSessionId);
}