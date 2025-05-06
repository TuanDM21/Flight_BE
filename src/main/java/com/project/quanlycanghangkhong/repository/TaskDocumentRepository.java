package com.project.quanlycanghangkhong.repository;

import com.project.quanlycanghangkhong.model.TaskDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskDocumentRepository extends JpaRepository<TaskDocument, Integer> {
}