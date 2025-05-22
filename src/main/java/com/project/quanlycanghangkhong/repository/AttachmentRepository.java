package com.project.quanlycanghangkhong.repository;

import com.project.quanlycanghangkhong.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {
    List<Attachment> findByDocument_Id(Integer documentId);
    List<Attachment> findAllByIdIn(List<Integer> ids);
    // Lấy tất cả attachment chưa bị xoá mềm
    List<Attachment> findByIsDeletedFalse();
    // Lấy attachment theo documentId và chưa bị xoá mềm
    List<Attachment> findByDocument_IdAndIsDeletedFalse(Integer documentId);
    // Lấy attachment theo id và chưa bị xoá mềm
    Attachment findByIdAndIsDeletedFalse(Integer id);
}


