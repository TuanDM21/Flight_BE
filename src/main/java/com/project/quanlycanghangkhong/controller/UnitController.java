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
import com.project.quanlycanghangkhong.dto.ApiResponse;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/units")
public class UnitController {

	 @Autowired
	    private UnitService unitService;

	    // Nếu có teamId, trả về unit của team đó; nếu không có, trả về tất cả unit.
	    @GetMapping
	    public ResponseEntity<ApiResponse<List<UnitDTO>>> getUnits(@RequestParam(value = "teamId", required = false) Integer teamId) {
	        List<UnitDTO> dtos;
	        if (teamId != null) {
	            dtos = unitService.getUnitsByTeam(teamId);
	        } else {
	            dtos = unitService.getAllUnits();
	        }
	        return ResponseEntity.ok(new ApiResponse<>("Thành công", 200, dtos, true));
	    }
}
