package com.project.quanlycanghangkhong.service.impl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.quanlycanghangkhong.dto.FlightDTO;
import com.project.quanlycanghangkhong.request.FlightTimeUpdateRequest;
import com.project.quanlycanghangkhong.request.CreateFlightRequest;
import com.project.quanlycanghangkhong.request.UpdateFlightRequest;
import com.project.quanlycanghangkhong.model.Flight;
import com.project.quanlycanghangkhong.model.Airport;
import com.project.quanlycanghangkhong.repository.FlightRepository;
import com.project.quanlycanghangkhong.repository.AirportRepository;
import com.project.quanlycanghangkhong.service.FlightService;
import com.project.quanlycanghangkhong.service.UserFlightShiftService;
import com.project.quanlycanghangkhong.service.UserShiftService;
import com.project.quanlycanghangkhong.service.NotificationService;

@Service
public class FlightServiceImpl implements FlightService {

	@Autowired
	private FlightRepository flightRepository;

	@Autowired
	private AirportRepository airportRepository;

	@Autowired
	private UserFlightShiftService userFlightShiftService;

	@Autowired
	private UserShiftService userShiftService;

	@Autowired
	private NotificationService notificationService;

	@Override
	public FlightDTO createFlight(Flight flight) {
		Flight saved = flightRepository.save(flight);
		return new FlightDTO(saved);
	}

	@Override
	public FlightDTO createFlightFromRequest(CreateFlightRequest request) {
		// Tìm departure airport từ database bằng ID
		Airport departureAirport = airportRepository.findById(request.getDepartureAirportId())
				.orElseThrow(() -> new RuntimeException("Departure airport not found with id: " + request.getDepartureAirportId()));
		
		// Tìm arrival airport từ database bằng ID
		Airport arrivalAirport = airportRepository.findById(request.getArrivalAirportId())
				.orElseThrow(() -> new RuntimeException("Arrival airport not found with id: " + request.getArrivalAirportId()));
		
		// Chuyển đổi CreateFlightRequest sang Flight entity
		Flight flight = new Flight();
		flight.setFlightNumber(request.getFlightNumber());
		flight.setDepartureAirport(departureAirport);
		flight.setArrivalAirport(arrivalAirport);
		
		// Parse String time sang LocalTime
		flight.setDepartureTime(LocalTime.parse(request.getDepartureTime()));
		flight.setArrivalTime(LocalTime.parse(request.getArrivalTime()));
		
		// Handle optional arrivalTimeatArrival
		if (request.getArrivalTimeatArrival() != null && !request.getArrivalTimeatArrival().trim().isEmpty()) {
			flight.setArrivalTimeatArrival(LocalTime.parse(request.getArrivalTimeatArrival()));
		}
		
		flight.setStatus(request.getStatus());
		flight.setFlightDate(request.getFlightDate());
		flight.setAirline(request.getAirline());
		flight.setCheckInCounters(request.getCheckInCounters());
		flight.setGate(request.getGate());
		flight.setNote(request.getNote());
		
		Flight saved = flightRepository.save(flight);
		return new FlightDTO(saved);
	}

	@Override
	public FlightDTO updateFlightFromRequest(Long id, UpdateFlightRequest request) {
		// Tìm flight cần update
		Flight existingFlight = flightRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Flight not found with id: " + id));
		
		// Tìm departure airport từ database bằng ID
		Airport departureAirport = airportRepository.findById(request.getDepartureAirportId())
				.orElseThrow(() -> new RuntimeException("Departure airport not found with id: " + request.getDepartureAirportId()));
		
		// Tìm arrival airport từ database bằng ID
		Airport arrivalAirport = airportRepository.findById(request.getArrivalAirportId())
				.orElseThrow(() -> new RuntimeException("Arrival airport not found with id: " + request.getArrivalAirportId()));
		
		// Cập nhật thông tin flight
		existingFlight.setFlightNumber(request.getFlightNumber());
		existingFlight.setDepartureAirport(departureAirport);
		existingFlight.setArrivalAirport(arrivalAirport);
		
		// Parse và cập nhật thời gian
		existingFlight.setDepartureTime(LocalTime.parse(request.getDepartureTime()));
		existingFlight.setArrivalTime(LocalTime.parse(request.getArrivalTime()));
		
		// Handle optional arrivalTimeatArrival
		if (request.getArrivalTimeatArrival() != null && !request.getArrivalTimeatArrival().trim().isEmpty()) {
			existingFlight.setArrivalTimeatArrival(LocalTime.parse(request.getArrivalTimeatArrival()));
		} else {
			existingFlight.setArrivalTimeatArrival(null);
		}
		
		// Cập nhật các thông tin khác
		existingFlight.setStatus(request.getStatus());
		existingFlight.setFlightDate(request.getFlightDate());
		existingFlight.setAirline(request.getAirline());
		existingFlight.setCheckInCounters(request.getCheckInCounters());
		existingFlight.setGate(request.getGate());
		existingFlight.setNote(request.getNote());
		
		Flight updatedFlight = flightRepository.save(existingFlight);
		return new FlightDTO(updatedFlight);
	}

	@Override
	public List<FlightDTO> getAllFlights() {
		List<Flight> flights = flightRepository.findAll();
		return flights.stream().map(FlightDTO::new).collect(Collectors.toList());
	}

	@Override
	public Optional<FlightDTO> getFlightById(Long id) {
		return flightRepository.findById(id).map(FlightDTO::new);
	}

	@Override
    public FlightDTO updateFlight(Long id, Flight flightData) {
        Flight existingFlight = flightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chuyến bay không tồn tại!"));
        
        // Cập nhật các trường cũ
        existingFlight.setFlightNumber(flightData.getFlightNumber());
        existingFlight.setDepartureAirport(flightData.getDepartureAirport());
        existingFlight.setArrivalAirport(flightData.getArrivalAirport());
        existingFlight.setDepartureTime(flightData.getDepartureTime());
        existingFlight.setArrivalTime(flightData.getArrivalTime());
        existingFlight.setFlightDate(flightData.getFlightDate());
		existingFlight.setNote(flightData.getNote());

        // Cập nhật các trường thực tế
        existingFlight.setActualDepartureTime(flightData.getActualDepartureTime());
        existingFlight.setActualArrivalTime(flightData.getActualArrivalTime());
        existingFlight.setActualDepartureTimeAtArrival(flightData.getActualDepartureTimeAtArrival());
        existingFlight.setArrivalTimeatArrival(flightData.getArrivalTimeatArrival());
        existingFlight.setStatus(flightData.getStatus());

        // Cập nhật các field mới
        existingFlight.setAirline(flightData.getAirline());
        existingFlight.setCheckInCounters(flightData.getCheckInCounters());
        existingFlight.setGate(flightData.getGate());

        Flight updatedFlight = flightRepository.save(existingFlight);
        return new FlightDTO(updatedFlight);
    }

	@Override
	@Transactional
	public void deleteFlight(Long id) {
		// Kiểm tra flight có tồn tại không trước khi xóa
		if (!flightRepository.existsById(id)) {
			throw new RuntimeException("Không tìm thấy chuyến bay với id: " + id);
		}
		
		// Với cascade = CascadeType.ALL và orphanRemoval = true trong Flight entity,
		// các UserFlightShift liên quan sẽ được tự động xóa
		flightRepository.deleteById(id);
	}

	@Override
	public List<FlightDTO> searchFlights(String keyword) {
		List<Flight> flights;
		if (keyword == null || keyword.trim().isEmpty()) {
			flights = flightRepository.findAll();
		} else {
			flights = flightRepository.findByFlightNumberContaining(keyword);
		}
		return flights.stream().map(FlightDTO::new).collect(Collectors.toList());
	}

	@Override
	public List<FlightDTO> getTodayFlights() {
		LocalDate today = LocalDate.now();
		LocalDate yesterday = today.minusDays(1);
		return flightRepository.findFlightsForServiceDay(today, yesterday);
	}

	@Override
	public List<FlightDTO> getFlightsByExactDate(LocalDate flightDate) {
		List<Flight> flights = flightRepository.findByFlightDate(flightDate);
		return flights.stream().map(FlightDTO::new).collect(Collectors.toList());
	}
	
	@Override
	public List<FlightDTO> getFlightsByDateAndKeyword(LocalDate date, String keyword) {
		// Nếu keyword null => mặc định là chuỗi trống
		if (keyword == null) {
			keyword = "";
		}
		// Tìm danh sách chuyến bay khớp với date và flightNumber chứa keyword
		List<Flight> flights = flightRepository.findByFlightDateAndFlightNumberContainingIgnoreCase(date, keyword);

		return flights.stream()
					  .map(FlightDTO::new)
					  .collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void updateFlightTimes(Long id, FlightTimeUpdateRequest req) {
		Flight f = flightRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("Không tìm thấy flight " + id));
		if (req.getActualDepartureTime() != null) {
			f.setActualDepartureTime(LocalTime.parse(req.getActualDepartureTime()));
		}
		if (req.getActualArrivalTime() != null) {
			f.setActualArrivalTime(LocalTime.parse(req.getActualArrivalTime()));
		}
		if (req.getActualDepartureTimeAtArrival() != null) {
			f.setActualDepartureTimeAtArrival(LocalTime.parse(req.getActualDepartureTimeAtArrival()));
		}
		flightRepository.save(f);
	}

	@Override
	public Flight getFlightEntityById(Long id) {
		return flightRepository.findById(id).orElse(null);
	}

	@Override
	public void notifyUsersOnActualTimeChange(Long flightId, LocalTime actualTime, String eventType, String airportCode) {
		Flight flight = getFlightEntityById(flightId);
		if (flight == null || actualTime == null) return;
		LocalDate flightDate = flight.getFlightDate();
		// Lấy user phục vụ chuyến bay
		HashSet<Integer> userIds = new HashSet<>(userFlightShiftService.getUserIdsByFlightAndDate(flightId, flightDate));
		// Lấy user trực ca
		userIds.addAll(userShiftService.getUserIdsOnDutyAtTime(flightDate, actualTime));
		if (!userIds.isEmpty()) {
			String title = "Thông báo chuyến bay";
			String content;
			if ("actualArrivalTime".equals(eventType)) {
				content = "Hạ cánh thực tế tại " + airportCode + ": " + actualTime;
			} else if ("actualDepartureTimeAtArrival".equals(eventType)) {
				content = "Cất cánh thực tế tại " + airportCode + ": " + actualTime;
			} else {
				content = "Chuyến bay " + flight.getFlightNumber() + " đã cập nhật giờ thực tế: " + actualTime;
			}
			notificationService.createNotifications(
				userIds.stream().toList(),
				"FLIGHT",
				title,
				content,
				flightId.intValue(),
				false
			);
		}
	}

	@Override
	public List<FlightDTO> searchFlightsByCriteria(String dateStr, String flightNumber, String departureAirport, String arrivalAirport) {
		// 🔍 Debug logging - Service layer
		System.out.println("=== SERVICE LAYER DEBUG ===");
		System.out.println("📥 Raw inputs:");
		System.out.println("   dateStr: " + dateStr);
		System.out.println("   flightNumber: " + flightNumber);
		System.out.println("   departureAirport: " + departureAirport);
		System.out.println("   arrivalAirport: " + arrivalAirport);
		
		// Parse date if provided
		LocalDate date = null;
		if (dateStr != null && !dateStr.trim().isEmpty() && !"null".equals(dateStr)) {
			try {
				date = LocalDate.parse(dateStr.trim());
				System.out.println("✅ Parsed date: " + date);
			} catch (Exception e) {
				System.err.println("❌ Date parse failed: " + e.getMessage());
				throw new RuntimeException("Invalid date format: " + dateStr + ". Use YYYY-MM-DD format.");
			}
		} else {
			System.out.println("ℹ️ No date provided or date is null/empty");
		}
		
		// Clean up parameters - handle "null" strings and empty strings
		String cleanFlightNumber = cleanParameter(flightNumber);
		String cleanDepartureAirport = cleanParameter(departureAirport);
		String cleanArrivalAirport = cleanParameter(arrivalAirport);
		
		System.out.println("🧹 Cleaned parameters:");
		System.out.println("   date: " + date);
		System.out.println("   cleanFlightNumber: " + cleanFlightNumber);
		System.out.println("   cleanDepartureAirport: " + cleanDepartureAirport);
		System.out.println("   cleanArrivalAirport: " + cleanArrivalAirport);
		
		// 🔍 Generate SQL for manual testing
		System.out.println("🔧 SQL FOR MANUAL DATABASE TESTING:");
		System.out.println("SELECT DISTINCT f.* FROM flights f");
		System.out.println("LEFT JOIN airports da ON da.id = f.departure_airport_id");
		System.out.println("LEFT JOIN airports aa ON aa.id = f.arrival_airport_id");
		System.out.println("WHERE 1=1");
		
		if (dateStr != null && !dateStr.trim().isEmpty()) {
			System.out.println("AND f.flight_date = '" + dateStr + "'");
		}
		if (cleanFlightNumber != null) {
			System.out.println("AND LOWER(f.flight_number) LIKE LOWER('%" + cleanFlightNumber + "%')");
		}
		if (cleanDepartureAirport != null) {
			System.out.println("AND (da.airport_code IS NOT NULL AND (LOWER(da.airport_code) LIKE LOWER('%" + cleanDepartureAirport + "%') OR LOWER(da.airport_name) LIKE LOWER('%" + cleanDepartureAirport + "%')))");
		}
		if (cleanArrivalAirport != null) {
			System.out.println("AND (aa.airport_code IS NOT NULL AND (LOWER(aa.airport_code) LIKE LOWER('%" + cleanArrivalAirport + "%') OR LOWER(aa.airport_name) LIKE LOWER('%" + cleanArrivalAirport + "%')))");
		}
		System.out.println(";");
		
		// 🔍 Debug airports data
		if (cleanDepartureAirport != null) {
			System.out.println("🔍 DEBUG: Looking for departure airports with code/name containing: " + cleanDepartureAirport);
		}
		if (cleanArrivalAirport != null) {
			System.out.println("🔍 DEBUG: Looking for arrival airports with code/name containing: " + cleanArrivalAirport);
		}
		
		// Call repository method with cleaned parameters
		System.out.println("🔍 Calling repository...");
		List<Flight> flights = flightRepository.findFlightsByCriteria(dateStr, cleanFlightNumber, cleanDepartureAirport, cleanArrivalAirport);
		
		System.out.println("📊 Repository results: " + (flights != null ? flights.size() : "NULL") + " flights found");
		if (flights != null && !flights.isEmpty()) {
			System.out.println("🎯 Sample results:");
			for (int i = 0; i < Math.min(3, flights.size()); i++) {
				Flight f = flights.get(i);
				System.out.println("   [" + i + "] " + f.getFlightNumber() + " | " + f.getFlightDate() + 
					" | " + (f.getDepartureAirport() != null ? f.getDepartureAirport().getAirportCode() : "NULL") +
					" -> " + (f.getArrivalAirport() != null ? f.getArrivalAirport().getAirportCode() : "NULL"));
			}
		}
		
		List<FlightDTO> result = flights.stream()
			.map(FlightDTO::new)
			.collect(Collectors.toList());
			
		System.out.println("🚀 Final DTO results: " + result.size() + " items");
		System.out.println("============================");
		
		return result;
	}
	
	// Helper method to clean parameters
	private String cleanParameter(String param) {
		if (param == null || param.trim().isEmpty() || "null".equals(param) || "undefined".equals(param)) {
			return null;
		}
		return param.trim();
	}
}
