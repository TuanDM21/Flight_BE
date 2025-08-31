package com.project.quanlycanghangkhong.dto;

import com.project.quanlycanghangkhong.dto.RoleDTO;
import com.project.quanlycanghangkhong.dto.TeamDTO;
import com.project.quanlycanghangkhong.dto.UnitDTO;
import lombok.Data;

@Data
public class RegisterDTO {
	private Integer id;
	private String name;
	private String email;
	private RoleDTO role;
	private TeamDTO team;
	private UnitDTO unit;
}
