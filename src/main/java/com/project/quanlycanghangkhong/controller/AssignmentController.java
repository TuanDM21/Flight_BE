package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.dto.AssignmentDTO;
import com.project.quanlycanghangkhong.request.UpdateAssignmentRequest;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import com.project.quanlycanghangkhong.request.AssignmentCommentRequest;
import com.project.quanlycanghangkhong.dto.AssignmentCommentHistoryDTO;
import com.project.quanlycanghangkhong.service.AssignmentService;
import com.project.quanlycanghangkhong.service.AssignmentCommentHistoryService;
import com.project.quanlycanghangkhong.request.CreateAssignmentsRequest;
import com.project.quanlycanghangkhong.model.User;
import com.project.quanlycanghangkhong.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/assignments")
@CrossOrigin(origins = "*")
public class AssignmentController {
    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private AssignmentCommentHistoryService assignmentCommentHistoryService;

    @Autowired
    private UserRepository userRepository;

    // Giao công việc (tạo mới assignment)
    @PostMapping
    @Operation(summary = "Tạo assignment cho nhiều người", description = "Tạo mới nhiều assignment cùng lúc cho nhiều người nhận việc")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tạo assignment thành công", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<List<AssignmentDTO>>> createAssignments(@RequestBody CreateAssignmentsRequest request) {
        List<AssignmentDTO> result = assignmentService.createAssignments(request);
        return ResponseEntity.status(201).body(ApiResponseCustom.created(result));
    }

    // Cập nhật giao công việc
    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật assignment", description = "Cập nhật thông tin assignment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cập nhật assignment thành công", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy assignment", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<AssignmentDTO>> updateAssignment(@PathVariable Integer id, @RequestBody UpdateAssignmentRequest request) {
        AssignmentDTO result = assignmentService.updateAssignment(id, request);
        if (result == null) return ResponseEntity.status(404).body(ApiResponseCustom.notFound("Không tìm thấy assignment"));
        return ResponseEntity.ok(ApiResponseCustom.updated(result));
    }

    // Xoá giao công việc
    @DeleteMapping("/{id}")
    @Operation(summary = "Xoá assignment", description = "Xoá một assignment theo id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Xoá assignment thành công", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<Void>> deleteAssignment(@PathVariable Integer id) {
        assignmentService.deleteAssignment(id);
        return ResponseEntity.ok(ApiResponseCustom.deleted());
    }

    // Xem chi tiết giao công việc
    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết assignment", description = "Lấy chi tiết một assignment theo id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy assignment thành công", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy assignment", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<AssignmentDTO>> getAssignmentById(@PathVariable Integer id) {
        AssignmentDTO result = assignmentService.getAssignmentById(id);
        if (result == null) return ResponseEntity.status(404).body(ApiResponseCustom.notFound("Không tìm thấy assignment"));
        return ResponseEntity.ok(ApiResponseCustom.success(result));
    }   

    // Lấy danh sách giao công việc theo task
    @GetMapping("/task/{taskId}")
    @Operation(summary = "Lấy danh sách assignment theo task", description = "Lấy danh sách assignment theo taskId")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách assignment thành công", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<List<AssignmentDTO>>> getAssignmentsByTaskId(@PathVariable Integer taskId) {
        List<AssignmentDTO> result = assignmentService.getAssignmentsByTaskId(taskId);
        return ResponseEntity.ok(ApiResponseCustom.success(result));
    }

    @PostMapping("/{id}/comment")
    @Operation(summary = "Thêm comment cho assignment", description = "Thêm comment vào assignment, nhận JSON {\"comment\": \"...\"}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thêm comment thành công", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<List<AssignmentCommentHistoryDTO>>> addAssignmentComment(
            @PathVariable Integer id,
            @RequestBody AssignmentCommentRequest request) {
        
        // Lấy thông tin user đang đăng nhập
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin user đang đăng nhập"));
        
        assignmentCommentHistoryService.addComment(Long.valueOf(id), request.getComment(), currentUser.getId().longValue());
        
        // Lấy lại danh sách comment mới nhất sau khi thêm
        List<AssignmentCommentHistoryDTO> dtoList = assignmentCommentHistoryService.getCommentsByAssignmentId(Long.valueOf(id));
        return ResponseEntity.ok(ApiResponseCustom.success(dtoList));
    }

    @GetMapping("/{id}/comments")
    @Operation(summary = "Lấy danh sách comment của assignment", description = "Lấy tất cả comment của assignment, gồm id, assignmentId, comment, createdAt, user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách comment thành công", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<List<AssignmentCommentHistoryDTO>>> getAssignmentComments(@PathVariable Integer id) {
        List<AssignmentCommentHistoryDTO> dtoList = assignmentCommentHistoryService.getCommentsByAssignmentId(Long.valueOf(id));
        return ResponseEntity.ok(ApiResponseCustom.success(dtoList));
    }
}
