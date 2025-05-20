package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.dto.EvaluationSessionDTO;
import com.project.quanlycanghangkhong.service.EvaluationSessionService;
import com.project.quanlycanghangkhong.dto.response.evaluationsession.ApiEvaluationSessionResponse;
import com.project.quanlycanghangkhong.dto.response.evaluationsession.ApiEvaluationSessionListResponse;
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
@RequestMapping("/api/evaluation-sessions")
@CrossOrigin(origins = "*")
public class EvaluationSessionController {
    @Autowired
    private EvaluationSessionService evaluationSessionService;

    @GetMapping
    @Operation(summary = "Lấy danh sách evaluation session", description = "Lấy tất cả evaluation session")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách evaluation session thành công", content = @Content(schema = @Schema(implementation = ApiEvaluationSessionListResponse.class)))
    })
    public ResponseEntity<ApiEvaluationSessionListResponse> getAllEvaluationSessions() {
        List<EvaluationSessionDTO> dtos = evaluationSessionService.getAllEvaluationSessions();
        ApiEvaluationSessionListResponse response = new ApiEvaluationSessionListResponse("Thành công", 200, dtos, true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết evaluation session", description = "Lấy chi tiết một evaluation session theo id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy evaluation session thành công", content = @Content(schema = @Schema(implementation = ApiEvaluationSessionResponse.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy evaluation session", content = @Content(schema = @Schema(implementation = ApiEvaluationSessionResponse.class)))
    })
    public ResponseEntity<ApiEvaluationSessionResponse> getEvaluationSessionById(@PathVariable Integer id) {
        EvaluationSessionDTO dto = evaluationSessionService.getEvaluationSessionById(id);
        if (dto == null) {
            return ResponseEntity.status(404).body(new ApiEvaluationSessionResponse("Không tìm thấy evaluation session", 404, null, false));
        }
        ApiEvaluationSessionResponse response = new ApiEvaluationSessionResponse("Thành công", 200, dto, true);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Tạo evaluation session", description = "Tạo mới một evaluation session")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tạo evaluation session thành công", content = @Content(schema = @Schema(implementation = ApiEvaluationSessionResponse.class)))
    })
    public ResponseEntity<ApiEvaluationSessionResponse> createEvaluationSession(@RequestBody EvaluationSessionDTO dto) {
        EvaluationSessionDTO created = evaluationSessionService.createEvaluationSession(dto);
        ApiEvaluationSessionResponse response = new ApiEvaluationSessionResponse("Tạo thành công", 201, created, true);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật evaluation session", description = "Cập nhật thông tin evaluation session")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cập nhật evaluation session thành công", content = @Content(schema = @Schema(implementation = ApiEvaluationSessionResponse.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy evaluation session", content = @Content(schema = @Schema(implementation = ApiEvaluationSessionResponse.class)))
    })
    public ResponseEntity<ApiEvaluationSessionResponse> updateEvaluationSession(@PathVariable Integer id, @RequestBody EvaluationSessionDTO dto) {
        EvaluationSessionDTO updated = evaluationSessionService.updateEvaluationSession(id, dto);
        if (updated == null) {
            return ResponseEntity.status(404).body(new ApiEvaluationSessionResponse("Không tìm thấy evaluation session", 404, null, false));
        }
        ApiEvaluationSessionResponse response = new ApiEvaluationSessionResponse("Cập nhật thành công", 200, updated, true);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xoá evaluation session", description = "Xoá một evaluation session theo id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Xoá evaluation session thành công", content = @Content(schema = @Schema(implementation = ApiEvaluationSessionResponse.class)))
    })
    public ResponseEntity<ApiEvaluationSessionResponse> deleteEvaluationSession(@PathVariable Integer id) {
        evaluationSessionService.deleteEvaluationSession(id);
        ApiEvaluationSessionResponse response = new ApiEvaluationSessionResponse("Xoá thành công", 200, null, true);
        return ResponseEntity.ok(response);
    }
}
