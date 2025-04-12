package com.project.quanlycanghangkhong.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.quanlycanghangkhong.dto.UserFlightShiftResponseSearchDTO;
import com.project.quanlycanghangkhong.model.Flight;
import com.project.quanlycanghangkhong.model.User;
import com.project.quanlycanghangkhong.model.UserFlightShift;

public interface UserFlightShiftRepository extends JpaRepository<UserFlightShift, Integer> {
	// Thêm các query tùy theo nghiệp vụ nếu cần
	Optional<UserFlightShift> findByUserAndFlightAndShiftDate(User user, Flight flight, LocalDate shiftDate);

	List<UserFlightShift> findByShiftDate(LocalDate shiftDate);

	// Lấy danh sách ca trực theo user
	List<UserFlightShift> findByUser_Id(Integer userId);

	// --- Thêm: Lấy danh sách ca trực theo flight và ngày ---
	List<UserFlightShift> findByFlight_IdAndShiftDate(Long flightId, LocalDate shiftDate);

	// Trong interface UserFlightShiftRepository
	@Query("SELECT ufs FROM UserFlightShift ufs " +
			"WHERE ufs.flight.id = :flightId " +
			"AND ufs.shiftDate = :shiftDate " +
			"AND ufs.user.id = :userId")
	Optional<UserFlightShift> findOneByFlightAndShiftDateAndUser(
			@Param("flightId") Long flightId,
			@Param("shiftDate") LocalDate shiftDate,
			@Param("userId") Integer userId);

	boolean existsByShiftDateAndUser_Id(LocalDate shiftDate, Integer userId);

	@Query("SELECT new com.project.quanlycanghangkhong.dto.UserFlightShiftResponseSearchDTO(" +
			"ufs.id, " +
			"ufs.user.name, " +
			"ufs.user.team.teamName, " +
			"ufs.user.unit.unitName, " +
			"ufs.shiftDate, " +
			"ufs.flight.flightNumber, " +
			"ufs.flight.departureTime, " +
			"ufs.flight.arrivalTime" +
			") " +
			"FROM UserFlightShift ufs " +
			"WHERE ufs.shiftDate = :shiftDate " +
			"AND (:teamId IS NULL OR ufs.user.team.id = :teamId) " +
			"AND (:unitId IS NULL OR ufs.user.unit.id = :unitId) " +
			"AND (:flightId IS NULL OR ufs.flight.id = :flightId)")
	List<UserFlightShiftResponseSearchDTO> findFlightSchedulesByCriteria(
			@Param("shiftDate") LocalDate shiftDate,
			@Param("teamId") Integer teamId,
			@Param("unitId") Integer unitId,
			@Param("flightId") Long flightId);

}
