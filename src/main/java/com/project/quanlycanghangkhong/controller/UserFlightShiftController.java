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

import com.project.quanlycanghangkhong.dto.ApplyFlightShiftRequest;
import com.project.quanlycanghangkhong.dto.UserFlightShiftResponseDTO;
import com.project.quanlycanghangkhong.dto.UserFlightShiftResponseSearchDTO;
import com.project.quanlycanghangkhong.dto.UpdateUserFlightShiftRequest;
import com.project.quanlycanghangkhong.service.UserFlightShiftService;
import com.project.quanlycanghangkhong.dto.response.userflightshift.*;

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
            content = @Content(schema = @Schema(implementation = ApiAllUserFlightShiftsResponse.class))
        )
    })
    public ResponseEntity<ApiAllUserFlightShiftsResponse> getAllUserFlightShifts() {
        List<UserFlightShiftResponseDTO> dtos = userFlightShiftService.getAllUserFlightShifts();
        ApiAllUserFlightShiftsResponse response = new ApiAllUserFlightShiftsResponse("Thành công", 200, dtos, true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-date")
    @Operation(summary = "Get user flight shifts by date", description = "Retrieve user flight shifts by date (DTO only)")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved user flight shifts by date",
            content = @Content(schema = @Schema(implementation = ApiUserFlightShiftsByDateResponse.class))
        )
    })
    public ResponseEntity<ApiUserFlightShiftsByDateResponse> getShiftsByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<UserFlightShiftResponseDTO> dtos = userFlightShiftService.getShiftsByDateDTO(date);
        ApiUserFlightShiftsByDateResponse response = new ApiUserFlightShiftsByDateResponse("Thành công", 200, dtos, true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-user/{userId}")
    @Operation(summary = "Get user flight shifts by user", description = "Retrieve user flight shifts by user (DTO only)")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved user flight shifts by user",
            content = @Content(schema = @Schema(implementation = ApiUserFlightShiftsByUserResponse.class))
        )
    })
    public ResponseEntity<ApiUserFlightShiftsByUserResponse> getShiftsByUser(@PathVariable Integer userId) {
        List<UserFlightShiftResponseDTO> dtos = userFlightShiftService.getShiftsByUserDTO(userId);
        ApiUserFlightShiftsByUserResponse response = new ApiUserFlightShiftsByUserResponse("Thành công", 200, dtos, true);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/apply")
    @Operation(summary = "Apply user flight shift", description = "Apply a new user flight shift")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User flight shift applied successfully",
            content = @Content(schema = @Schema(implementation = ApiCreateUserFlightShiftResponse.class))
        )
    })
    public ResponseEntity<ApiCreateUserFlightShiftResponse> applyFlightShift(@RequestBody ApplyFlightShiftRequest request) {
        userFlightShiftService.applyFlightShift(request);
        ApiCreateUserFlightShiftResponse response = new ApiCreateUserFlightShiftResponse("Thành công", 200, null, true);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user flight shift", description = "Update user flight shift by id")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully updated user flight shift",
            content = @Content(schema = @Schema(implementation = ApiUpdateUserFlightShiftResponse.class))
        )
    })
    public ResponseEntity<ApiUpdateUserFlightShiftResponse> updateUserFlightShift(
            @PathVariable Integer id,
            @RequestBody UpdateUserFlightShiftRequest request) {
        userFlightShiftService.updateUserFlightShift(id, request.getShiftDate(), request.getFlightId());
        // Lấy lại thông tin sau cập nhật để trả về
        UserFlightShiftResponseDTO data = userFlightShiftService.getAllUserFlightShifts()
            .stream().filter(dto -> dto.getId().equals(id)).findFirst().orElse(null);
        ApiUpdateUserFlightShiftResponse response = new ApiUpdateUserFlightShiftResponse(
            "Cập nhật thành công", 200, data, true
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/shifts")
    @Operation(summary = "Get user flight shifts by flight and date", description = "Retrieve user flight shifts by flight and date")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved user flight shifts by flight and date",
            content = @Content(schema = @Schema(implementation = ApiUserFlightShiftsByFlightAndDateResponse.class))
        )
    })
    public ResponseEntity<ApiUserFlightShiftsByFlightAndDateResponse> getShiftsByFlightAndDate(
            @RequestParam("flightId") Long flightId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<UserFlightShiftResponseDTO> dtos = userFlightShiftService.getShiftsByFlightAndDate(flightId, date);
        ApiUserFlightShiftsByFlightAndDateResponse response = new ApiUserFlightShiftsByFlightAndDateResponse("Thành công", 200, dtos, true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/shifts/available")
    @Operation(summary = "Get available user flight shifts", description = "Retrieve available user flight shifts")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved available user flight shifts",
            content = @Content(schema = @Schema(implementation = ApiAvailableUserFlightShiftsResponse.class))
        )
    })
    public ResponseEntity<ApiAvailableUserFlightShiftsResponse> getAvailableShifts(
            @RequestParam("flightId") Long flightId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<UserFlightShiftResponseDTO> dtos = userFlightShiftService.getAvailableShifts(flightId, date);
        ApiAvailableUserFlightShiftsResponse response = new ApiAvailableUserFlightShiftsResponse("Thành công", 200, dtos, true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/shifts/assigned")
    @Operation(summary = "Get assigned user flight shifts", description = "Retrieve assigned user flight shifts")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved assigned user flight shifts",
            content = @Content(schema = @Schema(implementation = ApiAssignedUserFlightShiftsResponse.class))
        )
    })
    public ResponseEntity<ApiAssignedUserFlightShiftsResponse> getAssignedShifts(
            @RequestParam(value = "flightId", required = false) Long flightId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<UserFlightShiftResponseDTO> dtos = userFlightShiftService.getAssignedShifts(flightId, date);
        ApiAssignedUserFlightShiftsResponse response = new ApiAssignedUserFlightShiftsResponse("Thành công", 200, dtos, true);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    @Operation(summary = "Remove user flight assignment", description = "Remove a user flight assignment")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User flight assignment removed successfully",
            content = @Content(schema = @Schema(implementation = ApiDeleteUserFlightShiftResponse.class))
        )
    })
    public ResponseEntity<ApiDeleteUserFlightShiftResponse> removeFlightAssignment(
            @RequestParam("flightId") Long flightId,
            @RequestParam("shiftDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate shiftDate,
            @RequestParam("userId") Integer userId) {
        userFlightShiftService.removeFlightAssignment(flightId, shiftDate, userId);
        ApiDeleteUserFlightShiftResponse response = new ApiDeleteUserFlightShiftResponse("Thành công", 200, null, true);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user flight shift by id", description = "Delete user flight shift by id")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully deleted user flight shift",
            content = @Content(schema = @Schema(implementation = ApiDeleteUserFlightShiftResponse.class))
        )
    })
    public ResponseEntity<ApiDeleteUserFlightShiftResponse> deleteUserFlightShift(@PathVariable Integer id) {
        userFlightShiftService.deleteUserFlightShiftById(id);
        ApiDeleteUserFlightShiftResponse response = new ApiDeleteUserFlightShiftResponse("Xóa thành công", 200, null, true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/isAssigned")
    @Operation(summary = "Check if user is assigned to flight", description = "Check if a user is assigned to a flight on a specific date")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully checked assignment",
            content = @Content(schema = @Schema(implementation = ApiCheckUserAssignedFlightResponse.class))
        )
    })
    public ResponseEntity<ApiCheckUserAssignedFlightResponse> isUserAssigned(
            @RequestParam("shiftDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate shiftDate,
            @RequestParam("userId") Integer userId) {
        boolean assigned = userFlightShiftService.isUserAssignedToFlight(shiftDate, userId);
        Map<String, Boolean> result = new HashMap<>();
        result.put("assigned", assigned);
        ApiCheckUserAssignedFlightResponse response = new ApiCheckUserAssignedFlightResponse("Thành công", 200, result, true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter user flight shifts", description = "Filter user flight shifts by criteria")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully filtered user flight shifts",
            content = @Content(schema = @Schema(implementation = ApiFilterUserFlightShiftsResponse.class))
        )
    })
    public ResponseEntity<ApiFilterUserFlightShiftsResponse> getFlightSchedules(
            @RequestParam("shiftDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate shiftDate,
            @RequestParam(value = "teamId", required = false) Integer teamId,
            @RequestParam(value = "unitId", required = false) Integer unitId,
            @RequestParam(value = "flightId", required = false) Long flightId) {
        List<UserFlightShiftResponseSearchDTO> dtos = userFlightShiftService.getFlightSchedulesByCriteria(shiftDate, teamId, unitId, flightId);
        ApiFilterUserFlightShiftsResponse response = new ApiFilterUserFlightShiftsResponse("Thành công", 200, dtos, true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter-schedules")
    @Operation(summary = "Filter user flight shifts", description = "Filter user flight shifts by date, team, unit")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully filtered user flight shifts",
            content = @Content(schema = @Schema(implementation = ApiFilterUserFlightShiftsResponse.class))
        )
    })
    public ResponseEntity<ApiFilterUserFlightShiftsResponse> filterUserFlightShifts(
            @RequestParam("shiftDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate shiftDate,
            @RequestParam(value = "teamId", required = false) Integer teamId,
            @RequestParam(value = "unitId", required = false) Integer unitId
    ) {
        List<UserFlightShiftResponseSearchDTO> dtos = userFlightShiftService.getFlightSchedulesByCriteria(shiftDate, teamId, unitId, null);
        ApiFilterUserFlightShiftsResponse response = new ApiFilterUserFlightShiftsResponse("Thành công", 200, dtos, true);
        return ResponseEntity.ok(response);
    }
}
