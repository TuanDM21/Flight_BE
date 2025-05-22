package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.dto.AssignmentDTO;
import com.project.quanlycanghangkhong.dto.request.UpdateAssignmentRequest;
import com.project.quanlycanghangkhong.dto.CreateAssignmentRequest;
import com.project.quanlycanghangkhong.dto.request.CreateAssignmentsRequest;
import java.util.List;

public interface AssignmentService {
    AssignmentDTO createAssignment(CreateAssignmentRequest request);
    List<AssignmentDTO> createAssignments(CreateAssignmentsRequest request);
    AssignmentDTO updateAssignment(Integer assignmentId, UpdateAssignmentRequest request);
    void deleteAssignment(Integer assignmentId);
    AssignmentDTO getAssignmentById(Integer assignmentId);
    List<AssignmentDTO> getAssignmentsByTaskId(Integer taskId);
    void addAssignmentComment(Integer assignmentId, String comment);
    // Nếu có các hàm public cần expose từ AssignmentServiceImpl (ví dụ updateAssignmentStatus), hãy bổ sung tại đây nếu cần dùng ngoài controller/service khác.
}
