package com.project.quanlycanghangkhong.service;

import java.util.List;

import com.project.quanlycanghangkhong.dto.UnitDTO;
import com.project.quanlycanghangkhong.model.Unit;

public interface UnitService {
    List<UnitDTO> getUnitsByTeam(Integer teamId);

    Unit getUnitById(Integer id);

    Unit createUnit(Unit unit);

    void deleteUnit(Integer id);

    List<UnitDTO> getAllUnits();
    
    /**
     * Lấy danh sách unit mà user hiện tại có thể giao việc cho (theo phân quyền).
     */
    List<UnitDTO> getAssignableUnitsForCurrentUser();

}
