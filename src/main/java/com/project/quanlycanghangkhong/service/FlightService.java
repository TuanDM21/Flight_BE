package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.model.Flight;
import java.util.List;
import java.util.Optional;

public interface FlightService {
    Flight createFlight(Flight flight);
    Optional<Flight> getFlightById(Long id);
    List<Flight> getAllFlights();
    Flight updateFlight(Long id, Flight flightData);
    void deleteFlight(Long id);
}
