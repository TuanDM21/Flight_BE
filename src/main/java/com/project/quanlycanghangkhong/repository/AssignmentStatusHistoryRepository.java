package com.project.quanlycanghangkhong.repository;

import com.project.quanlycanghangkhong.model.AssignmentStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentStatusHistoryRepository extends JpaRepository<AssignmentStatusHistory, Long> {
    List<AssignmentStatusHistory> findByAssignmentIdOrderByChangedAtDesc(Long assignmentId);
}
