package com.project.quanlycanghangkhong.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import com.project.quanlycanghangkhong.dto.DTOConverter;
import com.project.quanlycanghangkhong.dto.UnitDTO;
import com.project.quanlycanghangkhong.model.Unit;
import com.project.quanlycanghangkhong.model.User;
import com.project.quanlycanghangkhong.repository.UnitRepository;
import com.project.quanlycanghangkhong.repository.UserRepository;
import com.project.quanlycanghangkhong.service.UnitService;

@Service
public class UnitServiceImpl implements UnitService {
    private final UnitRepository unitRepository;
    private final UserRepository userRepository;

    public UnitServiceImpl(UnitRepository unitRepository, UserRepository userRepository) {
        this.unitRepository = unitRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<UnitDTO> getUnitsByTeam(Integer teamId) {
        List<Unit> units = unitRepository.findByTeam_Id(teamId);
        // Hoặc custom query: findAllByTeamId(teamId)

        return units.stream()
                .map(DTOConverter::convertUnit)
                .collect(Collectors.toList());
    }

    @Override
    public Unit getUnitById(Integer id) {
        return unitRepository.findById(id).orElseThrow(() -> new RuntimeException("Unit not found"));
    }

    @Override
    public Unit createUnit(Unit unit) {
        // ✅ ADMIN ONLY: Chỉ ADMIN mới có thể tạo unit
        if (!isAdmin()) {
            throw new RuntimeException("Access denied: Only ADMIN can create units");
        }
        
        return unitRepository.save(unit);
    }

    @Override
    public void deleteUnit(Integer id) {
        // ✅ ADMIN ONLY: Chỉ ADMIN mới có thể xóa unit
        if (!isAdmin()) {
            throw new RuntimeException("Access denied: Only ADMIN can delete units");
        }
        
        unitRepository.deleteById(id);
    }

    @Override
    public List<UnitDTO> getAllUnits() {
        // ✅ ADMIN ONLY: Chỉ ADMIN mới có thể xem tất cả units
        if (!isAdmin()) {
            throw new RuntimeException("Access denied: Only ADMIN can get all units");
        }
        
        List<Unit> units = unitRepository.findAll();
        return units.stream()
                .map(DTOConverter::convertUnit)
                .collect(Collectors.toList());
    }

    @Override
    public List<UnitDTO> getAssignableUnitsForCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);
        if (currentUser == null || currentUser.getRole() == null) return List.of();
        
        String role = currentUser.getRole().getRoleName();
        List<Unit> assignableUnits;
        
        switch (role) {
            case "ADMIN":
                // ADMIN có thể giao cho tất cả units (full quyền)
                assignableUnits = unitRepository.findAll();
                break;
            case "DIRECTOR":
            case "VICE_DIRECTOR":
                // Có thể giao cho tất cả units
                assignableUnits = unitRepository.findAll();
                break;
            case "TEAM_LEAD":
            case "TEAM_VICE_LEAD":
                // Có thể giao cho tất cả units trong team của mình
                if (currentUser.getTeam() != null) {
                    assignableUnits = unitRepository.findByTeam_Id(currentUser.getTeam().getId());
                } else {
                    assignableUnits = List.of();
                }
                break;
            case "UNIT_LEAD":
                // Chỉ có thể giao cho unit của mình
                if (currentUser.getUnit() != null) {
                    assignableUnits = List.of(currentUser.getUnit());
                } else {
                    assignableUnits = List.of();
                }
                break;
            case "UNIT_VICE_LEAD":
                // UNIT_VICE_LEAD không thể giao việc cho unit, chỉ có thể giao cho users
                assignableUnits = List.of();
                break;
            case "MEMBER":
            case "OFFICE":
                // Chỉ có thể giao cho unit của mình
                if (currentUser.getUnit() != null) {
                    assignableUnits = List.of(currentUser.getUnit());
                } else {
                    assignableUnits = List.of();
                }
                break;
            default:
                assignableUnits = List.of();
        }
        
        return assignableUnits.stream()
                .map(DTOConverter::convertUnit)
                .collect(Collectors.toList());
    }

    /**
     * Helper method để check xem user hiện tại có phải ADMIN không
     */
    private boolean isAdmin() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);
        return currentUser != null && 
               currentUser.getRole() != null && 
               "ADMIN".equals(currentUser.getRole().getRoleName());
    }
}
