package com.project.quanlycanghangkhong.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.quanlycanghangkhong.dto.FlightDTO;
import com.project.quanlycanghangkhong.model.Flight;
import com.project.quanlycanghangkhong.service.FlightService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/flights")
public class FlightController {

    @Autowired
    private FlightService flightService;

    @GetMapping
    public ResponseEntity<List<FlightDTO>> getAllFlights() {
        List<FlightDTO> dtos = flightService.getAllFlights();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlightDTO> getFlightById(@PathVariable Long id) {
        Optional<FlightDTO> dto = flightService.getFlightById(id);
        return dto.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<FlightDTO> createFlight(@RequestBody Flight flight) {
        FlightDTO dto = flightService.createFlight(flight);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlightDTO> updateFlight(@PathVariable Long id, @RequestBody Flight flightData) {
        try {
            FlightDTO updatedDto = flightService.updateFlight(id, flightData);
            return ResponseEntity.ok(updatedDto);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<FlightDTO>> searchFlights(
            @RequestParam(value = "keyword", required = false) String keyword) {
        List<FlightDTO> dtos = flightService.searchFlights(keyword);
        return ResponseEntity.ok(dtos);
    }
    @GetMapping("/today")
    public ResponseEntity<List<FlightDTO>> getTodayFlights() {
        List<FlightDTO> dtos = flightService.getTodayFlights();
        return ResponseEntity.ok(dtos);
    }

    // Endpoint tìm kiếm chuyến bay theo ngày (đúng hoàn toàn)
    @GetMapping("/searchByDate")
    public ResponseEntity<List<FlightDTO>> searchFlightByDate(@RequestParam("date") String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr);  // format YYYY-MM-DD
            List<FlightDTO> dtos = flightService.getFlightsByExactDate(date);
            return ResponseEntity.ok(dtos);
        } catch (DateTimeParseException ex) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/searchByDateAndKeyword")
    public ResponseEntity<List<FlightDTO>> searchFlightByDateAndKeyword(
        @RequestParam("date") String dateStr,
        @RequestParam(value = "keyword", required = false) String keyword
    ) {
        try {
            LocalDate date = LocalDate.parse(dateStr);  // format YYYY-MM-DD
            List<FlightDTO> dtos = flightService.getFlightsByDateAndKeyword(date, keyword);
            return ResponseEntity.ok(dtos);
        } catch (DateTimeParseException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

}
