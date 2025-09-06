package com.project.quanlycanghangkhong.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.quanlycanghangkhong.dto.TeamDTO;
import com.project.quanlycanghangkhong.model.Team;
import com.project.quanlycanghangkhong.service.TeamService;
import com.project.quanlycanghangkhong.dto.ApiResponse;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import com.project.quanlycanghangkhong.dto.response.team.TeamListApiResponse;
import com.project.quanlycanghangkhong.dto.response.team.TeamCreateApiResponse;
import com.project.quanlycanghangkhong.dto.response.team.AssignableTeamsApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/teams")
@Tag(name = "Team Management", description = "APIs for managing teams")
public class TeamController {

	@Autowired
	private TeamService teamService;

	@GetMapping
	@Operation(summary = "Get all teams", description = "Retrieve a list of all teams")
	@ApiResponses(value = {
	    @io.swagger.v3.oas.annotations.responses.ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved all teams",
	        content = @Content(schema = @Schema(implementation = TeamListApiResponse.class))
	    ),
	    @io.swagger.v3.oas.annotations.responses.ApiResponse(
	        responseCode = "500",
	        description = "Internal server error",
	        content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))
	    )
	})
	public ResponseEntity<ApiResponseCustom<List<TeamDTO>>> getAllTeams() {
		List<TeamDTO> dtos = teamService.getAllTeams();
		return ResponseEntity.ok(ApiResponseCustom.success("Thành công", dtos));
	}

	@PostMapping
	@Operation(summary = "Create a new team", description = "Create a new team with the provided details")
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Team created successfully", content = @Content(schema = @Schema(implementation = TeamCreateApiResponse.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
	})
	public ResponseEntity<ApiResponse<TeamDTO>> createTeam(@RequestBody TeamDTO teamDTO) {
		Team team = new Team();
		team.setTeamName(teamDTO.getTeamName());
		Team createdTeam = teamService.createTeam(team);
		TeamDTO dto = new TeamDTO(createdTeam.getId(), createdTeam.getTeamName());
		return ResponseEntity.ok(new ApiResponse<>("Thành công", 200, dto, true));
	}

	@GetMapping("/assignable")
	@Operation(summary = "Get assignable teams", description = "Lấy danh sách team mà user hiện tại có thể giao việc cho theo phân quyền")
	@ApiResponses(value = {
	    @io.swagger.v3.oas.annotations.responses.ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved assignable teams",
	        content = @Content(schema = @Schema(implementation = AssignableTeamsApiResponse.class))
	    ),
	    @io.swagger.v3.oas.annotations.responses.ApiResponse(
	        responseCode = "403",
	        description = "Forbidden - User not authorized",
	        content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))
	    ),
	    @io.swagger.v3.oas.annotations.responses.ApiResponse(
	        responseCode = "500",
	        description = "Internal server error",
	        content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))
	    )
	})
	public ResponseEntity<ApiResponseCustom<List<TeamDTO>>> getAssignableTeams() {
		List<TeamDTO> dtos = teamService.getAssignableTeamsForCurrentUser();
		return ResponseEntity.ok(ApiResponseCustom.success("Thành công", dtos));
	}
}
