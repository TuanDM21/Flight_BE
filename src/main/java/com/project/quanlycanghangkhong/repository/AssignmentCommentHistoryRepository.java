package com.project.quanlycanghangkhong.repository;

import com.project.quanlycanghangkhong.model.AssignmentCommentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentCommentHistoryRepository extends JpaRepository<AssignmentCommentHistory, Long> {
    List<AssignmentCommentHistory> findByAssignmentIdOrderByCreatedAtDesc(Long assignmentId);
}
