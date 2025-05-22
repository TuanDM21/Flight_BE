package com.project.quanlycanghangkhong.service;

import com.azure.storage.blob.*;
import com.azure.storage.blob.models.*;
import com.project.quanlycanghangkhong.dto.AttachmentDTO;
import com.project.quanlycanghangkhong.model.Attachment;
import com.project.quanlycanghangkhong.repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AzureBlobService {
    private final Dotenv dotenv = Dotenv.load();
    private final String connectionString = dotenv.get("AZURE_STORAGE_CONNECTION_STRING");
    private final String containerName = dotenv.get("AZURE_STORAGE_CONTAINER_NAME");

    @Autowired
    private AttachmentRepository attachmentRepository;

    private AttachmentDTO toDTO(Attachment att) {
        AttachmentDTO dto = new AttachmentDTO();
        dto.setId(att.getId());
        dto.setFilePath(att.getFilePath());
        dto.setFileName(att.getFileName());
        dto.setFileSize(att.getFileSize());
        dto.setCreatedAt(att.getCreatedAt());
        return dto;
    }

    public List<AttachmentDTO> uploadFiles(MultipartFile[] files) throws Exception {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);

        List<AttachmentDTO> result = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            BlobClient blobClient = containerClient.getBlobClient(fileName);
            try (InputStream is = file.getInputStream()) {
                blobClient.upload(is, file.getSize(), true);
            }
            String fileUrl = blobClient.getBlobUrl();
            Attachment att = new Attachment();
            att.setFileName(file.getOriginalFilename());
            att.setFilePath(fileUrl);
            att.setFileSize(file.getSize());
            // createdAt sẽ tự động set bởi @CreationTimestamp
            Attachment saved = attachmentRepository.save(att);
            result.add(toDTO(saved));
        }
        return result;
    }

    public void deleteAttachmentAndBlob(Integer attachmentId) {
        Attachment att = attachmentRepository.findById(attachmentId).orElse(null);
        if (att == null) return;
        // Xoá file vật lý trên Azure Blob
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        String blobName = att.getFilePath();
        // Lấy tên blob từ filePath (URL)
        if (blobName != null && blobName.contains("/")) {
            blobName = blobName.substring(blobName.lastIndexOf("/") + 1);
        }
        BlobClient blobClient = containerClient.getBlobClient(blobName);
        if (blobClient.exists()) {
            blobClient.delete();
        }
        // Xoá metadata trong database
        attachmentRepository.deleteById(attachmentId);
    }
}

