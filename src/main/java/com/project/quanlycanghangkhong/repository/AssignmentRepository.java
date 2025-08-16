package com.project.quanlycanghangkhong.repository;

import com.project.quanlycanghangkhong.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {
    List<Assignment> findByTask_Id(Integer taskId);
    
    /**
     * 🚀 OPTIMIZED: Get assignments by task ID (thay thế findAll + filter)
     * @param taskId Task ID
     * @return List of assignments for the task
     */
    @Query("SELECT a FROM Assignment a WHERE a.task.id = :taskId")
    List<Assignment> findByTaskId(@Param("taskId") Integer taskId);
}