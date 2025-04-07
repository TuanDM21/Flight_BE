package com.project.quanlycanghangkhong.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.quanlycanghangkhong.dto.TeamDTO;
import com.project.quanlycanghangkhong.service.TeamService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/teams")
public class TeamController {

	@Autowired
	private TeamService teamService;

	@GetMapping
	public ResponseEntity<List<TeamDTO>> getAllTeams() {
		List<TeamDTO> dtos = teamService.getAllTeams();
		return ResponseEntity.ok(dtos);
	}
}
