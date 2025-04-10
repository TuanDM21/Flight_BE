package com.project.quanlycanghangkhong.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.quanlycanghangkhong.model.Airport;

public interface AirportRepository extends JpaRepository<Airport, Long> {
    // Thêm các phương thức truy vấn nếu cần, ví dụ: tìm theo mã sân bay
    Airport findByAirportCode(String airportCode);
}
