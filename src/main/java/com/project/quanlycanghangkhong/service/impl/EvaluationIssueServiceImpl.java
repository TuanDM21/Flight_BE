package com.project.quanlycanghangkhong.service.impl;

import com.project.quanlycanghangkhong.dto.EvaluationIssueDTO;
import com.project.quanlycanghangkhong.model.EvaluationIssue;
import com.project.quanlycanghangkhong.model.EvaluationSession;
import com.project.quanlycanghangkhong.model.Document;
import com.project.quanlycanghangkhong.model.EvaluationIssueDocument;
import com.project.quanlycanghangkhong.repository.EvaluationIssueRepository;
import com.project.quanlycanghangkhong.repository.EvaluationSessionRepository;
import com.project.quanlycanghangkhong.service.EvaluationIssueService;
import com.project.quanlycanghangkhong.repository.DocumentRepository;
import com.project.quanlycanghangkhong.repository.EvaluationIssueDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EvaluationIssueServiceImpl implements EvaluationIssueService {
    @Autowired
    private EvaluationIssueRepository evaluationIssueRepository;
    @Autowired
    private EvaluationSessionRepository evaluationSessionRepository;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private EvaluationIssueDocumentRepository evaluationIssueDocumentRepository;

    private EvaluationIssueDTO convertToDTO(EvaluationIssue entity) {
        EvaluationIssueDTO dto = new EvaluationIssueDTO();
        dto.setId(entity.getId());
        dto.setEvaluationSessionId(entity.getEvaluationSession().getId());
        dto.setTargetType(entity.getTargetType().name());
        dto.setTargetId(entity.getTargetId());
        dto.setIssueContent(entity.getIssueContent());
        dto.setRequestedResolutionDate(entity.getRequestedResolutionDate());
        dto.setIsResolved(entity.getIsResolved());
        dto.setResolutionDate(entity.getResolutionDate());
        dto.setNotes(entity.getNotes());
        dto.setCreatedAt(entity.getCreatedAt());
        if (entity.getDocuments() != null) {
            dto.setDocumentIds(entity.getDocuments().stream().map(doc -> doc.getDocument().getId()).collect(Collectors.toList()));
        }
        return dto;
    }

    private EvaluationIssue convertToEntity(EvaluationIssueDTO dto) {
        EvaluationIssue entity = new EvaluationIssue();
        entity.setId(dto.getId());
        Optional<EvaluationSession> sessionOpt = evaluationSessionRepository.findById(dto.getEvaluationSessionId());
        sessionOpt.ifPresent(entity::setEvaluationSession);
        entity.setTargetType(EvaluationIssue.TargetType.valueOf(dto.getTargetType()));
        entity.setTargetId(dto.getTargetId());
        entity.setIssueContent(dto.getIssueContent());
        entity.setRequestedResolutionDate(dto.getRequestedResolutionDate());
        entity.setIsResolved(dto.getIsResolved());
        entity.setResolutionDate(dto.getResolutionDate());
        entity.setNotes(dto.getNotes());
        return entity;
    }

    @Override
    public List<EvaluationIssueDTO> getAllIssues() {
        return evaluationIssueRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<EvaluationIssueDTO> getIssuesBySession(Integer evaluationSessionId) {
        return evaluationIssueRepository.findByEvaluationSession_Id(evaluationSessionId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public EvaluationIssueDTO getIssueById(Integer id) {
        Optional<EvaluationIssue> issue = evaluationIssueRepository.findById(id);
        return issue.map(this::convertToDTO).orElse(null);
    }

    @Override
    public EvaluationIssueDTO createIssue(EvaluationIssueDTO dto) {
        EvaluationIssue entity = convertToEntity(dto);
        EvaluationIssue saved = evaluationIssueRepository.save(entity);
        // Gán document nếu có
        if (dto.getDocumentIds() != null) {
            for (Integer docId : dto.getDocumentIds()) {
                Optional<Document> docOpt = documentRepository.findById(docId);
                if (docOpt.isPresent()) {
                    EvaluationIssueDocument eid = new EvaluationIssueDocument();
                    eid.setEvaluationIssue(saved);
                    eid.setDocument(docOpt.get());
                    evaluationIssueDocumentRepository.save(eid);
                }
            }
        }
        return convertToDTO(saved);
    }

    @Override
    public EvaluationIssueDTO updateIssue(Integer id, EvaluationIssueDTO dto) {
        Optional<EvaluationIssue> optional = evaluationIssueRepository.findById(id);
        if (optional.isPresent()) {
            EvaluationIssue entity = optional.get();
            entity.setIssueContent(dto.getIssueContent());
            entity.setRequestedResolutionDate(dto.getRequestedResolutionDate());
            entity.setIsResolved(dto.getIsResolved());
            entity.setResolutionDate(dto.getResolutionDate());
            entity.setNotes(dto.getNotes());
            evaluationIssueRepository.save(entity);
            // Xóa documents cũ (KHÔNG set lại list documents)
            evaluationIssueDocumentRepository.deleteAll(entity.getDocuments());
            // Thêm mới documents
            if (dto.getDocumentIds() != null) {
                for (Integer docId : dto.getDocumentIds()) {
                    Optional<Document> docOpt = documentRepository.findById(docId);
                    if (docOpt.isPresent()) {
                        EvaluationIssueDocument eid = new EvaluationIssueDocument();
                        eid.setEvaluationIssue(entity);
                        eid.setDocument(docOpt.get());
                        evaluationIssueDocumentRepository.save(eid);
                    }
                }
            }
            return convertToDTO(entity);
        }
        return null;
    }

    @Override
    public void deleteIssue(Integer id) {
        evaluationIssueRepository.deleteById(id);
    }

    @Override
    public EvaluationIssueDTO updateIssueStatus(Integer id, Boolean isResolved, java.time.LocalDate resolutionDate) {
        Optional<EvaluationIssue> optional = evaluationIssueRepository.findById(id);
        if (optional.isPresent()) {
            EvaluationIssue entity = optional.get();
            entity.setIsResolved(isResolved);
            entity.setResolutionDate(resolutionDate);
            evaluationIssueRepository.save(entity);
            // KHÔNG thêm lại document, chỉ cập nhật trạng thái
            return convertToDTO(entity);
        }
        return null;
    }
}
