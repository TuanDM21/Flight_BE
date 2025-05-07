package com.project.quanlycanghangkhong.dto;

import com.project.quanlycanghangkhong.model.Team;
import com.project.quanlycanghangkhong.model.Unit;
import com.project.quanlycanghangkhong.model.User;
import com.project.quanlycanghangkhong.model.UserShift;
import com.project.quanlycanghangkhong.model.Document;
import com.project.quanlycanghangkhong.model.Attachment;
import java.util.stream.Collectors;

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

    public static DocumentDTO convertDocument(Document doc) {
        if (doc == null) return null;
        DocumentDTO dto = new DocumentDTO();
        dto.setId(doc.getId());
        dto.setDocumentType(doc.getDocumentType());
        dto.setContent(doc.getContent());
        dto.setNotes(doc.getNotes());
        dto.setCreatedAt(doc.getCreatedAt());
        dto.setUpdatedAt(doc.getUpdatedAt());
        if (doc.getAttachments() != null) {
            dto.setAttachments(doc.getAttachments().stream()
                .map(DTOConverter::convertAttachment)
                .collect(Collectors.toList()));
        }
        return dto;
    }

    public static AttachmentDTO convertAttachment(Attachment att) {
        if (att == null) return null;
        AttachmentDTO dto = new AttachmentDTO();
        dto.setId(att.getId());
        dto.setFilePath(att.getFilePath());
        dto.setFileName(att.getFileName());
        dto.setFileSize(att.getFileSize());
        dto.setCreatedAt(att.getCreatedAt());
        return dto;
    }
}
