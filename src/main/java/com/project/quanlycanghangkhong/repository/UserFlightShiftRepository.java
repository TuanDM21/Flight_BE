package com.project.quanlycanghangkhong.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.quanlycanghangkhong.model.Flight;
import com.project.quanlycanghangkhong.model.User;
import com.project.quanlycanghangkhong.model.UserFlightShift;

public interface UserFlightShiftRepository extends JpaRepository<UserFlightShift, Long> {
    // Thêm các query tùy theo nghiệp vụ nếu cần
    Optional<UserFlightShift> findByUserAndFlightAndShiftDate(User user, Flight flight, LocalDate shiftDate);

}
