package com.project.quanlycanghangkhong.repository;

import com.project.quanlycanghangkhong.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {
}