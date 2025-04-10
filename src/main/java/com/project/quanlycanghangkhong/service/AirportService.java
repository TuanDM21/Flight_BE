package com.project.quanlycanghangkhong.service;

import java.util.List;
import java.util.Optional;
import com.project.quanlycanghangkhong.model.Airport;

public interface AirportService {
    Airport createAirport(Airport airport);
    List<Airport> getAllAirports();
    Optional<Airport> getAirportById(Long id);
    Airport updateAirport(Long id, Airport airportData);
    void deleteAirport(Long id);
}
