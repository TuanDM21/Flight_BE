package com.project.quanlycanghangkhong.service.impl;

import com.project.quanlycanghangkhong.dto.ShiftDTO;
import com.project.quanlycanghangkhong.model.Shift;
import com.project.quanlycanghangkhong.repository.ShiftRepository;
import com.project.quanlycanghangkhong.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import com.project.quanlycanghangkhong.repository.UserRepository;

@Service
public class ShiftServiceImpl implements ShiftService {
    
    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Shift createShift(Shift shift) {
        return shiftRepository.save(shift);
    }

    @Override
    public Optional<Shift> getShiftById(Integer id) {
        return shiftRepository.findById(id);
    }

    @Override
    public List<Shift> getAllShifts() {
        return shiftRepository.findAll();
    }

    @Override
    public Shift updateShift(Integer id, Shift shiftData) {
        return shiftRepository.findById(id).map(shift -> {
            shift.setShiftCode(shiftData.getShiftCode());
            shift.setStartTime(shiftData.getStartTime());
            shift.setEndTime(shiftData.getEndTime());
            shift.setLocation(shiftData.getLocation());
            shift.setDescription(shiftData.getDescription());
            shift.setTeam(shiftData.getTeam()); // cập nhật team nếu cần
            return shiftRepository.save(shift);
        }).orElse(null);
    }

    @Override
    public void deleteShift(Integer id) {
        shiftRepository.deleteById(id);
    }
    
    @Override
    public Optional<Shift> findByShiftCode(String shiftCode) {
        return shiftRepository.findByShiftCode(shiftCode);
    }

    @Override
    public List<Shift> getShiftsByTeamId(Integer teamId) {
        return shiftRepository.findByTeamId(teamId);
    }

    @Override
    public ShiftDTO toDTO(Shift shift) {
        if (shift == null) return null;
        return new ShiftDTO(
            shift.getId(),
            shift.getShiftCode(),
            shift.getStartTime(),
            shift.getEndTime(),
            shift.getLocation(),
            shift.getDescription(),
            shift.getTeam() != null ? shift.getTeam().getId() : null,
            shift.getTeam() != null ? shift.getTeam().getTeamName() : null
        );
    }

    @Override
    public List<ShiftDTO> getAllShiftsForCurrentUser() {
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        com.project.quanlycanghangkhong.model.User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return List.of();
        String role = user.getRole() != null ? user.getRole().getRoleName() : "";
        if (role.equals("ADMIN") || role.equals("DIRECTOR") || role.equals("VICE_DIRECTOR")) {
            return shiftRepository.findAll().stream().map(this::toDTO).toList();
        } else if (user.getTeam() != null) {
            return shiftRepository.findByTeamId(user.getTeam().getId()).stream().map(this::toDTO).toList();
        } else {
            return List.of();
        }
    }

    @Override
    public List<ShiftDTO> getShiftsByTeamIdDTO(Integer teamId) {
        return shiftRepository.findByTeamId(teamId).stream().map(this::toDTO).toList();
    }
}
