package com.project.quanlycanghangkhong.repository;

import com.project.quanlycanghangkhong.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {
    List<Attachment> findByDocument_Id(Integer documentId);
}


