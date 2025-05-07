package com.project.quanlycanghangkhong.repository;

import com.project.quanlycanghangkhong.model.TaskDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import com.project.quanlycanghangkhong.model.Task;
import com.project.quanlycanghangkhong.model.Document;

@Repository
public interface TaskDocumentRepository extends JpaRepository<TaskDocument, Integer> {
    Optional<TaskDocument> findByTaskAndDocument(Task task, Document document);
}