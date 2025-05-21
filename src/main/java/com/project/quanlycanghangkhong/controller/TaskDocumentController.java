package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.dto.DocumentDTO;
import com.project.quanlycanghangkhong.service.TaskDocumentService;
import com.project.quanlycanghangkhong.dto.response.taskdocument.ApiTaskDocumentListResponse;
import com.project.quanlycanghangkhong.dto.response.taskdocument.ApiTaskDocumentActionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task-documents")
@CrossOrigin(origins = "*")
public class TaskDocumentController {
    @Autowired
    private TaskDocumentService taskDocumentService;

    @GetMapping
    @Operation(summary = "Lấy danh sách document của task", description = "Lấy tất cả document gắn với một task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách document thành công", content = @Content(schema = @Schema(implementation = ApiTaskDocumentListResponse.class)))
    })
    public ResponseEntity<ApiTaskDocumentListResponse> getDocumentsByTask(@RequestParam Integer taskId) {
        List<DocumentDTO> docs = taskDocumentService.getDocumentsByTaskId(taskId);
        ApiTaskDocumentListResponse response = new ApiTaskDocumentListResponse("Thành công", 200, docs, true);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/attach")
    @Operation(summary = "Gắn document vào task", description = "Gắn một hoặc nhiều document vào task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Gắn document thành công", content = @Content(schema = @Schema(implementation = ApiTaskDocumentActionResponse.class)))
    })
    public ResponseEntity<ApiTaskDocumentActionResponse> attachDocuments(@RequestParam Integer taskId, @RequestParam List<Integer> documentIds) {
        for (Integer documentId : documentIds) {
            taskDocumentService.attachDocumentToTask(taskId, documentId);
        }
        ApiTaskDocumentActionResponse response = new ApiTaskDocumentActionResponse("Gắn document thành công", 200, true);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/attach-bulk")
    @Operation(summary = "Gắn nhiều document vào task", description = "Gắn nhiều document vào một task (bulk)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Gắn document thành công", content = @Content(schema = @Schema(implementation = ApiTaskDocumentActionResponse.class)))
    })
    public ResponseEntity<ApiTaskDocumentActionResponse> attachDocumentsBulk(@RequestParam Integer taskId, @RequestBody List<Integer> documentIds) {
        taskDocumentService.bulkAttachDocumentsToTask(taskId, documentIds);
        ApiTaskDocumentActionResponse response = new ApiTaskDocumentActionResponse("Gắn document thành công", 200, true);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove")
    @Operation(summary = "Gỡ document khỏi task", description = "Gỡ một document khỏi task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Gỡ document thành công", content = @Content(schema = @Schema(implementation = ApiTaskDocumentActionResponse.class)))
    })
    public ResponseEntity<ApiTaskDocumentActionResponse> removeDocument(@RequestParam Integer taskId, @RequestParam Integer documentId) {
        taskDocumentService.removeDocumentFromTask(taskId, documentId);
        ApiTaskDocumentActionResponse response = new ApiTaskDocumentActionResponse("Gỡ document thành công", 200, true);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove-bulk")
    @Operation(summary = "Gỡ nhiều document khỏi task", description = "Gỡ nhiều document khỏi một task (bulk)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Gỡ document thành công", content = @Content(schema = @Schema(implementation = ApiTaskDocumentActionResponse.class)))
    })
    public ResponseEntity<ApiTaskDocumentActionResponse> removeDocumentsBulk(@RequestParam Integer taskId, @RequestBody List<Integer> documentIds) {
        taskDocumentService.bulkRemoveDocumentsFromTask(taskId, documentIds);
        ApiTaskDocumentActionResponse response = new ApiTaskDocumentActionResponse("Gỡ document thành công", 200, true);
        return ResponseEntity.ok(response);
    }
}
