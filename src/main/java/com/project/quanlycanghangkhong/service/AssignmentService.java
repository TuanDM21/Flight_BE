package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.dto.AssignmentDTO;
import com.project.quanlycanghangkhong.dto.request.UpdateAssignmentRequest;
import java.util.List;

public interface AssignmentService {
    AssignmentDTO createAssignment(AssignmentDTO dto);
    AssignmentDTO updateAssignment(Integer assignmentId, UpdateAssignmentRequest request);
    void deleteAssignment(Integer assignmentId);
    AssignmentDTO getAssignmentById(Integer assignmentId);
    List<AssignmentDTO> getAssignmentsByTaskId(Integer taskId);
}
