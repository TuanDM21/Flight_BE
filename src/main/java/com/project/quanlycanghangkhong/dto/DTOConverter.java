package com.project.quanlycanghangkhong.dto;

import com.project.quanlycanghangkhong.model.Team;
import com.project.quanlycanghangkhong.model.Unit;
import com.project.quanlycanghangkhong.model.User;
import com.project.quanlycanghangkhong.model.UserShift;
import com.project.quanlycanghangkhong.model.Attachment;
import java.util.stream.Collectors;
import java.util.List;

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
        UserDTO dto = new UserDTO(user); // Sử dụng constructor đầy đủ, sẽ lấy cả roleName, teamName, unitName
        
        // Lấy permissions từ UserPermission
        if (user.getUserPermissions() != null) {
            List<String> permCodes = user.getUserPermissions().stream()
                .filter(perm -> perm.getValue() != null && perm.getValue())
                .map(perm -> perm.getPermissionCode())
                .collect(Collectors.toList());
            
            dto.setPermissions(permCodes);
        } else {
            // Nếu không có userPermissions, khởi tạo permissions rỗng
            dto.setPermissions(new java.util.ArrayList<>());
        }
        
        return dto;
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

    public static AttachmentDTO convertAttachment(Attachment att) {
        if (att == null) return null;
        AttachmentDTO dto = new AttachmentDTO();
        dto.setId(att.getId());
        dto.setFilePath(att.getFilePath());
        dto.setFileName(att.getFileName());
        dto.setFileSize(att.getFileSize());
        dto.setCreatedAt(att.getCreatedAt());
        
        // Map owner information
        if (att.getUploadedBy() != null) {
            UserDTO ownerDto = new UserDTO();
            ownerDto.setId(att.getUploadedBy().getId());
            ownerDto.setName(att.getUploadedBy().getName());
            ownerDto.setEmail(att.getUploadedBy().getEmail());
            dto.setUploadedBy(ownerDto);
        }
        
        return dto;
    }
}
