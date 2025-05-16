package com.project.quanlycanghangkhong.dao;

import java.time.LocalDate;
import java.util.List;
import com.project.quanlycanghangkhong.dto.ScheduleDTO;

public interface ScheduleDAO {
    List<ScheduleDTO> getSchedulesByUserAndDateRange(Integer userId, LocalDate startDate, LocalDate endDate);
}