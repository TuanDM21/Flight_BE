package com.project.quanlycanghangkhong.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.quanlycanghangkhong.model.Flight;

public interface FlightRepository extends JpaRepository<Flight, Long> {
    // Thêm các phương thức truy vấn nếu cần
}
