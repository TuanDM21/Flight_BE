package com.project.quanlycanghangkhong.dto.response.auth;

import com.project.quanlycanghangkhong.dto.RoleDTO;
import com.project.quanlycanghangkhong.dto.TeamDTO;
import com.project.quanlycanghangkhong.dto.UnitDTO;
import lombok.Data;

@Data
public class RegisterResponse {
	private Integer id;
	private String name;
	private String email;
	private RoleDTO role;
	private TeamDTO team;
	private UnitDTO unit;
}
