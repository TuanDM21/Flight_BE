package com.project.quanlycanghangkhong.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.quanlycanghangkhong.model.Airport;
import com.project.quanlycanghangkhong.dto.AirportDTO;
import com.project.quanlycanghangkhong.service.AirportService;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/airports")
public class AirportController {

    @Autowired
    private AirportService airportService;

    @PostMapping
    @Operation(summary = "Create airport", description = "Create a new airport")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Airport created successfully", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<AirportDTO>> createAirport(@RequestBody Airport airport) {
        AirportDTO createdAirportDTO = airportService.createAirportDTO(airport);
        // 🎯 SAME BUSINESS LOGIC - chỉ thay cách tạo response
        return ResponseEntity.ok(ApiResponseCustom.success("Thành công", createdAirportDTO));
    }

    @GetMapping
    @Operation(summary = "Get all airports", description = "Retrieve a list of all airports")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved all airports", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<List<AirportDTO>>> getAllAirports() {
        List<AirportDTO> airportsDTO = airportService.getAllAirportsDTO();
        // 🎯 SAME BUSINESS LOGIC - chỉ thay cách tạo response
        return ResponseEntity.ok(ApiResponseCustom.success("Thành công", airportsDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get airport by ID", description = "Retrieve an airport by its ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved airport", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Airport not found", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<AirportDTO>> getAirportById(@PathVariable Long id) {
        Optional<AirportDTO> optionalAirportDTO = airportService.getAirportDTOById(id);
        return optionalAirportDTO.map(airportDTO -> {
            // 🎯 SAME BUSINESS LOGIC - same success case
            return ResponseEntity.ok(ApiResponseCustom.success("Thành công", airportDTO));
        }).orElse(ResponseEntity.status(404)
            .body(ApiResponseCustom.notFound("Airport not found")));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update airport", description = "Update an existing airport")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Airport updated successfully", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Airport not found", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<AirportDTO>> updateAirport(@PathVariable Long id, @RequestBody Airport airportData) {
        try {
            AirportDTO updatedAirportDTO = airportService.updateAirportDTO(id, airportData);
            // 🎯 SAME BUSINESS LOGIC - same success case
            return ResponseEntity.ok(ApiResponseCustom.success("Thành công", updatedAirportDTO));
        } catch (RuntimeException ex) {
            // 🎯 SAME BUSINESS LOGIC - same error handling
            return ResponseEntity.status(404)
                .body(ApiResponseCustom.notFound("Airport not found"));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete airport", description = "Delete an airport by ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Airport deleted successfully", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<Void>> deleteAirport(@PathVariable Long id) {
        airportService.deleteAirport(id);
        // 🎯 SAME BUSINESS LOGIC - same 204 status code
        return ResponseEntity.status(204).body(ApiResponseCustom.<Void>builder()
            .message("Thành công")
            .statusCode(204)
            .data(null)
            .success(true)
            .build());
    }
}
