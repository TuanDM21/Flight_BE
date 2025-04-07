package com.project.quanlycanghangkhong.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import com.project.quanlycanghangkhong.dto.ScheduleDTO;

@Repository
public class ScheduleDAO {

    private DataSource dataSource;

    public ScheduleDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<ScheduleDTO> getSchedulesByDateTeamUnit(LocalDate shiftDate, Integer teamId, Integer unitId) throws SQLException {
        List<ScheduleDTO> schedules = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT us.id AS schedule_id, us.shift_date, ")
           .append("u.id AS user_id, u.name AS user_name, ")
           .append("t.id AS team_id, t.team_name, ")
           .append("un.id AS unit_id, un.unit_name, ")
           .append("s.id AS shift_id, s.shift_code, s.start_time, s.end_time, s.location, s.description ")
           .append("FROM user_shifts us ")
           .append("JOIN users u ON us.user_id = u.id ")
           .append("LEFT JOIN teams t ON u.team_id = t.id ")
           .append("LEFT JOIN units un ON u.unit_id = un.id ")
           .append("LEFT JOIN shifts s ON us.shift_id = s.id ")
           .append("WHERE us.shift_date = ? ");
        
        if (teamId != null) {
            sql.append("AND t.id = ? ");
        }
        if (unitId != null) {
            sql.append("AND un.id = ? ");
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            ps.setDate(1, Date.valueOf(shiftDate));
            int paramIndex = 2;
            if (teamId != null) {
                ps.setInt(paramIndex++, teamId);
            }
            if (unitId != null) {
                ps.setInt(paramIndex++, unitId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ScheduleDTO dto = new ScheduleDTO();
                    dto.setScheduleId(rs.getInt("schedule_id"));
                    dto.setShiftDate(rs.getDate("shift_date").toLocalDate());
                    dto.setUserId(rs.getInt("user_id"));
                    dto.setUserName(rs.getString("user_name"));

                    int tid = rs.getInt("team_id");
                    if (!rs.wasNull()) {
                        dto.setTeamId(tid);
                        dto.setTeamName(rs.getString("team_name"));
                    }
                    
                    int uid = rs.getInt("unit_id");
                    if (!rs.wasNull()) {
                        dto.setUnitId(uid);
                        dto.setUnitName(rs.getString("unit_name"));
                    }
                    
                    int sid = rs.getInt("shift_id");
                    if (!rs.wasNull()) {
                        dto.setShiftId(sid);
                        dto.setShiftCode(rs.getString("shift_code"));
                        dto.setStartTime(rs.getTime("start_time"));
                        dto.setEndTime(rs.getTime("end_time"));
                        dto.setLocation(rs.getString("location"));
                        dto.setDescription(rs.getString("description"));
                    }
                    schedules.add(dto);
                }
            }
        }
        return schedules;
    }
}
