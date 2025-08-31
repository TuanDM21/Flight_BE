package com.project.quanlycanghangkhong.dto.response.users;

import lombok.Builder;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
public class MeResponse {
	@Schema(description = "ID of the user", required = true)
	private Integer id;

	@Schema(description = "Name of the user", required = true)
	private String name;

	@Schema(description = "Email of the user", required = true)
	private String email;

	@Schema(description = "Role name of the user", required = true)
	private String roleName;

	@Schema(description = "Team name of the user", required = true)
	private String teamName;

	@Schema(description = "Unit name of the user", required = true)
	private String unitName;

	@Schema(description = "Role ID of the user", required = true)
	private Integer roleId;

	@Schema(description = "Team ID of the user", required = true)
	private Integer teamId;

	@Schema(description = "Unit ID of the user", required = true)
	private Integer unitId;
}
