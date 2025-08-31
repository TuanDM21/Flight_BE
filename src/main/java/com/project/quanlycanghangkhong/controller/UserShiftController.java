package com.project.quanlycanghangkhong.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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

import com.project.quanlycanghangkhong.dto.ApplyShiftMultiDTO;
import com.project.quanlycanghangkhong.request.AssignShiftRequest;
import com.project.quanlycanghangkhong.dto.ScheduleDTO;
import com.project.quanlycanghangkhong.dto.UserShiftDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import com.project.quanlycanghangkhong.service.UserShiftService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/user-shifts")
public class UserShiftController {

    @Autowired
    private UserShiftService userShiftService;

    @GetMapping
    @Operation(summary = "Get all user shifts", description = "Retrieve all user shift assignments")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved all user shifts", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<List<UserShiftDTO>>> getAllUserShifts() {
        List<UserShiftDTO> dtos = userShiftService.getAllUserShifts();
        // 🎯 SAME BUSINESS LOGIC - giữ nguyên message và logic
        return ResponseEntity.ok(ApiResponseCustom.success("Thành công", dtos));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user shift by ID", description = "Retrieve a user shift by their ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved user shift", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User shift not found", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<UserShiftDTO>> getUserShiftById(@PathVariable Integer id) {
        return userShiftService.getUserShiftById(id)
            .map(userShift -> {
                // 🎯 SAME BUSINESS LOGIC - giữ nguyên message "Thành công"
                return ResponseEntity.ok(ApiResponseCustom.success("Thành công", userShift));
            })
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseCustom.notFound("User shift not found")));
    }

    // Endpoint gán ca trực cho 1 user (assign)
    @PostMapping("/assign")
    @Operation(summary = "Assign shift to user", description = "Assign a specific shift to a user on a given date")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Shift assigned successfully", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "User already has a shift on this date", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<UserShiftDTO>> assignShiftToUser(@RequestBody AssignShiftRequest request) {
        try {
            LocalDate shiftDate = LocalDate.parse(request.getShiftDate());
            UserShiftDTO dto = userShiftService.assignShiftToUser(request.getUserId(), shiftDate, request.getShiftId());
            // 🎯 SAME BUSINESS LOGIC - giữ nguyên message "Thành công"
            return ResponseEntity.ok(ApiResponseCustom.success("Thành công", dto));
        } catch (RuntimeException ex) {
            // 🎯 SAME BUSINESS LOGIC - giữ nguyên 409 status và error message
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponseCustom.error(HttpStatus.CONFLICT, ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user shift", description = "Update an existing user shift")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User shift updated successfully", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User shift not found", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<UserShiftDTO>> updateUserShift(
            @PathVariable Integer id,
            @RequestBody AssignShiftRequest request) {
        try {
            // Chuyển đổi shiftDate từ String sang LocalDate
            LocalDate newShiftDate = LocalDate.parse(request.getShiftDate());
            UserShiftDTO dto = userShiftService.updateUserShift(id, request.getShiftId(), newShiftDate);
            if (dto != null) {
                return ResponseEntity.ok(ApiResponseCustom.success("Thành công", dto));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseCustom.notFound("User shift not found"));
            }
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponseCustom.error(ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user shift", description = "Delete a user shift by ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "User shift deleted successfully", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<Void>> deleteUserShift(@PathVariable Integer id) {
        userShiftService.deleteUserShift(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponseCustom.deleted());
    }

    // Endpoint lọc lịch trực theo ngày, team và unit, trả về ScheduleDTO
    @GetMapping("/filter")
    @Operation(summary = "Get schedules by criteria", description = "Filter schedules by date, team, and unit")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully filtered schedules", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<List<ScheduleDTO>>> getSchedulesByCriteria(
            @RequestParam("shiftDate") String shiftDateStr,
            @RequestParam(value = "teamId", required = false) Integer teamId,
            @RequestParam(value = "unitId", required = false) Integer unitId) {
        LocalDate shiftDate = LocalDate.parse(shiftDateStr);
        List<ScheduleDTO> dtos = userShiftService.getSchedulesByCriteria(shiftDate, teamId, unitId);
        return ResponseEntity.ok(ApiResponseCustom.success("Thành công", dtos));
    }

    // Endpoint áp dụng ca cho nhiều user (apply-multi)
    @PostMapping("/apply-multi")
    @Operation(summary = "Apply shift to multiple users", description = "Apply a shift to multiple users at once")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Shifts applied successfully", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conflict in applying shifts", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<List<UserShiftDTO>>> applyShiftToUsers(@RequestBody ApplyShiftMultiDTO dto) {
        try {
            List<UserShiftDTO> result = userShiftService.applyShiftToUsers(dto);
            return ResponseEntity.ok(ApiResponseCustom.success("Thành công", result));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponseCustom.error(ex.getMessage()));
        }
    }

    @GetMapping("/filter-by-user-and-range")
    @Operation(summary = "Filter schedules by user and date range", description = "Get schedules for a specific user within a date range")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully filtered schedules by user and date range", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<List<ScheduleDTO>>> filterByUserAndRange(
        @RequestParam("userId") Integer userId,
        @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String startDateStr,
        @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String endDateStr
    ) {
        // Chuyển đổi string sang LocalDate
        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);
        // Gọi service để lấy danh sách lịch trực theo user và khoảng ngày
        List<ScheduleDTO> dtos = userShiftService.getSchedulesByUserAndDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(ApiResponseCustom.success("Thành công", dtos));
    }

    // Endpoint lưu nhiều ca trực cùng lúc (batch)
    @PostMapping("/batch")
    @Operation(summary = "Assign multiple shifts", description = "Assign multiple shifts to users in batch")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Shifts assigned successfully", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conflict in shift assignment", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<List<UserShiftDTO>>> saveUserShiftsBatch(@RequestBody List<AssignShiftRequest> userShifts) {
        try {
            List<UserShiftDTO> savedShifts = userShiftService.saveUserShiftsBatch(userShifts);
            return ResponseEntity.ok(ApiResponseCustom.success("Thành công", savedShifts));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponseCustom.error(ex.getMessage()));
        }
    }

    // Endpoint lấy danh sách userId trực chung theo ngày và actualTime
    @GetMapping("/on-duty")
    @Operation(summary = "Get users on duty", description = "Get list of user IDs who are on duty at specific date and time")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved users on duty", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<List<Integer>>> getUsersOnDuty(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String time) {
        java.time.LocalTime actualTime = java.time.LocalTime.parse(time);
        List<Integer> userIds = userShiftService.getUserIdsOnDutyAtTime(date, actualTime);
        return ResponseEntity.ok(ApiResponseCustom.success("Thành công", userIds));
    }

    @GetMapping("/my-shifts")
    @Operation(summary = "Get current user's shifts", description = "Get shifts for the currently authenticated user")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved user's shifts", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponseCustom<List<UserShiftDTO>>> getMyShifts() {
        try {
            List<UserShiftDTO> myShifts = userShiftService.getMyShifts();
            return ResponseEntity.ok(ApiResponseCustom.success("Thành công", myShifts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseCustom.internalError("Không thể lấy ca trực: " + e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user shifts by user ID", description = "Get all shifts for a specific user")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved user's shifts", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponseCustom<List<UserShiftDTO>>> getUserShiftsByUserId(@PathVariable Integer userId) {
        try {
            List<UserShiftDTO> userShifts = userShiftService.getShiftsByUserId(userId);
            return ResponseEntity.ok(ApiResponseCustom.success("Thành công", userShifts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseCustom.internalError("Không thể lấy ca trực cho user: " + e.getMessage()));
        }
    }

}
