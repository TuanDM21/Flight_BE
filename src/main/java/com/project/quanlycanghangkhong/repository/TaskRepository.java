package com.project.quanlycanghangkhong.repository;

import com.project.quanlycanghangkhong.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findAllByDeletedFalse();
    Optional<Task> findByIdAndDeletedFalse(Integer id);
}