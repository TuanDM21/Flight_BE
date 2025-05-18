package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.dto.DocumentDTO;
import com.project.quanlycanghangkhong.service.TaskDocumentService;
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
    public ResponseEntity<List<DocumentDTO>> getDocumentsByTask(@RequestParam Integer taskId) {
        return ResponseEntity.ok(taskDocumentService.getDocumentsByTaskId(taskId));
    }

    @PostMapping("/attach")
    public ResponseEntity<Void> attachDocuments(@RequestParam Integer taskId, @RequestParam List<Integer> documentIds) {
        for (Integer documentId : documentIds) {
            taskDocumentService.attachDocumentToTask(taskId, documentId);
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeDocument(@RequestParam Integer taskId, @RequestParam Integer documentId) {
        taskDocumentService.removeDocumentFromTask(taskId, documentId);
        return ResponseEntity.ok().build();
    }
}
