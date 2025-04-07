package com.project.quanlycanghangkhong.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.project.quanlycanghangkhong.dto.DTOConverter;
import com.project.quanlycanghangkhong.dto.UnitDTO;
import com.project.quanlycanghangkhong.model.Unit;
import com.project.quanlycanghangkhong.repository.UnitRepository;
import com.project.quanlycanghangkhong.service.UnitService;

@Service
public class UnitServiceImpl implements UnitService {
    private final UnitRepository unitRepository;

    public UnitServiceImpl(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
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
    public Unit getUnitById(Long id) {
        return unitRepository.findById(id).orElseThrow(() -> new RuntimeException("Unit not found"));
    }

    @Override
    public Unit createUnit(Unit unit) {
        return unitRepository.save(unit);
    }

    @Override
    public void deleteUnit(Long id) {
        unitRepository.deleteById(id);
    }

    @Override
    public List<UnitDTO> getAllUnits() {
        List<Unit> units = unitRepository.findAll();
        return units.stream()
                    .map(DTOConverter::convertUnit)
                    .collect(Collectors.toList());
    }
}
