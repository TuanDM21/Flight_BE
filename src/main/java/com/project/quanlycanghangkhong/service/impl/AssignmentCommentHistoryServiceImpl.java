package com.project.quanlycanghangkhong.service.impl;

import com.project.quanlycanghangkhong.dto.AssignmentCommentHistoryDTO;
import com.project.quanlycanghangkhong.dto.UserDTO;
import com.project.quanlycanghangkhong.model.AssignmentCommentHistory;
import com.project.quanlycanghangkhong.model.User;
import com.project.quanlycanghangkhong.repository.AssignmentCommentHistoryRepository;
import com.project.quanlycanghangkhong.repository.UserRepository;
import com.project.quanlycanghangkhong.service.AssignmentCommentHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssignmentCommentHistoryServiceImpl implements AssignmentCommentHistoryService {
    @Autowired
    private AssignmentCommentHistoryRepository commentHistoryRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<AssignmentCommentHistoryDTO> getCommentsByAssignmentId(Long assignmentId) {
        List<AssignmentCommentHistory> historyList = commentHistoryRepository.findByAssignmentIdOrderByCreatedAtDesc(assignmentId);
        return historyList.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public void addComment(Long assignmentId, String comment, Long userId) {
        AssignmentCommentHistory entity = new AssignmentCommentHistory();
        entity.setAssignmentId(assignmentId);
        entity.setComment(comment);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUserId(userId);
        commentHistoryRepository.save(entity);
    }

    private AssignmentCommentHistoryDTO toDTO(AssignmentCommentHistory entity) {
        AssignmentCommentHistoryDTO dto = new AssignmentCommentHistoryDTO();
        dto.setId(entity.getId());
        dto.setAssignmentId(entity.getAssignmentId());
        dto.setComment(entity.getComment());
        dto.setCreatedAt(entity.getCreatedAt());
        UserDTO userDTO = null;
        if (entity.getUserId() != null) {
            User user = userRepository.findById(entity.getUserId().intValue()).orElse(null);
            if (user != null) {
                userDTO = new UserDTO(user);
            }
        }
        dto.setUser(userDTO);
        return dto;
    }
}
