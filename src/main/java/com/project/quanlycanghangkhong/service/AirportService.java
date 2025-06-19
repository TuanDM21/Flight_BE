package com.project.quanlycanghangkhong.service;

import java.util.List;
import java.util.Optional;
import com.project.quanlycanghangkhong.model.Airport;
import com.project.quanlycanghangkhong.dto.AirportDTO;

public interface AirportService {
    Airport createAirport(Airport airport);
    List<Airport> getAllAirports();
    Optional<Airport> getAirportById(Long id);
    Airport updateAirport(Long id, Airport airportData);
    void deleteAirport(Long id);
    
    // DTO methods
    AirportDTO createAirportDTO(Airport airport);
    List<AirportDTO> getAllAirportsDTO();
    Optional<AirportDTO> getAirportDTOById(Long id);
    AirportDTO updateAirportDTO(Long id, Airport airportData);
}
