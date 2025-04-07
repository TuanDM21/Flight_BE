package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.dto.ApplyShiftMultiDTO;
import com.project.quanlycanghangkhong.dto.ScheduleDTO;
import com.project.quanlycanghangkhong.dto.UserShiftDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserShiftService {
    UserShiftDTO assignShiftToUser(Integer userId, LocalDate date, Integer shiftId);
    Optional<UserShiftDTO> getUserShiftById(Integer id);
    List<UserShiftDTO> getAllUserShifts();
    UserShiftDTO updateUserShift(Integer id, Integer shiftId);
    void deleteUserShift(Integer id);
    List<ScheduleDTO> getSchedulesByCriteria(LocalDate shiftDate, Integer teamId, Integer unitId);
    List<UserShiftDTO> applyShiftToUsers(ApplyShiftMultiDTO dto);
}
