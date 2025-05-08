package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.dto.EvaluationSessionDTO;
import java.util.List;

public interface EvaluationSessionService {
    List<EvaluationSessionDTO> getAllEvaluationSessions();
    EvaluationSessionDTO getEvaluationSessionById(Integer id);
    EvaluationSessionDTO createEvaluationSession(EvaluationSessionDTO dto);
    EvaluationSessionDTO updateEvaluationSession(Integer id, EvaluationSessionDTO dto);
    void deleteEvaluationSession(Integer id);
}
