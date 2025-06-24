package com.project.quanlycanghangkhong.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.quanlycanghangkhong.dto.FlightDTO;
import com.project.quanlycanghangkhong.model.Flight;

public interface FlightRepository extends JpaRepository<Flight, Long> {
	// Thêm các phương thức truy vấn nếu cần
	List<Flight> findByFlightNumberContaining(String keyword);

	// Thêm phương thức mới: tìm các chuyến bay có flightDate bằng tham số truyền
	// vào
	@Query("SELECT new com.project.quanlycanghangkhong.dto.FlightDTO(" +
       "f.id, f.flightNumber, " +
       "d.airportCode, a.airportCode, " +
       "f.departureTime, f.arrivalTime, f.flightDate, " +
       "f.actualDepartureTime, f.actualArrivalTime, f.actualDepartureTimeAtArrival, " +
       "f.arrivalTimeatArrival, f.status, " +
       "f.createdAt, f.updatedAt, f.note, " +
       "f.airline, f.checkInCounters, f.gate) " +
       "FROM Flight f " +
       "LEFT JOIN f.departureAirport d " +
       "LEFT JOIN f.arrivalAirport a " +
       "WHERE f.flightDate = :today " +
       "   OR (f.flightDate = :yesterday AND f.actualDepartureTimeAtArrival IS NULL)")
	List<FlightDTO> findFlightsForServiceDay(@Param("today") LocalDate today,
                                          @Param("yesterday") LocalDate yesterday);


	List<Flight> findByFlightDate(LocalDate flightDate);

	// Phương thức tìm kiếm theo ngày và chứa keyword trong flightNumber (nếu cần)
	List<Flight> findByFlightDateAndFlightNumberContainingIgnoreCase(LocalDate flightDate, String flightNumber);

	// Phương thức tìm kiếm theo nhiều tiêu chí - FIX LOGIC
	@Query("SELECT f FROM Flight f " +
		   "LEFT JOIN f.departureAirport da " +
		   "LEFT JOIN f.arrivalAirport aa " +
		   "WHERE (:date IS NULL OR f.flightDate = :date) " +
		   "AND (:flightNumber IS NULL OR :flightNumber = '' OR TRIM(:flightNumber) = '' OR LOWER(f.flightNumber) LIKE LOWER(CONCAT('%', TRIM(:flightNumber), '%'))) " +
		   "AND (:departureAirport IS NULL OR :departureAirport = '' OR TRIM(:departureAirport) = '' OR " +
		   "     LOWER(da.airportCode) LIKE LOWER(CONCAT('%', TRIM(:departureAirport), '%')) OR " +
		   "     LOWER(da.airportName) LIKE LOWER(CONCAT('%', TRIM(:departureAirport), '%'))) " +
		   "AND (:arrivalAirport IS NULL OR :arrivalAirport = '' OR TRIM(:arrivalAirport) = '' OR " +
		   "     LOWER(aa.airportCode) LIKE LOWER(CONCAT('%', TRIM(:arrivalAirport), '%')) OR " +
		   "     LOWER(aa.airportName) LIKE LOWER(CONCAT('%', TRIM(:arrivalAirport), '%')))")
	List<Flight> findFlightsByCriteria(@Param("date") LocalDate date,
									   @Param("flightNumber") String flightNumber,
									   @Param("departureAirport") String departureAirport,
									   @Param("arrivalAirport") String arrivalAirport);
}
