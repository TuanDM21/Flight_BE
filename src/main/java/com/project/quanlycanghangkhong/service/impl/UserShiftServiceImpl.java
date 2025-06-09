package com.project.quanlycanghangkhong.service.impl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.quanlycanghangkhong.dto.ApplyShiftMultiDTO;
import com.project.quanlycanghangkhong.dto.AssignShiftRequest;
import com.project.quanlycanghangkhong.dto.DTOConverter;
import com.project.quanlycanghangkhong.dto.ScheduleDTO;
import com.project.quanlycanghangkhong.dto.UserShiftDTO;
import com.project.quanlycanghangkhong.model.Shift;
import com.project.quanlycanghangkhong.model.User;
import com.project.quanlycanghangkhong.model.UserShift;
import com.project.quanlycanghangkhong.dao.ScheduleDAO;
import com.project.quanlycanghangkhong.repository.ScheduleRepository;
import com.project.quanlycanghangkhong.repository.ShiftRepository;
import com.project.quanlycanghangkhong.repository.UserRepository;
import com.project.quanlycanghangkhong.repository.UserShiftRepository;
import com.project.quanlycanghangkhong.service.UserShiftService;

@Service
public class UserShiftServiceImpl implements UserShiftService {
    
    @Autowired
    private UserShiftRepository userShiftRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ShiftRepository shiftRepository;
    
    @Autowired
    private ScheduleDAO scheduleDAO;

    @Autowired
    private ScheduleRepository scheduleRepository;

    
    @Override
    public UserShiftDTO assignShiftToUser(Integer userId, LocalDate date, Integer shiftId) {
        // Lấy user và shift
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new RuntimeException("Shift not found"));
        // Kiểm tra conflict: tìm ca trực của user trong ngày date
        List<UserShift> existingShifts = userShiftRepository.findByUserIdAndShiftDate(userId, date);
        if (!existingShifts.isEmpty()) {
            throw new RuntimeException("Nhân viên này đã có ca trực trong ngày " + date);
        }
        UserShift userShift = new UserShift(user, date, shift);
        UserShift saved = userShiftRepository.save(userShift);
        return new UserShiftDTO(saved);
    }

    @Override
    public Optional<UserShiftDTO> getUserShiftById(Integer id) {
        return userShiftRepository.findById(id)
                .map(DTOConverter::convertUserShift);
    }

    @Override
    public List<UserShiftDTO> getAllUserShifts() {
        return userShiftRepository.findAll().stream()
                .map(DTOConverter::convertUserShift)
                .collect(Collectors.toList());
    }
    @Override
    public UserShiftDTO updateUserShift(Integer id, Integer newShiftId, LocalDate newShiftDate) {
        // Lấy UserShift cần update
        UserShift userShift = userShiftRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UserShift not found with id: " + id));

        // Kiểm tra conflict: nếu người dùng đã có ca trực khác trong cùng ngày (loại trừ bản ghi hiện tại)
        List<UserShift> existingAssignments = userShiftRepository.findByUserIdAndShiftDate(
                userShift.getUser().getId(), userShift.getShiftDate());
        existingAssignments.removeIf(us -> us.getId().equals(id));
        if (!existingAssignments.isEmpty()) {
            throw new RuntimeException("Nhân viên này đã có ca trực trong ngày " + userShift.getShiftDate());
        }

        // Lấy đối tượng Shift mới
        Shift newShift = shiftRepository.findById(newShiftId)
                .orElseThrow(() -> new RuntimeException("Shift not found with id: " + newShiftId));

        // Cập nhật ca và ngày trực
        userShift.setShift(newShift);
        userShift.setShiftDate(newShiftDate);

        // Lưu và trả về DTO
        UserShift updated = userShiftRepository.save(userShift);
        return new UserShiftDTO(updated);
    }

    @Override
    public void deleteUserShift(Integer id) {
        userShiftRepository.deleteById(id);
    }

    @Override
    public List<ScheduleDTO> getSchedulesByCriteria(LocalDate shiftDate, Integer teamId, Integer unitId) {
        try {
            // Nếu shiftDate là null, trả về tất cả lịch trực, hoặc bạn có thể thêm logic khác.
            return scheduleRepository.getSchedulesByDateTeamUnit(shiftDate, teamId, unitId);
        } catch (Exception ex) {
            throw new RuntimeException("Error fetching schedules", ex);
        }
    }

    @Override
    public List<UserShiftDTO> applyShiftToUsers(ApplyShiftMultiDTO dto) {
        List<UserShiftDTO> createdDTOs = new ArrayList<>();
        LocalDate shiftDate = dto.getShiftDate();
        // Tìm shift dựa trên shiftCode
        Shift shift = shiftRepository.findByShiftCode(dto.getShiftCode())
                .orElseThrow(() -> new RuntimeException("Shift not found"));
        for (Integer userId : dto.getUserIds()) {
            // Kiểm tra conflict
            List<UserShift> conflicts = userShiftRepository.findByUserIdAndShiftDate(userId, shiftDate);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userId));
            if (!conflicts.isEmpty()) {
                throw new RuntimeException("Nhân viên với tên " + user.getName() + " đã có ca trực trong ngày " + shiftDate);
            }
            UserShift newShift = new UserShift(user, shiftDate, shift);
            UserShift saved = userShiftRepository.save(newShift);
            createdDTOs.add(new UserShiftDTO(saved));
        }
        return createdDTOs;
    }

    @Override
    public List<Integer> getUserIdsOnDutyAtTime(LocalDate date, LocalTime actualTime) {
        List<UserShift> shifts = userShiftRepository.findByShiftDate(date);
        return shifts.stream()
                .filter(shift -> {
                    LocalTime start = shift.getShift().getStartTime();
                    LocalTime end = shift.getShift().getEndTime();
                    if (end.isBefore(start)) {
                        // ca qua đêm
                        return (!actualTime.isBefore(start) || !actualTime.isAfter(end));
                    } else {
                        return !actualTime.isBefore(start) && !actualTime.isAfter(end);
                    }
                })
                .map(shift -> shift.getUser().getId())
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleDTO> getSchedulesByUserAndDateRange(Integer userId, LocalDate startDate, LocalDate endDate) {
        // Gọi DAO hoặc repository để lấy danh sách lịch trực theo userId và khoảng ngày
        List<ScheduleDTO> result = scheduleDAO.getSchedulesByUserAndDateRange(userId, startDate, endDate);
        System.out.println("[LOG] getSchedulesByUserAndDateRange result: " + result);
        return result;
    }

    @Override
    @Transactional
    public List<UserShiftDTO> saveUserShiftsBatch(List<AssignShiftRequest> userShifts) {
        List<UserShiftDTO> savedShifts = new ArrayList<>();
        
        for (AssignShiftRequest request : userShifts) {
            LocalDate shiftDate = LocalDate.parse(request.getShiftDate()); // Convert String to LocalDate
            
            // Kiểm tra user tồn tại
            User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));
            
            // Kiểm tra shift tồn tại (có thể null)
            Shift shift = null;
            if (request.getShiftId() != null) {
                shift = shiftRepository.findById(request.getShiftId())
                    .orElseThrow(() -> new RuntimeException("Shift not found with id: " + request.getShiftId()));
            }
            
            // Kiểm tra conflict: user đã có ca trực trong ngày này chưa
            List<UserShift> existingShifts = userShiftRepository.findByUserIdAndShiftDate(
                request.getUserId(), shiftDate);
            if (!existingShifts.isEmpty()) {
                throw new RuntimeException("Nhân viên " + user.getName() + " đã có ca trực trong ngày " + shiftDate);
            }
            
            // Tạo và lưu UserShift
            UserShift userShift = new UserShift(user, shiftDate, shift);
            UserShift saved = userShiftRepository.save(userShift);
            savedShifts.add(new UserShiftDTO(saved));
        }
        
        return savedShifts;
    }
}
