package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.dto.AssignmentDTO;
import java.util.List;

public interface AssignmentService {
    AssignmentDTO createAssignment(AssignmentDTO dto);
    AssignmentDTO updateAssignment(Integer assignmentId, AssignmentDTO dto);
    void deleteAssignment(Integer assignmentId);
    AssignmentDTO getAssignmentById(Integer assignmentId);
    List<AssignmentDTO> getAssignmentsByTaskId(Integer taskId);
}
