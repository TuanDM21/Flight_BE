package com.project.quanlycanghangkhong.dto.response.task;

import com.project.quanlycanghangkhong.dto.AttachmentDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiTaskAttachmentUploadResponse {
    private String message;
    private int statusCode;
    private List<AttachmentDTO> data;
    private boolean success;
}
