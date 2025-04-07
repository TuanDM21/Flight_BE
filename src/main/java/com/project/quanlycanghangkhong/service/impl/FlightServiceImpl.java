package com.project.quanlycanghangkhong.service.impl;

import com.project.quanlycanghangkhong.model.Flight;
import com.project.quanlycanghangkhong.repository.FlightRepository;
import com.project.quanlycanghangkhong.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class FlightServiceImpl implements FlightService {

    @Autowired
    private FlightRepository flightRepository;

    @Override
    public Flight createFlight(Flight flight) {
        return flightRepository.save(flight);
    }

    @Override
    public Optional<Flight> getFlightById(Long id) {
        return flightRepository.findById(id);
    }

    @Override
    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    @Override
    public Flight updateFlight(Long id, Flight flightData) {
        return flightRepository.findById(id).map(flight -> {
            flight.setFlightNumber(flightData.getFlightNumber());
            flight.setDepartureAirport(flightData.getDepartureAirport());
            flight.setArrivalAirport(flightData.getArrivalAirport());
            flight.setDepartureTime(flightData.getDepartureTime());
            flight.setArrivalTime(flightData.getArrivalTime());
            return flightRepository.save(flight);
        }).orElse(null);
    }

    @Override
    public void deleteFlight(Long id) {
        flightRepository.deleteById(id);
    }
}
