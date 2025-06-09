package com.project.quanlycanghangkhong.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.project.quanlycanghangkhong.dto.ApplyShiftMultiDTO;
import com.project.quanlycanghangkhong.dto.AssignShiftRequest;
import com.project.quanlycanghangkhong.dto.ScheduleDTO;
import com.project.quanlycanghangkhong.dto.UserShiftDTO;

public interface UserShiftService {
    UserShiftDTO assignShiftToUser(Integer userId, LocalDate date, Integer shiftId);

    Optional<UserShiftDTO> getUserShiftById(Integer id);

    List<UserShiftDTO> getAllUserShifts();

    UserShiftDTO updateUserShift(Integer id, Integer shiftId, LocalDate newShiftDate);

    void deleteUserShift(Integer id);

    List<ScheduleDTO> getSchedulesByCriteria(LocalDate shiftDate, Integer teamId, Integer unitId);

    List<UserShiftDTO> applyShiftToUsers(ApplyShiftMultiDTO dto);

    // Lấy danh sách userId trực chung theo ngày và actualTime
    List<Integer> getUserIdsOnDutyAtTime(LocalDate date, java.time.LocalTime actualTime);

    List<ScheduleDTO> getSchedulesByUserAndDateRange(Integer userId, LocalDate startDate, LocalDate endDate);

    // Lưu nhiều ca trực cùng lúc
    List<UserShiftDTO> saveUserShiftsBatch(List<AssignShiftRequest> userShifts);
}
