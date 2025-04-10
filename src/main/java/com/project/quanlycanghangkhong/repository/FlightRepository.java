package com.project.quanlycanghangkhong.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.quanlycanghangkhong.model.Flight;

public interface FlightRepository extends JpaRepository<Flight, Long> {
	// Thêm các phương thức truy vấn nếu cần
	List<Flight> findByFlightNumberContaining(String keyword);

	// Thêm phương thức mới: tìm các chuyến bay có flightDate bằng tham số truyền
	// vào
	 @Query("SELECT f FROM Flight f " +
	           "WHERE f.flightDate = :today " +
	           "   OR (f.flightDate = :yesterday AND f.actualDepartureTimeAtArrival IS NULL)")
	    List<Flight> findFlightsForServiceDay(@Param("today") LocalDate today,
	                                           @Param("yesterday") LocalDate yesterday);

	List<Flight> findByFlightDate(LocalDate flightDate);

	// Phương thức tìm kiếm theo ngày và chứa keyword trong flightNumber (nếu cần)
	List<Flight> findByFlightDateAndFlightNumberContainingIgnoreCase(LocalDate flightDate, String flightNumber);
}
