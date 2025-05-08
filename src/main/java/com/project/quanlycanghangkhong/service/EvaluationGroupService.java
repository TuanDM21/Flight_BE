package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.dto.EvaluationGroupDTO;
import java.util.List;

public interface EvaluationGroupService {
    List<EvaluationGroupDTO> getAllEvaluationGroups();
    EvaluationGroupDTO getEvaluationGroupById(Integer id);
    EvaluationGroupDTO createEvaluationGroup(EvaluationGroupDTO evaluationGroupDTO);
    EvaluationGroupDTO updateEvaluationGroup(Integer id, EvaluationGroupDTO evaluationGroupDTO);
    void deleteEvaluationGroup(Integer id);
}
