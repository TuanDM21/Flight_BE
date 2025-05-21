package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.dto.AssignmentDTO;
import com.project.quanlycanghangkhong.dto.request.UpdateAssignmentRequest;
import com.project.quanlycanghangkhong.dto.AssignmentStatusHistoryDTO;
import com.project.quanlycanghangkhong.model.AssignmentStatusHistory;
import com.project.quanlycanghangkhong.repository.AssignmentStatusHistoryRepository;
import com.project.quanlycanghangkhong.service.AssignmentService;
import com.project.quanlycanghangkhong.dto.response.assignment.ApiAssignmentResponse;
import com.project.quanlycanghangkhong.dto.response.assignment.ApiAssignmentListResponse;
import com.project.quanlycanghangkhong.dto.response.assignment.ApiAssignmentStatusHistoryResponse;
import com.project.quanlycanghangkhong.dto.AssignmentCommentRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    private AssignmentStatusHistoryRepository assignmentStatusHistoryRepository;

    // Giao công việc (tạo mới assignment)
    @PostMapping
    @Operation(summary = "Tạo assignment", description = "Tạo mới một assignment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tạo assignment thành công", content = @Content(schema = @Schema(implementation = ApiAssignmentResponse.class)))
    })
    public ResponseEntity<ApiAssignmentResponse> createAssignment(@RequestBody AssignmentDTO dto) {
        AssignmentDTO result = assignmentService.createAssignment(dto);
        ApiAssignmentResponse response = new ApiAssignmentResponse("Tạo thành công", 201, result, true);
        return ResponseEntity.status(201).body(response);
    }

    // Cập nhật giao công việc
    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật assignment", description = "Cập nhật thông tin assignment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cập nhật assignment thành công", content = @Content(schema = @Schema(implementation = ApiAssignmentResponse.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy assignment", content = @Content(schema = @Schema(implementation = ApiAssignmentResponse.class)))
    })
    public ResponseEntity<ApiAssignmentResponse> updateAssignment(@PathVariable Integer id, @RequestBody UpdateAssignmentRequest request) {
        AssignmentDTO result = assignmentService.updateAssignment(id, request);
        if (result == null) return ResponseEntity.status(404).body(new ApiAssignmentResponse("Không tìm thấy assignment", 404, null, false));
        ApiAssignmentResponse response = new ApiAssignmentResponse("Cập nhật thành công", 200, result, true);
        return ResponseEntity.ok(response);
    }

    // Xoá giao công việc
    @DeleteMapping("/{id}")
    @Operation(summary = "Xoá assignment", description = "Xoá một assignment theo id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Xoá assignment thành công", content = @Content(schema = @Schema(implementation = ApiAssignmentResponse.class)))
    })
    public ResponseEntity<ApiAssignmentResponse> deleteAssignment(@PathVariable Integer id) {
        assignmentService.deleteAssignment(id);
        ApiAssignmentResponse response = new ApiAssignmentResponse("Xoá thành công", 200, null, true);
        return ResponseEntity.ok(response);
    }

    // Xem chi tiết giao công việc
    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết assignment", description = "Lấy chi tiết một assignment theo id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy assignment thành công", content = @Content(schema = @Schema(implementation = ApiAssignmentResponse.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy assignment", content = @Content(schema = @Schema(implementation = ApiAssignmentResponse.class)))
    })
    public ResponseEntity<ApiAssignmentResponse> getAssignmentById(@PathVariable Integer id) {
        AssignmentDTO result = assignmentService.getAssignmentById(id);
        if (result == null) return ResponseEntity.status(404).body(new ApiAssignmentResponse("Không tìm thấy assignment", 404, null, false));
        ApiAssignmentResponse response = new ApiAssignmentResponse("Thành công", 200, result, true);
        return ResponseEntity.ok(response);
    }   

    // Lấy danh sách giao công việc theo task
    @GetMapping("/task/{taskId}")
    @Operation(summary = "Lấy danh sách assignment theo task", description = "Lấy danh sách assignment theo taskId")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách assignment thành công", content = @Content(schema = @Schema(implementation = ApiAssignmentListResponse.class)))
    })
    public ResponseEntity<ApiAssignmentListResponse> getAssignmentsByTaskId(@PathVariable Integer taskId) {
        List<AssignmentDTO> result = assignmentService.getAssignmentsByTaskId(taskId);
        ApiAssignmentListResponse response = new ApiAssignmentListResponse("Thành công", 200, result, true);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/comment")
    @Operation(summary = "Thêm comment cho assignment", description = "Thêm comment vào assignment, nhận JSON {\"comment\": \"...\"}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thêm comment thành công", content = @Content(schema = @Schema(implementation = ApiAssignmentResponse.class)))
    })
    public ResponseEntity<ApiAssignmentResponse> addAssignmentComment(
            @PathVariable Integer id,
            @RequestBody AssignmentCommentRequest request) {
        assignmentService.addAssignmentComment(id, request.getComment());
        ApiAssignmentResponse response = new ApiAssignmentResponse("Thêm comment thành công", 200, null, true);
        return ResponseEntity.ok(response);
    }

    // Đã xoá endpoint lấy lịch sử trạng thái assignment (GET /{id}/status-history) vì chỉ lấy comment.

    @GetMapping("/{id}/comments")
    @Operation(summary = "Lấy danh sách comment của assignment", description = "Lấy tất cả comment của assignment, chỉ gồm id, assignmentId, comment, changedAt, userId")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách comment thành công", content = @Content(schema = @Schema(implementation = ApiAssignmentStatusHistoryResponse.class)))
    })
    public ResponseEntity<ApiAssignmentStatusHistoryResponse> getAssignmentComments(@PathVariable Integer id) {
        List<AssignmentStatusHistory> historyList = assignmentStatusHistoryRepository.findByAssignmentIdOrderByChangedAtDesc(Long.valueOf(id));
        List<AssignmentStatusHistoryDTO> dtoList = historyList.stream().map(h -> {
            AssignmentStatusHistoryDTO dto = new AssignmentStatusHistoryDTO();
            dto.setId(h.getId());
            dto.setAssignmentId(h.getAssignmentId());
            dto.setComment(h.getComment());
            dto.setChangedAt(h.getChangedAt());
            dto.setUserId(h.getUserId());
            return dto;
        }).toList();
        ApiAssignmentStatusHistoryResponse response = new ApiAssignmentStatusHistoryResponse("Thành công", 200, dtoList, true);
        return ResponseEntity.ok(response);
    }
}
