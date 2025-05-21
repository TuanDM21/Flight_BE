package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.dto.AssignmentCommentHistoryDTO;
import java.util.List;

public interface AssignmentCommentHistoryService {
    List<AssignmentCommentHistoryDTO> getCommentsByAssignmentId(Long assignmentId);
    void addComment(Long assignmentId, String comment, Long userId);
}
