package com.project.quanlycanghangkhong.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.quanlycanghangkhong.dto.UnitDTO;
import com.project.quanlycanghangkhong.service.UnitService;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/units")
public class UnitController {

	 @Autowired
	    private UnitService unitService;

	    // Nếu có teamId, trả về unit của team đó; nếu không có, trả về tất cả unit.
	    @GetMapping
	    @Operation(summary = "Get all units", description = "Retrieve a list of all units or units by teamId")
	    @ApiResponses(value = {
	        @io.swagger.v3.oas.annotations.responses.ApiResponse(
	            responseCode = "200",
	            description = "Successfully retrieved all units",
	            content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))
	        )
	    })
	    public ResponseEntity<ApiResponseCustom<List<UnitDTO>>> getUnits(@RequestParam(value = "teamId", required = false) Integer teamId) {
	        List<UnitDTO> dtos;
	        if (teamId != null) {
	            dtos = unitService.getUnitsByTeam(teamId);
	        } else {
	            dtos = unitService.getAllUnits();
	        }
	        return ResponseEntity.ok(ApiResponseCustom.success("Thành công", dtos));
	    }

	    @GetMapping("/assignable")
	    @Operation(summary = "Get assignable units", description = "Lấy danh sách unit mà user hiện tại có thể giao việc cho theo phân quyền")
	    public ResponseEntity<ApiResponseCustom<List<UnitDTO>>> getAssignableUnits() {
	        List<UnitDTO> dtos = unitService.getAssignableUnitsForCurrentUser();
	        return ResponseEntity.ok(ApiResponseCustom.success("Thành công", dtos));
	    }
}
