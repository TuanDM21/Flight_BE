package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.model.Shift;
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
}
