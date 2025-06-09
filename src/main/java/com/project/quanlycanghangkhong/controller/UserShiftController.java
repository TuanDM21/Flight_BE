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
import com.project.quanlycanghangkhong.dto.AssignShiftRequest;
import com.project.quanlycanghangkhong.dto.ScheduleDTO;
import com.project.quanlycanghangkhong.dto.UserShiftDTO;
import com.project.quanlycanghangkhong.dto.response.usershifts.ApiAllUserShiftsResponse;
import com.project.quanlycanghangkhong.dto.response.usershifts.ApiAssignShiftResponse;
import com.project.quanlycanghangkhong.dto.response.usershifts.ApiBatchAssignShiftResponse;
import com.project.quanlycanghangkhong.dto.response.usershifts.ApiDeleteUserShiftResponse;
import com.project.quanlycanghangkhong.dto.response.usershifts.ApiSchedulesResponse;
import com.project.quanlycanghangkhong.dto.response.usershifts.ApiUpdateUserShiftResponse;
import com.project.quanlycanghangkhong.dto.response.usershifts.ApiUserShiftByIdResponse;
import com.project.quanlycanghangkhong.dto.response.usershifts.ApiUsersOnDutyResponse;
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
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved all user shifts", content = @Content(schema = @Schema(implementation = ApiAllUserShiftsResponse.class)))
    })
    public ResponseEntity<ApiAllUserShiftsResponse> getAllUserShifts() {
        List<UserShiftDTO> dtos = userShiftService.getAllUserShifts();
        ApiAllUserShiftsResponse response = new ApiAllUserShiftsResponse();
        response.setMessage("Thành công");
        response.setStatusCode(200);
        response.setData(dtos);
        response.setSuccess(true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user shift by ID", description = "Retrieve a user shift by their ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved user shift", content = @Content(schema = @Schema(implementation = ApiUserShiftByIdResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User shift not found", content = @Content(schema = @Schema(implementation = ApiUserShiftByIdResponse.class)))
    })
    public ResponseEntity<ApiUserShiftByIdResponse> getUserShiftById(@PathVariable Integer id) {
        return userShiftService.getUserShiftById(id)
            .map(userShift -> {
                ApiUserShiftByIdResponse res = new ApiUserShiftByIdResponse();
                res.setMessage("Thành công");
                res.setStatusCode(200);
                res.setData(userShift);
                res.setSuccess(true);
                return ResponseEntity.ok(res);
            })
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiUserShiftByIdResponse("User shift not found", 404, null, false)));
    }

    // Endpoint gán ca trực cho 1 user (assign)
    @PostMapping("/assign")
    @Operation(summary = "Assign shift to user", description = "Assign a specific shift to a user on a given date")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Shift assigned successfully", content = @Content(schema = @Schema(implementation = ApiAssignShiftResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "User already has a shift on this date", content = @Content(schema = @Schema(implementation = ApiAssignShiftResponse.class)))
    })
    public ResponseEntity<ApiAssignShiftResponse> assignShiftToUser(@RequestBody AssignShiftRequest request) {
        try {
            LocalDate shiftDate = LocalDate.parse(request.getShiftDate());
            UserShiftDTO dto = userShiftService.assignShiftToUser(request.getUserId(), shiftDate, request.getShiftId());
            ApiAssignShiftResponse res = new ApiAssignShiftResponse();
            res.setMessage("Thành công");
            res.setStatusCode(200);
            res.setData(dto);
            res.setSuccess(true);
            return ResponseEntity.ok(res);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiAssignShiftResponse(ex.getMessage(), 409, null, false));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user shift", description = "Update an existing user shift")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User shift updated successfully", content = @Content(schema = @Schema(implementation = ApiUpdateUserShiftResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User shift not found", content = @Content(schema = @Schema(implementation = ApiUpdateUserShiftResponse.class)))
    })
    public ResponseEntity<ApiUpdateUserShiftResponse> updateUserShift(
            @PathVariable Integer id,
            @RequestBody AssignShiftRequest request) {
        try {
            // Chuyển đổi shiftDate từ String sang LocalDate
            LocalDate newShiftDate = LocalDate.parse(request.getShiftDate());
            UserShiftDTO dto = userShiftService.updateUserShift(id, request.getShiftId(), newShiftDate);
            if (dto != null) {
                ApiUpdateUserShiftResponse res = new ApiUpdateUserShiftResponse();
                res.setMessage("Thành công");
                res.setStatusCode(200);
                res.setData(dto);
                res.setSuccess(true);
                return ResponseEntity.ok(res);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiUpdateUserShiftResponse("User shift not found", 404, null, false));
            }
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiUpdateUserShiftResponse(ex.getMessage(), 409, null, false));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user shift", description = "Delete a user shift by ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "User shift deleted successfully", content = @Content(schema = @Schema(implementation = ApiDeleteUserShiftResponse.class)))
    })
    public ResponseEntity<ApiDeleteUserShiftResponse> deleteUserShift(@PathVariable Integer id) {
        userShiftService.deleteUserShift(id);
        ApiDeleteUserShiftResponse res = new ApiDeleteUserShiftResponse();
        res.setMessage("Thành công");
        res.setStatusCode(204);
        res.setData(null);
        res.setSuccess(true);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(res);
    }

    // Endpoint lọc lịch trực theo ngày, team và unit, trả về ScheduleDTO
    @GetMapping("/filter")
    @Operation(summary = "Get schedules by criteria", description = "Filter schedules by date, team, and unit")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully filtered schedules", content = @Content(schema = @Schema(implementation = ApiSchedulesResponse.class)))
    })
    public ResponseEntity<ApiSchedulesResponse> getSchedulesByCriteria(
            @RequestParam("shiftDate") String shiftDateStr,
            @RequestParam(value = "teamId", required = false) Integer teamId,
            @RequestParam(value = "unitId", required = false) Integer unitId) {
        LocalDate shiftDate = LocalDate.parse(shiftDateStr);
        List<ScheduleDTO> dtos = userShiftService.getSchedulesByCriteria(shiftDate, teamId, unitId);
        ApiSchedulesResponse res = new ApiSchedulesResponse();
        res.setMessage("Thành công");
        res.setStatusCode(200);
        res.setData(dtos);
        res.setSuccess(true);
        return ResponseEntity.ok(res);
    }

    // Endpoint áp dụng ca cho nhiều user (apply-multi)
    @PostMapping("/apply-multi")
    @Operation(summary = "Apply shift to multiple users", description = "Apply a shift to multiple users at once")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Shifts applied successfully", content = @Content(schema = @Schema(implementation = ApiBatchAssignShiftResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conflict in applying shifts", content = @Content(schema = @Schema(implementation = ApiBatchAssignShiftResponse.class)))
    })
    public ResponseEntity<ApiBatchAssignShiftResponse> applyShiftToUsers(@RequestBody ApplyShiftMultiDTO dto) {
        try {
            List<UserShiftDTO> result = userShiftService.applyShiftToUsers(dto);
            ApiBatchAssignShiftResponse res = new ApiBatchAssignShiftResponse();
            res.setMessage("Thành công");
            res.setStatusCode(200);
            res.setData(result);
            res.setSuccess(true);
            return ResponseEntity.ok(res);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiBatchAssignShiftResponse(ex.getMessage(), 409, null, false));
        }
    }

    @GetMapping("/filter-by-user-and-range")
    @Operation(summary = "Filter schedules by user and date range", description = "Get schedules for a specific user within a date range")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully filtered schedules by user and date range", content = @Content(schema = @Schema(implementation = ApiSchedulesResponse.class)))
    })
    public ResponseEntity<ApiSchedulesResponse> filterByUserAndRange(
        @RequestParam("userId") Integer userId,
        @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String startDateStr,
        @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String endDateStr
    ) {
        // Chuyển đổi string sang LocalDate
        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);
        // Gọi service để lấy danh sách lịch trực theo user và khoảng ngày
        List<ScheduleDTO> dtos = userShiftService.getSchedulesByUserAndDateRange(userId, startDate, endDate);
        ApiSchedulesResponse res = new ApiSchedulesResponse();
        res.setMessage("Thành công");
        res.setStatusCode(200);
        res.setData(dtos);
        res.setSuccess(true);
        return ResponseEntity.ok(res);
    }

    // Endpoint lưu nhiều ca trực cùng lúc (batch)
    @PostMapping("/batch")
    @Operation(summary = "Assign multiple shifts", description = "Assign multiple shifts to users in batch")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Shifts assigned successfully", content = @Content(schema = @Schema(implementation = ApiBatchAssignShiftResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conflict in shift assignment", content = @Content(schema = @Schema(implementation = ApiBatchAssignShiftResponse.class)))
    })
    public ResponseEntity<ApiBatchAssignShiftResponse> saveUserShiftsBatch(@RequestBody List<AssignShiftRequest> userShifts) {
        try {
            List<UserShiftDTO> savedShifts = userShiftService.saveUserShiftsBatch(userShifts);
            ApiBatchAssignShiftResponse res = new ApiBatchAssignShiftResponse();
            res.setMessage("Thành công");
            res.setStatusCode(200);
            res.setData(savedShifts);
            res.setSuccess(true);
            return ResponseEntity.ok(res);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiBatchAssignShiftResponse(ex.getMessage(), 409, null, false));
        }
    }

    // Endpoint lấy danh sách userId trực chung theo ngày và actualTime
    @GetMapping("/on-duty")
    @Operation(summary = "Get users on duty", description = "Get list of user IDs who are on duty at specific date and time")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved users on duty", content = @Content(schema = @Schema(implementation = ApiUsersOnDutyResponse.class)))
    })
    public ResponseEntity<ApiUsersOnDutyResponse> getUsersOnDuty(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String time) {
        java.time.LocalTime actualTime = java.time.LocalTime.parse(time);
        List<Integer> userIds = userShiftService.getUserIdsOnDutyAtTime(date, actualTime);
        ApiUsersOnDutyResponse res = new ApiUsersOnDutyResponse();
        res.setMessage("Thành công");
        res.setStatusCode(200);
        res.setData(userIds);
        res.setSuccess(true);
        return ResponseEntity.ok(res);
    }

}
