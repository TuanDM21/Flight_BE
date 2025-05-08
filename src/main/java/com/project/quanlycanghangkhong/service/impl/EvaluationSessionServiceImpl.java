package com.project.quanlycanghangkhong.service.impl;

import com.project.quanlycanghangkhong.dto.EvaluationSessionDTO;
import com.project.quanlycanghangkhong.dto.EvaluationAssignmentDTO;
import com.project.quanlycanghangkhong.model.EvaluationSession;
import com.project.quanlycanghangkhong.model.EvaluationGroup;
import com.project.quanlycanghangkhong.model.EvaluationAssignment;
import com.project.quanlycanghangkhong.repository.EvaluationSessionRepository;
import com.project.quanlycanghangkhong.service.EvaluationSessionService;
import com.project.quanlycanghangkhong.repository.EvaluationGroupRepository;
import com.project.quanlycanghangkhong.repository.EvaluationAssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EvaluationSessionServiceImpl implements EvaluationSessionService {
    @Autowired
    private EvaluationSessionRepository evaluationSessionRepository;
    @Autowired
    private EvaluationGroupRepository evaluationGroupRepository;
    @Autowired
    private EvaluationAssignmentRepository evaluationAssignmentRepository;

    private EvaluationSessionDTO convertToDTO(EvaluationSession entity) {
        EvaluationSessionDTO dto = new EvaluationSessionDTO();
        dto.setId(entity.getId());
        dto.setEvaluationGroupId(entity.getEvaluationGroup().getId());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setNotes(entity.getNotes());
        dto.setCreatedAt(entity.getCreatedAt());
        if (entity.getAssignments() != null) {
            dto.setAssignments(entity.getAssignments().stream().map(this::convertAssignmentToDTO).collect(Collectors.toList()));
        }
        return dto;
    }

    private EvaluationAssignmentDTO convertAssignmentToDTO(EvaluationAssignment entity) {
        EvaluationAssignmentDTO dto = new EvaluationAssignmentDTO();
        dto.setId(entity.getId());
        dto.setEvaluationPeriodId(entity.getEvaluationSession().getId());
        dto.setTargetType(entity.getTargetType().name());
        dto.setTargetId(entity.getTargetId());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    private EvaluationSession convertToEntity(EvaluationSessionDTO dto) {
        EvaluationSession entity = new EvaluationSession();
        entity.setId(dto.getId());
        Optional<EvaluationGroup> groupOpt = evaluationGroupRepository.findById(dto.getEvaluationGroupId());
        groupOpt.ifPresent(entity::setEvaluationGroup);
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setNotes(dto.getNotes());
        return entity;
    }

    @Override
    public List<EvaluationSessionDTO> getAllEvaluationSessions() {
        return evaluationSessionRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public EvaluationSessionDTO getEvaluationSessionById(Integer id) {
        Optional<EvaluationSession> session = evaluationSessionRepository.findById(id);
        return session.map(this::convertToDTO).orElse(null);
    }

    @Override
    public EvaluationSessionDTO createEvaluationSession(EvaluationSessionDTO dto) {
        EvaluationSession entity = convertToEntity(dto);
        EvaluationSession saved = evaluationSessionRepository.save(entity);
        // Gán assignment nếu có
        if (dto.getAssignments() != null) {
            for (EvaluationAssignmentDTO a : dto.getAssignments()) {
                EvaluationAssignment assignment = new EvaluationAssignment();
                assignment.setEvaluationSession(saved);
                assignment.setTargetType(EvaluationAssignment.TargetType.valueOf(a.getTargetType()));
                assignment.setTargetId(a.getTargetId());
                evaluationAssignmentRepository.save(assignment);
            }
        }
        return convertToDTO(saved);
    }

    @Override
    public EvaluationSessionDTO updateEvaluationSession(Integer id, EvaluationSessionDTO dto) {
        Optional<EvaluationSession> optional = evaluationSessionRepository.findById(id);
        if (optional.isPresent()) {
            EvaluationSession entity = optional.get();
            entity.setStartDate(dto.getStartDate());
            entity.setEndDate(dto.getEndDate());
            entity.setNotes(dto.getNotes());
            evaluationSessionRepository.save(entity);
            // Cập nhật assignment: xóa cũ, thêm mới
            evaluationAssignmentRepository.deleteAll(entity.getAssignments());
            if (dto.getAssignments() != null) {
                for (EvaluationAssignmentDTO a : dto.getAssignments()) {
                    EvaluationAssignment assignment = new EvaluationAssignment();
                    assignment.setEvaluationSession(entity);
                    assignment.setTargetType(EvaluationAssignment.TargetType.valueOf(a.getTargetType()));
                    assignment.setTargetId(a.getTargetId());
                    evaluationAssignmentRepository.save(assignment);
                }
            }
            return convertToDTO(entity);
        }
        return null;
    }

    @Override
    public void deleteEvaluationSession(Integer id) {
        evaluationSessionRepository.deleteById(id);
    }
}
