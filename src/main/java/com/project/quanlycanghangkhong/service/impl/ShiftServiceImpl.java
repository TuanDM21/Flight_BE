package com.project.quanlycanghangkhong.service.impl;

import com.project.quanlycanghangkhong.model.Shift;
import com.project.quanlycanghangkhong.repository.ShiftRepository;
import com.project.quanlycanghangkhong.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShiftServiceImpl implements ShiftService {
    
    @Autowired
    private ShiftRepository shiftRepository;

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
}
