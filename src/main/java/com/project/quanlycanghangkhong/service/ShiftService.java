package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.model.Shift;
import com.project.quanlycanghangkhong.dto.ShiftDTO;
import java.util.List;
import java.util.Optional;

public interface ShiftService {
    Shift createShift(Shift shift);
    Optional<Shift> getShiftById(Integer id);
    List<Shift> getAllShifts();
    Shift updateShift(Integer id, Shift shiftData);
    void deleteShift(Integer id);
    // Phương thức kiểm tra tồn tại mã ca trực
    Optional<Shift> findByShiftCode(String shiftCode);
    List<Shift> getShiftsByTeamId(Integer teamId);
    List<ShiftDTO> getAllShiftsForCurrentUser();
    List<ShiftDTO> getShiftsByTeamIdDTO(Integer teamId);
    ShiftDTO toDTO(com.project.quanlycanghangkhong.model.Shift shift);
}
