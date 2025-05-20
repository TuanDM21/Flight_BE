package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.dto.EvaluationIssueDTO;
import com.project.quanlycanghangkhong.service.EvaluationIssueService;
import com.project.quanlycanghangkhong.dto.response.evaluationissue.ApiEvaluationIssueResponse;
import com.project.quanlycanghangkhong.dto.response.evaluationissue.ApiEvaluationIssueListResponse;
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
@RequestMapping("/api/evaluation-issues")
@CrossOrigin(origins = "*")
public class EvaluationIssueController {
    @Autowired
    private EvaluationIssueService evaluationIssueService;

    @GetMapping
    @Operation(summary = "Lấy danh sách evaluation issue", description = "Lấy tất cả evaluation issue")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách evaluation issue thành công", content = @Content(schema = @Schema(implementation = ApiEvaluationIssueListResponse.class)))
    })
    public ResponseEntity<ApiEvaluationIssueListResponse> getAllIssues() {
        List<EvaluationIssueDTO> dtos = evaluationIssueService.getAllIssues();
        ApiEvaluationIssueListResponse response = new ApiEvaluationIssueListResponse("Thành công", 200, dtos, true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/session/{sessionId}")
    @Operation(summary = "Lấy danh sách evaluation issue theo session", description = "Lấy tất cả evaluation issue theo sessionId")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách evaluation issue thành công", content = @Content(schema = @Schema(implementation = ApiEvaluationIssueListResponse.class)))
    })
    public ResponseEntity<ApiEvaluationIssueListResponse> getIssuesBySession(@PathVariable Integer sessionId) {
        List<EvaluationIssueDTO> dtos = evaluationIssueService.getIssuesBySession(sessionId);
        ApiEvaluationIssueListResponse response = new ApiEvaluationIssueListResponse("Thành công", 200, dtos, true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết evaluation issue", description = "Lấy chi tiết một evaluation issue theo id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy evaluation issue thành công", content = @Content(schema = @Schema(implementation = ApiEvaluationIssueResponse.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy evaluation issue", content = @Content(schema = @Schema(implementation = ApiEvaluationIssueResponse.class)))
    })
    public ResponseEntity<ApiEvaluationIssueResponse> getIssueById(@PathVariable Integer id) {
        EvaluationIssueDTO dto = evaluationIssueService.getIssueById(id);
        if (dto == null) {
            return ResponseEntity.status(404).body(new ApiEvaluationIssueResponse("Không tìm thấy evaluation issue", 404, null, false));
        }
        ApiEvaluationIssueResponse response = new ApiEvaluationIssueResponse("Thành công", 200, dto, true);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Tạo evaluation issue", description = "Tạo mới một evaluation issue")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tạo evaluation issue thành công", content = @Content(schema = @Schema(implementation = ApiEvaluationIssueResponse.class)))
    })
    public ResponseEntity<ApiEvaluationIssueResponse> createIssue(@RequestBody EvaluationIssueDTO dto) {
        EvaluationIssueDTO created = evaluationIssueService.createIssue(dto);
        ApiEvaluationIssueResponse response = new ApiEvaluationIssueResponse("Tạo thành công", 201, created, true);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật evaluation issue", description = "Cập nhật thông tin evaluation issue")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cập nhật evaluation issue thành công", content = @Content(schema = @Schema(implementation = ApiEvaluationIssueResponse.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy evaluation issue", content = @Content(schema = @Schema(implementation = ApiEvaluationIssueResponse.class)))
    })
    public ResponseEntity<ApiEvaluationIssueResponse> updateIssue(@PathVariable Integer id, @RequestBody EvaluationIssueDTO dto) {
        EvaluationIssueDTO updated = evaluationIssueService.updateIssue(id, dto);
        if (updated == null) {
            return ResponseEntity.status(404).body(new ApiEvaluationIssueResponse("Không tìm thấy evaluation issue", 404, null, false));
        }
        ApiEvaluationIssueResponse response = new ApiEvaluationIssueResponse("Cập nhật thành công", 200, updated, true);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xoá evaluation issue", description = "Xoá một evaluation issue theo id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Xoá evaluation issue thành công", content = @Content(schema = @Schema(implementation = ApiEvaluationIssueResponse.class)))
    })
    public ResponseEntity<ApiEvaluationIssueResponse> deleteIssue(@PathVariable Integer id) {
        evaluationIssueService.deleteIssue(id);
        ApiEvaluationIssueResponse response = new ApiEvaluationIssueResponse("Xoá thành công", 200, null, true);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Cập nhật trạng thái evaluation issue", description = "Cập nhật trạng thái evaluation issue")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cập nhật trạng thái thành công", content = @Content(schema = @Schema(implementation = ApiEvaluationIssueResponse.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy evaluation issue", content = @Content(schema = @Schema(implementation = ApiEvaluationIssueResponse.class)))
    })
    public ResponseEntity<ApiEvaluationIssueResponse> updateIssueStatus(@PathVariable Integer id, @RequestBody StatusUpdateRequest request) {
        EvaluationIssueDTO updated = evaluationIssueService.updateIssueStatus(id, request.getIsResolved(), request.getResolutionDate());
        if (updated == null) {
            return ResponseEntity.status(404).body(new ApiEvaluationIssueResponse("Không tìm thấy evaluation issue", 404, null, false));
        }
        ApiEvaluationIssueResponse response = new ApiEvaluationIssueResponse("Cập nhật trạng thái thành công", 200, updated, true);
        return ResponseEntity.ok(response);
    }

    public static class StatusUpdateRequest {
        private Boolean isResolved;
        private java.time.LocalDate resolutionDate;
        public Boolean getIsResolved() { return isResolved; }
        public void setIsResolved(Boolean isResolved) { this.isResolved = isResolved; }
        public java.time.LocalDate getResolutionDate() { return resolutionDate; }
        public void setResolutionDate(java.time.LocalDate resolutionDate) { this.resolutionDate = resolutionDate; }
    }
}
