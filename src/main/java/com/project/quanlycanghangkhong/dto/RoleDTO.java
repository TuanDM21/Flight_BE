package com.project.quanlycanghangkhong.dto;

import com.project.quanlycanghangkhong.model.Role;

public class RoleDTO {
	private Integer id;
	private String roleName;

	public RoleDTO() {
	}

	public RoleDTO(Role role) {
		this.id = role.getId();
		this.roleName = role.getRoleName();
	}

	public RoleDTO(Integer id, String roleName) {
		this.id = id;
		this.roleName = roleName;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
}
