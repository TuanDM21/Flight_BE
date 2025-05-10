package com.project.quanlycanghangkhong.service;

import java.time.LocalDate;
import java.util.List;

import com.project.quanlycanghangkhong.dto.ApplyFlightShiftRequest;
import com.project.quanlycanghangkhong.dto.UserFlightShiftResponseDTO;
import com.project.quanlycanghangkhong.dto.UserFlightShiftResponseSearchDTO;
import com.project.quanlycanghangkhong.model.UserFlightShift;

public interface UserFlightShiftService {
    void applyFlightShift(ApplyFlightShiftRequest request);

    List<UserFlightShift> getShiftsByDate(LocalDate shiftDate);

    List<UserFlightShift> getShiftsByUser(Integer userId);

    // --- Thêm: Lấy ca trực theo flightId và shiftDate ---
    List<UserFlightShiftResponseDTO> getShiftsByFlightAndDate(Long flightId, LocalDate shiftDate);

    // Lấy danh sách userId phục vụ chuyến bay theo flightId và shiftDate
    List<Integer> getUserIdsByFlightAndDate(Long flightId, LocalDate shiftDate);

    void removeFlightAssignment(Long flightId, LocalDate shiftDate, Integer userId);

    boolean isUserAssignedToFlight(LocalDate shiftDate, Integer userId);

    List<UserFlightShiftResponseSearchDTO> getFlightSchedulesByCriteria(LocalDate shiftDate, Integer teamId,
            Integer unitId, Long flightId);

    List<UserFlightShiftResponseDTO> getAvailableShifts(Long flightId, LocalDate date);

    List<UserFlightShiftResponseDTO> getAssignedShifts(Long flightId, LocalDate date);

    // Bổ sung các hàm trả về DTO cho controller mới
    List<UserFlightShiftResponseDTO> getAllUserFlightShifts();
    List<UserFlightShiftResponseDTO> getShiftsByDateDTO(LocalDate date);
    List<UserFlightShiftResponseDTO> getShiftsByUserDTO(Integer userId);

}
