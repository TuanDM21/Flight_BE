package com.project.quanlycanghangkhong.service.impl;

import com.project.quanlycanghangkhong.dto.EvaluationGroupDTO;
import com.project.quanlycanghangkhong.model.EvaluationGroup;
import com.project.quanlycanghangkhong.repository.EvaluationGroupRepository;
import com.project.quanlycanghangkhong.service.EvaluationGroupService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EvaluationGroupServiceImpl implements EvaluationGroupService {
    @Autowired
    private EvaluationGroupRepository evaluationGroupRepository;

    private EvaluationGroupDTO convertToDTO(EvaluationGroup entity) {
        EvaluationGroupDTO dto = new EvaluationGroupDTO();
        dto.setId(entity.getId());
        dto.setGroupName(entity.getGroupName());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    private EvaluationGroup convertToEntity(EvaluationGroupDTO dto) {
        EvaluationGroup entity = new EvaluationGroup();
        entity.setId(dto.getId());
        entity.setGroupName(dto.getGroupName());
        entity.setDescription(dto.getDescription());
        entity.setCreatedAt(dto.getCreatedAt());
        return entity;
    }

    @Override
    public List<EvaluationGroupDTO> getAllEvaluationGroups() {
        return evaluationGroupRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EvaluationGroupDTO getEvaluationGroupById(Integer id) {
        Optional<EvaluationGroup> group = evaluationGroupRepository.findById(id);
        return group.map(this::convertToDTO).orElse(null);
    }

    @Override
    public EvaluationGroupDTO createEvaluationGroup(EvaluationGroupDTO evaluationGroupDTO) {
        EvaluationGroup entity = convertToEntity(evaluationGroupDTO);
        entity.setId(null); // Ensure id is null for new entity
        EvaluationGroup saved = evaluationGroupRepository.save(entity);
        return convertToDTO(saved);
    }

    @Override
    public EvaluationGroupDTO updateEvaluationGroup(Integer id, EvaluationGroupDTO evaluationGroupDTO) {
        Optional<EvaluationGroup> optional = evaluationGroupRepository.findById(id);
        if (optional.isPresent()) {
            EvaluationGroup entity = optional.get();
            entity.setGroupName(evaluationGroupDTO.getGroupName());
            entity.setDescription(evaluationGroupDTO.getDescription());
            // createdAt giữ nguyên
            EvaluationGroup saved = evaluationGroupRepository.save(entity);
            return convertToDTO(saved);
        }
        return null;
    }

    @Override
    public void deleteEvaluationGroup(Integer id) {
        evaluationGroupRepository.deleteById(id);
    }
}
