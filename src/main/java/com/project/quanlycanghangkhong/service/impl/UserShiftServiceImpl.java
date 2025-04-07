package com.project.quanlycanghangkhong.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.quanlycanghangkhong.dto.ApplyShiftMultiDTO;
import com.project.quanlycanghangkhong.dto.DTOConverter;
import com.project.quanlycanghangkhong.dto.ScheduleDTO;
import com.project.quanlycanghangkhong.dto.UserShiftDTO;
import com.project.quanlycanghangkhong.model.Shift;
import com.project.quanlycanghangkhong.model.User;
import com.project.quanlycanghangkhong.model.UserShift;
import com.project.quanlycanghangkhong.repository.ScheduleDAO;
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
    
    @Override
    public UserShiftDTO assignShiftToUser(Integer userId, LocalDate date, Integer shiftId) {
        // Lấy thông tin user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Lấy thông tin shift
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new RuntimeException("Shift not found"));
        // Tạo mới UserShift
        UserShift userShift = new UserShift(user, date, shift);
        return DTOConverter.convertUserShift(userShiftRepository.save(userShift));
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
    public UserShiftDTO updateUserShift(Integer id, Integer shiftId) {
        Optional<UserShift> opt = userShiftRepository.findById(id);
        if(opt.isPresent()){
            UserShift userShift = opt.get();
            Shift shift = shiftRepository.findById(shiftId)
                    .orElseThrow(() -> new RuntimeException("Shift not found"));
            userShift.setShift(shift);
            return DTOConverter.convertUserShift(userShiftRepository.save(userShift));
        }
        return null;
    }

    @Override
    public void deleteUserShift(Integer id) {
        userShiftRepository.deleteById(id);
    }

    @Override
    public List<ScheduleDTO> getSchedulesByCriteria(LocalDate shiftDate, Integer teamId, Integer unitId) {
        try {
            // Nếu shiftDate là null, trả về tất cả lịch trực, hoặc bạn có thể thêm logic khác.
            return scheduleDAO.getSchedulesByDateTeamUnit(shiftDate, teamId, unitId);
        } catch (Exception ex) {
            throw new RuntimeException("Error fetching schedules", ex);
        }
    }

    @Override
    public List<UserShiftDTO> applyShiftToUsers(ApplyShiftMultiDTO dto) {
        List<UserShiftDTO> createdDTOs = new ArrayList<>();
        LocalDate shiftDate = dto.getShiftDate();

        // Tra cứu shift entity bằng shiftCode
        Shift shiftEntity = null;
        if (dto.getShiftCode() != null && !dto.getShiftCode().trim().isEmpty()) {
            shiftEntity = shiftRepository.findByShiftCode(dto.getShiftCode())
                .orElseThrow(() -> new RuntimeException("Shift not found with code = " + dto.getShiftCode()));
        }

        for (Integer userId : dto.getUserIds()) {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with id " + userId + " not found"));

            // Kiểm tra xem userShift đã tồn tại cho user + date chưa? 
            // (nếu có unique constraint user-date => check, update)
            UserShift userShift = new UserShift();
            userShift.setUser(user);
            userShift.setShiftDate(shiftDate);
            userShift.setShift(shiftEntity); // foreign key shift_id
            userShiftRepository.save(userShift);

            // Chuyển sang DTO
            createdDTOs.add(DTOConverter.convertUserShift(userShift));
        }
        return createdDTOs;
    }
}
