package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.dto.RoleDTO;
import com.project.quanlycanghangkhong.model.Role;
import com.project.quanlycanghangkhong.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/roles")
public class RoleController {

	@Autowired
	private RoleService roleService;

	@GetMapping
	public ResponseEntity<List<RoleDTO>> getAllRoles() {
		List<Role> roles = roleService.getAllRoles();
		List<RoleDTO> roleDTOs = roles.stream()
				.map(role -> new RoleDTO(role))
				.collect(Collectors.toList());
		return ResponseEntity.ok(roleDTOs);
	}

	@GetMapping("/{id}")
	public ResponseEntity<RoleDTO> getRoleById(@PathVariable Long id) {
		try {
			Role role = roleService.getRoleById(id);
			return ResponseEntity.ok(new RoleDTO(role));
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping
	public ResponseEntity<RoleDTO> createRole(@RequestBody RoleDTO roleDTO) {
		Role role = new Role();
		role.setRoleName(roleDTO.getRoleName());

		Role createdRole = roleService.createRole(role);
		return ResponseEntity.status(HttpStatus.CREATED).body(new RoleDTO(createdRole));
	}

	@PutMapping("/{id}")
	public ResponseEntity<RoleDTO> updateRole(@PathVariable Long id, @RequestBody RoleDTO roleDTO) {
		try {
			Role role = new Role();
			role.setRoleName(roleDTO.getRoleName());

			Role updatedRole = roleService.updateRole(id, role);
			return ResponseEntity.ok(new RoleDTO(updatedRole));
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
		try {
			roleService.deleteRole(id);
			return ResponseEntity.noContent().build();
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}
}
