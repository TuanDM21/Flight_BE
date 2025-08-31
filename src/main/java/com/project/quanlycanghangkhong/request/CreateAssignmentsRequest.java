package com.project.quanlycanghangkhong.request;
import com.project.quanlycanghangkhong.request.CreateAssignmentRequest;
import java.util.List;

public class CreateAssignmentsRequest {
    private Integer taskId;
    private List<CreateAssignmentRequest> assignments;
    public Integer getTaskId() { return taskId; }
    public void setTaskId(Integer taskId) { this.taskId = taskId; }
    public List<CreateAssignmentRequest> getAssignments() { return assignments; }
    public void setAssignments(List<CreateAssignmentRequest> assignments) { this.assignments = assignments; }
}
