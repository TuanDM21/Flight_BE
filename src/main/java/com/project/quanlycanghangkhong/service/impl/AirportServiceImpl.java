package com.project.quanlycanghangkhong.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.quanlycanghangkhong.model.Airport;
import com.project.quanlycanghangkhong.dto.AirportDTO;
import com.project.quanlycanghangkhong.repository.AirportRepository;
import com.project.quanlycanghangkhong.service.AirportService;

@Service
public class AirportServiceImpl implements AirportService {

    @Autowired
    private AirportRepository airportRepository;

    @Override
    public Airport createAirport(Airport airport) {
        return airportRepository.save(airport);
    }

    @Override
    public List<Airport> getAllAirports() {
        return airportRepository.findAll();
    }

    @Override
    public Optional<Airport> getAirportById(Long id) {
        return airportRepository.findById(id);
    }

    @Override
    public Airport updateAirport(Long id, Airport airportData) {
        Optional<Airport> optionalAirport = airportRepository.findById(id);
        if (optionalAirport.isPresent()) {
            Airport airport = optionalAirport.get();
            airport.setAirportCode(airportData.getAirportCode());
            airport.setAirportName(airportData.getAirportName());
            airport.setLatitude(airportData.getLatitude());
            airport.setLongitude(airportData.getLongitude());
            airport.setCity(airportData.getCity());
            airport.setCountry(airportData.getCountry());
            return airportRepository.save(airport);
        } else {
            throw new RuntimeException("Airport not found with id: " + id);
        }
    }

    @Override
    public void deleteAirport(Long id) {
        airportRepository.deleteById(id);
    }

    // DTO methods
    @Override
    public AirportDTO createAirportDTO(Airport airport) {
        Airport savedAirport = airportRepository.save(airport);
        return convertToDTO(savedAirport);
    }

    @Override
    public List<AirportDTO> getAllAirportsDTO() {
        List<Airport> airports = airportRepository.findAll();
        return airports.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<AirportDTO> getAirportDTOById(Long id) {
        Optional<Airport> airport = airportRepository.findById(id);
        return airport.map(this::convertToDTO);
    }

    @Override
    public AirportDTO updateAirportDTO(Long id, Airport airportData) {
        Airport updatedAirport = updateAirport(id, airportData);
        return convertToDTO(updatedAirport);
    }

    // Helper method to convert Airport to AirportDTO
    private AirportDTO convertToDTO(Airport airport) {
        return new AirportDTO(
            airport.getId(),
            airport.getAirportCode(),
            airport.getAirportName(),
            airport.getLatitude(),
            airport.getLongitude(),
            airport.getCity(),
            airport.getCountry(),
            airport.getCreatedAt(),
            airport.getUpdatedAt()
        );
    }
}
