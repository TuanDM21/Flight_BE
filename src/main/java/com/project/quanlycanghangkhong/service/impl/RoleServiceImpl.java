package com.project.quanlycanghangkhong.service.impl;

import com.project.quanlycanghangkhong.model.Role;
import com.project.quanlycanghangkhong.repository.RoleRepository;
import com.project.quanlycanghangkhong.service.RoleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleRepository roleRepository;

	@Override
	public List<Role> getAllRoles() {
		return roleRepository.findAll();
	}

	@Override
	public Role getRoleById(Long id) {
		Optional<Role> roleOptional = roleRepository.findById(id.intValue());
		if (roleOptional.isPresent()) {
			return roleOptional.get();
		}
		throw new RuntimeException("Role not found with id: " + id);
	}

	@Override
	public Role createRole(Role role) {
		return roleRepository.save(role);
	}

	@Override
	public Role updateRole(Long id, Role roleDetails) {
		Role role = getRoleById(id);
		role.setRoleName(roleDetails.getRoleName());
		return roleRepository.save(role);
	}

	@Override
	public void deleteRole(Long id) {
		Role role = getRoleById(id);
		roleRepository.delete(role);
	}
}
