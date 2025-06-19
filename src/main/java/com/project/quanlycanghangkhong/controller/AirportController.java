package com.project.quanlycanghangkhong.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.quanlycanghangkhong.model.Airport;
import com.project.quanlycanghangkhong.dto.AirportDTO;
import com.project.quanlycanghangkhong.service.AirportService;
import com.project.quanlycanghangkhong.dto.response.airports.*;

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
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Airport created successfully", content = @Content(schema = @Schema(implementation = ApiCreateAirportResponse.class)))
    })
    public ResponseEntity<ApiCreateAirportResponse> createAirport(@RequestBody Airport airport) {
        AirportDTO createdAirportDTO = airportService.createAirportDTO(airport);
        ApiCreateAirportResponse response = new ApiCreateAirportResponse();
        response.setMessage("Thành công");
        response.setStatusCode(200);
        response.setData(createdAirportDTO);
        response.setSuccess(true);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all airports", description = "Retrieve a list of all airports")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved all airports", content = @Content(schema = @Schema(implementation = ApiAllAirportsResponse.class)))
    })
    public ResponseEntity<ApiAllAirportsResponse> getAllAirports() {
        List<AirportDTO> airportsDTO = airportService.getAllAirportsDTO();
        ApiAllAirportsResponse response = new ApiAllAirportsResponse();
        response.setMessage("Thành công");
        response.setStatusCode(200);
        response.setData(airportsDTO);
        response.setSuccess(true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get airport by ID", description = "Retrieve an airport by its ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved airport", content = @Content(schema = @Schema(implementation = ApiAirportByIdResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Airport not found", content = @Content(schema = @Schema(implementation = ApiAirportByIdResponse.class)))
    })
    public ResponseEntity<ApiAirportByIdResponse> getAirportById(@PathVariable Long id) {
        Optional<AirportDTO> optionalAirportDTO = airportService.getAirportDTOById(id);
        return optionalAirportDTO.map(airportDTO -> {
            ApiAirportByIdResponse response = new ApiAirportByIdResponse();
            response.setMessage("Thành công");
            response.setStatusCode(200);
            response.setData(airportDTO);
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        }).orElse(ResponseEntity.status(404)
            .body(new ApiAirportByIdResponse("Airport not found", 404, null, false)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update airport", description = "Update an existing airport")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Airport updated successfully", content = @Content(schema = @Schema(implementation = ApiUpdateAirportResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Airport not found", content = @Content(schema = @Schema(implementation = ApiUpdateAirportResponse.class)))
    })
    public ResponseEntity<ApiUpdateAirportResponse> updateAirport(@PathVariable Long id, @RequestBody Airport airportData) {
        try {
            AirportDTO updatedAirportDTO = airportService.updateAirportDTO(id, airportData);
            ApiUpdateAirportResponse response = new ApiUpdateAirportResponse();
            response.setMessage("Thành công");
            response.setStatusCode(200);
            response.setData(updatedAirportDTO);
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404)
                .body(new ApiUpdateAirportResponse("Airport not found", 404, null, false));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete airport", description = "Delete an airport by ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Airport deleted successfully", content = @Content(schema = @Schema(implementation = ApiDeleteAirportResponse.class)))
    })
    public ResponseEntity<ApiDeleteAirportResponse> deleteAirport(@PathVariable Long id) {
        airportService.deleteAirport(id);
        ApiDeleteAirportResponse response = new ApiDeleteAirportResponse();
        response.setMessage("Thành công");
        response.setStatusCode(204);
        response.setData(null);
        response.setSuccess(true);
        return ResponseEntity.status(204).body(response);
    }
}
