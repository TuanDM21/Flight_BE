package com.project.quanlycanghangkhong.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.quanlycanghangkhong.model.Airport;
import com.project.quanlycanghangkhong.service.AirportService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/airports")
public class AirportController {

    @Autowired
    private AirportService airportService;

    // Create airport
    @PostMapping
    public ResponseEntity<Airport> createAirport(@RequestBody Airport airport) {
        Airport createdAirport = airportService.createAirport(airport);
        return ResponseEntity.ok(createdAirport);
    }

    // Lấy danh sách tất cả các sân bay
    @GetMapping
    public ResponseEntity<List<Airport>> getAllAirports() {
        List<Airport> airports = airportService.getAllAirports();
        return ResponseEntity.ok(airports);
    }

    // Lấy sân bay theo id
    @GetMapping("/{id}")
    public ResponseEntity<Airport> getAirportById(@PathVariable Long id) {
        Optional<Airport> optionalAirport = airportService.getAirportById(id);
        return optionalAirport.map(ResponseEntity::ok)
                              .orElse(ResponseEntity.notFound().build());
    }

    // Cập nhật sân bay
    @PutMapping("/{id}")
    public ResponseEntity<Airport> updateAirport(@PathVariable Long id, @RequestBody Airport airportData) {
        try {
            Airport updatedAirport = airportService.updateAirport(id, airportData);
            return ResponseEntity.ok(updatedAirport);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // Xóa sân bay
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAirport(@PathVariable Long id) {
        airportService.deleteAirport(id);
        return ResponseEntity.noContent().build();
    }
}
