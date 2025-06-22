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
import com.project.quanlycanghangkhong.dto.FlightTimeUpdateRequest;
import com.project.quanlycanghangkhong.dto.CreateFlightRequest;
import com.project.quanlycanghangkhong.dto.UpdateFlightRequest;
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
	public void deleteFlight(Long id) {
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
}
