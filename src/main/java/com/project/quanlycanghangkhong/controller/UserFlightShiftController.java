package com.project.quanlycanghangkhong.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

import com.project.quanlycanghangkhong.request.ApplyFlightShiftRequest;
import com.project.quanlycanghangkhong.dto.UserFlightShiftResponseDTO;
import com.project.quanlycanghangkhong.dto.UserFlightShiftResponseSearchDTO;
import com.project.quanlycanghangkhong.request.UpdateUserFlightShiftRequest;
import com.project.quanlycanghangkhong.service.UserFlightShiftService;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/user-flight-shifts")
@CrossOrigin(origins = "*")
public class UserFlightShiftController {

    @Autowired
    private UserFlightShiftService userFlightShiftService;

    @GetMapping
    @Operation(summary = "Get all user flight shifts", description = "Retrieve a list of all user flight shifts (DTO only)")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved all user flight shifts",
            content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))
        )
    })
    public ResponseEntity<ApiResponseCustom<List<UserFlightShiftResponseDTO>>> getAllUserFlightShifts() {
        List<UserFlightShiftResponseDTO> dtos = userFlightShiftService.getAllUserFlightShifts();
        return ResponseEntity.ok(ApiResponseCustom.success(dtos));
    }

        @GetMapping("/date/{date}")
    @Operation(summary = "Get shifts by date", description = "Get all user flight shifts for a specific date")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved shifts for the given date",
            content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))
        )
    })
    public ResponseEntity<ApiResponseCustom<List<UserFlightShiftResponseDTO>>> getShiftsByDate(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        List<UserFlightShiftResponseDTO> dtos = userFlightShiftService.getShiftsByDateDTO(date);
        return ResponseEntity.ok(ApiResponseCustom.success(dtos));
    }

    @GetMapping("/by-user/{userId}")
    @Operation(summary = "Get user flight shifts by user", description = "Retrieve user flight shifts by user (DTO only)")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved user flight shifts by user",
            content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))
        )
    })
    public ResponseEntity<ApiResponseCustom<List<UserFlightShiftResponseDTO>>> getShiftsByUser(@PathVariable Integer userId) {
        List<UserFlightShiftResponseDTO> dtos = userFlightShiftService.getShiftsByUserDTO(userId);
        return ResponseEntity.ok(ApiResponseCustom.success(dtos));
    }

    @PostMapping("/apply")
    @Operation(summary = "Apply user flight shift", description = "Apply a new user flight shift")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User flight shift applied successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))
        )
    })
    public ResponseEntity<ApiResponseCustom<Object>> applyFlightShift(@RequestBody ApplyFlightShiftRequest request) {
        userFlightShiftService.applyFlightShift(request);
        return ResponseEntity.ok(ApiResponseCustom.success(null));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user flight shift", description = "Update user flight shift by id")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully updated user flight shift",
            content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))
        )
    })
    public ResponseEntity<ApiResponseCustom<UserFlightShiftResponseDTO>> updateUserFlightShift(
            @PathVariable Integer id,
            @RequestBody UpdateUserFlightShiftRequest request) {
        userFlightShiftService.updateUserFlightShift(id, request.getShiftDate(), request.getFlightId());
        UserFlightShiftResponseDTO data = userFlightShiftService.getAllUserFlightShifts()
            .stream().filter(dto -> dto.getId().equals(id)).findFirst().orElse(null);
        return ResponseEntity.ok(ApiResponseCustom.updated(data));
    }

    @GetMapping("/shifts")
    @Operation(summary = "Get user flight shifts by flight and date", description = "Retrieve user flight shifts by flight and date")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved user flight shifts by flight and date",
            content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))
        )
    })
    public ResponseEntity<ApiResponseCustom<List<UserFlightShiftResponseDTO>>> getShiftsByFlightAndDate(
            @RequestParam("flightId") Integer flightId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<UserFlightShiftResponseDTO> dtos = userFlightShiftService.getShiftsByFlightAndDate(flightId.longValue(), date);
        return ResponseEntity.ok(ApiResponseCustom.success(dtos));
    }

        @GetMapping("/available")
    @Operation(summary = "Get available user flight shifts", description = "Retrieve available user flight shifts")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved available user flight shifts",
            content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))
        )
    })
    public ResponseEntity<ApiResponseCustom<List<UserFlightShiftResponseDTO>>> getAvailableShifts(
            @RequestParam("userId") Integer userId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<UserFlightShiftResponseDTO> dtos = userFlightShiftService.getAvailableShifts(userId.longValue(), date);
        return ResponseEntity.ok(ApiResponseCustom.success(dtos));
    }

    @GetMapping("/assigned")
    @Operation(summary = "Get assigned user flight shifts", description = "Retrieve assigned user flight shifts")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved assigned user flight shifts",
            content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))
        )
    })
    public ResponseEntity<ApiResponseCustom<List<UserFlightShiftResponseDTO>>> getAssignedShifts(
            @RequestParam("userId") Integer userId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<UserFlightShiftResponseDTO> dtos = userFlightShiftService.getAssignedShifts(userId.longValue(), date);
        return ResponseEntity.ok(ApiResponseCustom.success(dtos));
    }

    @DeleteMapping("/remove")
    @Operation(summary = "Remove flight assignment", description = "Remove flight assignment for user")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Flight assignment removed successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))
        )
    })
    public ResponseEntity<ApiResponseCustom<Object>> removeFlightAssignment(
            @RequestParam("userId") Integer userId,
            @RequestParam("flightId") Integer flightId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        userFlightShiftService.removeFlightAssignment(userId.longValue(), date, flightId);
        return ResponseEntity.ok(ApiResponseCustom.deleted());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user flight shift", description = "Delete a user flight shift by ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User flight shift deleted successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))
        )
    })
    public ResponseEntity<ApiResponseCustom<Object>> deleteUserFlightShift(@PathVariable Integer id) {
        userFlightShiftService.deleteUserFlightShiftById(id);
        return ResponseEntity.ok(ApiResponseCustom.deleted());
    }

    @GetMapping("/check-assigned")
    @Operation(summary = "Check if user is assigned to flight", description = "Check if user is assigned to a specific flight")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Check result retrieved successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))
        )
    })
    public ResponseEntity<ApiResponseCustom<Map<String, Boolean>>> isUserAssigned(
            @RequestParam("userId") Integer userId,
            @RequestParam("flightId") Integer flightId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        boolean isAssigned = userFlightShiftService.isUserAssignedToFlight(date, userId);
        Map<String, Boolean> result = new HashMap<>();
        result.put("isAssigned", isAssigned);
        return ResponseEntity.ok(ApiResponseCustom.success(result));
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter user flight shifts", description = "Filter user flight shifts by criteria")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully filtered user flight shifts",
            content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))
        )
    })
    public ResponseEntity<ApiResponseCustom<List<UserFlightShiftResponseSearchDTO>>> getFlightSchedules(
            @RequestParam("shiftDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate shiftDate,
            @RequestParam(value = "teamId", required = false) Integer teamId,
            @RequestParam(value = "unitId", required = false) Integer unitId,
            @RequestParam(value = "flightId", required = false) Long flightId) {
        List<UserFlightShiftResponseSearchDTO> dtos = userFlightShiftService.getFlightSchedulesByCriteria(shiftDate, teamId, unitId, flightId);
        return ResponseEntity.ok(ApiResponseCustom.success(dtos));
    }

    @GetMapping("/filter-schedules")
    @Operation(summary = "Filter user flight shifts", description = "Filter user flight shifts by date, team, unit")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully filtered user flight shifts",
            content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))
        )
    })
    public ResponseEntity<ApiResponseCustom<List<UserFlightShiftResponseSearchDTO>>> filterUserFlightShifts(
            @RequestParam("shiftDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate shiftDate,
            @RequestParam(value = "teamId", required = false) Integer teamId,
            @RequestParam(value = "unitId", required = false) Integer unitId
    ) {
        List<UserFlightShiftResponseSearchDTO> dtos = userFlightShiftService.getFlightSchedulesByCriteria(shiftDate, teamId, unitId, null);
        return ResponseEntity.ok(ApiResponseCustom.success(dtos));
    }
}
