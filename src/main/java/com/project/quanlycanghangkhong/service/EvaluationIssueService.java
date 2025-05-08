package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.dto.EvaluationIssueDTO;
import java.util.List;

public interface EvaluationIssueService {
    List<EvaluationIssueDTO> getAllIssues();
    List<EvaluationIssueDTO> getIssuesBySession(Integer evaluationSessionId);
    EvaluationIssueDTO getIssueById(Integer id);
    EvaluationIssueDTO createIssue(EvaluationIssueDTO dto);
    EvaluationIssueDTO updateIssue(Integer id, EvaluationIssueDTO dto);
    void deleteIssue(Integer id);
    EvaluationIssueDTO updateIssueStatus(Integer id, Boolean isResolved, java.time.LocalDate resolutionDate);
}
