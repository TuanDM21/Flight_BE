package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.dto.EvaluationGroupDTO;
import com.project.quanlycanghangkhong.service.EvaluationGroupService;
import com.project.quanlycanghangkhong.dto.response.evaluationgroup.ApiEvaluationGroupResponse;
import com.project.quanlycanghangkhong.dto.response.evaluationgroup.ApiEvaluationGroupListResponse;
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
@RequestMapping("/api/evaluation-groups")
@CrossOrigin(origins = "*")
public class EvaluationGroupController {
    @Autowired
    private EvaluationGroupService evaluationGroupService;

    @GetMapping
    @Operation(summary = "Lấy danh sách evaluation group", description = "Lấy tất cả evaluation group")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách evaluation group thành công", content = @Content(schema = @Schema(implementation = ApiEvaluationGroupListResponse.class)))
    })
    public ResponseEntity<ApiEvaluationGroupListResponse> getAllEvaluationGroups() {
        List<EvaluationGroupDTO> dtos = evaluationGroupService.getAllEvaluationGroups();
        ApiEvaluationGroupListResponse response = new ApiEvaluationGroupListResponse("Thành công", 200, dtos, true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết evaluation group", description = "Lấy chi tiết một evaluation group theo id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy evaluation group thành công", content = @Content(schema = @Schema(implementation = ApiEvaluationGroupResponse.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy evaluation group", content = @Content(schema = @Schema(implementation = ApiEvaluationGroupResponse.class)))
    })
    public ResponseEntity<ApiEvaluationGroupResponse> getEvaluationGroupById(@PathVariable Integer id) {
        EvaluationGroupDTO dto = evaluationGroupService.getEvaluationGroupById(id);
        if (dto == null) {
            return ResponseEntity.status(404).body(new ApiEvaluationGroupResponse("Không tìm thấy evaluation group", 404, null, false));
        }
        ApiEvaluationGroupResponse response = new ApiEvaluationGroupResponse("Thành công", 200, dto, true);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Tạo evaluation group", description = "Tạo mới một evaluation group")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tạo evaluation group thành công", content = @Content(schema = @Schema(implementation = ApiEvaluationGroupResponse.class)))
    })
    public ResponseEntity<ApiEvaluationGroupResponse> createEvaluationGroup(@RequestBody EvaluationGroupDTO evaluationGroupDTO) {
        EvaluationGroupDTO created = evaluationGroupService.createEvaluationGroup(evaluationGroupDTO);
        ApiEvaluationGroupResponse response = new ApiEvaluationGroupResponse("Tạo thành công", 201, created, true);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật evaluation group", description = "Cập nhật thông tin evaluation group")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cập nhật evaluation group thành công", content = @Content(schema = @Schema(implementation = ApiEvaluationGroupResponse.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy evaluation group", content = @Content(schema = @Schema(implementation = ApiEvaluationGroupResponse.class)))
    })
    public ResponseEntity<ApiEvaluationGroupResponse> updateEvaluationGroup(@PathVariable Integer id, @RequestBody EvaluationGroupDTO evaluationGroupDTO) {
        EvaluationGroupDTO updated = evaluationGroupService.updateEvaluationGroup(id, evaluationGroupDTO);
        if (updated == null) {
            return ResponseEntity.status(404).body(new ApiEvaluationGroupResponse("Không tìm thấy evaluation group", 404, null, false));
        }
        ApiEvaluationGroupResponse response = new ApiEvaluationGroupResponse("Cập nhật thành công", 200, updated, true);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xoá evaluation group", description = "Xoá một evaluation group theo id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Xoá evaluation group thành công", content = @Content(schema = @Schema(implementation = ApiEvaluationGroupResponse.class)))
    })
    public ResponseEntity<ApiEvaluationGroupResponse> deleteEvaluationGroup(@PathVariable Integer id) {
        evaluationGroupService.deleteEvaluationGroup(id);
        ApiEvaluationGroupResponse response = new ApiEvaluationGroupResponse("Xoá thành công", 200, null, true);
        return ResponseEntity.ok(response);
    }
}
