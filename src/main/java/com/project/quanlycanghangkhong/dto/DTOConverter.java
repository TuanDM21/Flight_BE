package com.project.quanlycanghangkhong.dto;

import com.project.quanlycanghangkhong.model.Team;
import com.project.quanlycanghangkhong.model.Unit;
import com.project.quanlycanghangkhong.model.User;
import com.project.quanlycanghangkhong.model.UserShift;

public class DTOConverter {

    public static TeamDTO convertTeam(Team team) {
        if (team == null) return null;
        return new TeamDTO(team.getId(), team.getTeamName());
    }
    
    public static UnitDTO convertUnit(Unit unit) {
        if (unit == null) return null;
        Integer teamId = (unit.getTeam() != null) ? unit.getTeam().getId() : null;
        return new UnitDTO(unit.getId(), unit.getUnitName(), teamId);
    }
    
    public static UserDTO convertUser(User user) {
        if (user == null) return null;
        Integer roleId = (user.getRole() != null) ? user.getRole().getId() : null;
        Integer teamId = (user.getTeam() != null) ? user.getTeam().getId() : null;
        Integer unitId = (user.getUnit() != null) ? user.getUnit().getId() : null;
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), roleId, teamId, unitId);
    }
    
    public static UserShiftDTO convertUserShift(UserShift us) {
        if (us == null) return null;
        Integer shiftId = (us.getShift() != null) ? us.getShift().getId() : null;
        return new UserShiftDTO(
            us.getId(),
            convertUser(us.getUser()),
            us.getShiftDate(),
            shiftId,
            us.getCreatedAt(),
            us.getUpdatedAt()
        );
    }
}
