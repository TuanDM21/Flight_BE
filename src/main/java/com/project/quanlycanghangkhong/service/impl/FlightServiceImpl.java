package com.project.quanlycanghangkhong.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.quanlycanghangkhong.dto.FlightDTO;
import com.project.quanlycanghangkhong.model.Flight;
import com.project.quanlycanghangkhong.repository.FlightRepository;
import com.project.quanlycanghangkhong.service.FlightService;

@Service
public class FlightServiceImpl implements FlightService {

	@Autowired
	private FlightRepository flightRepository;

	@Override
	public FlightDTO createFlight(Flight flight) {
		Flight saved = flightRepository.save(flight);
		return new FlightDTO(saved);
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

        // Cập nhật các trường thực tế
        existingFlight.setActualDepartureTime(flightData.getActualDepartureTime());
        existingFlight.setActualArrivalTime(flightData.getActualArrivalTime());
        existingFlight.setActualDepartureTimeAtArrival(flightData.getActualDepartureTimeAtArrival());

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
	        List<Flight> flights = flightRepository.findFlightsForServiceDay(today, yesterday);
	        return flights.stream().map(FlightDTO::new).collect(Collectors.toList());
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

}
