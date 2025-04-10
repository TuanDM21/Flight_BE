package com.project.quanlycanghangkhong.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.quanlycanghangkhong.model.UserShift;

public interface UserShiftRepository extends JpaRepository<UserShift, Integer> {
	// Nếu cần custom query, ví dụ tìm theo userId & shiftDate, có thể thêm:
	// Optional<UserShift> findByUserIdAndShiftDate(Integer userId, LocalDate
	// shiftDate);
	List<UserShift> findByUserIdAndShiftDate(Integer userId, LocalDate shiftDate);

}

