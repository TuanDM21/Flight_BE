package com.project.quanlycanghangkhong.repository;

import com.project.quanlycanghangkhong.model.EvaluationIssueDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluationIssueDocumentRepository extends JpaRepository<EvaluationIssueDocument, Integer> {
    void deleteAllByEvaluationIssue_Id(Integer evaluationIssueId);
    void deleteAllByDocument_Id(Integer documentId);
    java.util.List<EvaluationIssueDocument> findByEvaluationIssue_Id(Integer evaluationIssueId);
}
