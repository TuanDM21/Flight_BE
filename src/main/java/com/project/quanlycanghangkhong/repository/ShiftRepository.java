package com.project.quanlycanghangkhong.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.quanlycanghangkhong.model.Shift;

public interface ShiftRepository extends JpaRepository<Shift, Integer> {
    // Nếu cần thêm phương thức custom query, khai báo tại đây
    Optional<Shift> findByShiftCode(String shiftCode);
    List<Shift> findByTeamId(Integer teamId);
}

