package com.project.quanlycanghangkhong.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.quanlycanghangkhong.dto.FlightDTO;
import com.project.quanlycanghangkhong.dto.FlightTimeUpdateRequest;
import com.project.quanlycanghangkhong.dto.CreateFlightRequest;
import com.project.quanlycanghangkhong.dto.UpdateFlightRequest;
import com.project.quanlycanghangkhong.model.Flight;
import com.project.quanlycanghangkhong.service.FlightService;
import com.project.quanlycanghangkhong.service.UserFlightShiftService;
import com.project.quanlycanghangkhong.service.UserShiftService;
import com.project.quanlycanghangkhong.service.NotificationService;
import com.project.quanlycanghangkhong.dto.response.flights.ApiAllFlightsResponse;
import com.project.quanlycanghangkhong.dto.response.flights.ApiFlightByIdResponse;
import com.project.quanlycanghangkhong.dto.response.flights.ApiCreateFlightResponse;
import com.project.quanlycanghangkhong.dto.response.flights.ApiUpdateFlightResponse;
import com.project.quanlycanghangkhong.dto.response.flights.ApiDeleteFlightResponse;
import com.project.quanlycanghangkhong.dto.response.flights.ApiSearchFlightsResponse;
import com.project.quanlycanghangkhong.dto.response.ApiErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/flights")
public class FlightController {
    private static final Logger logger = LoggerFactory.getLogger(FlightController.class);

    @Autowired
    private FlightService flightService;

    @Autowired
    private UserFlightShiftService userFlightShiftService;

    @Autowired
    private UserShiftService userShiftService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    @Operation(summary = "Get all flights", description = "Retrieve a list of all flights")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved all flights", content = @Content(schema = @Schema(implementation = ApiAllFlightsResponse.class)))
    })
    public ResponseEntity<ApiAllFlightsResponse> getAllFlights() {
        List<FlightDTO> dtos = flightService.getAllFlights();
        ApiAllFlightsResponse response = new ApiAllFlightsResponse();
        response.setMessage("Thành công");
        response.setStatusCode(200);
        response.setData(dtos);
        response.setSuccess(true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get flight by ID", description = "Retrieve a flight by its ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved flight", content = @Content(schema = @Schema(implementation = ApiFlightByIdResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Flight not found", content = @Content(schema = @Schema(implementation = ApiFlightByIdResponse.class)))
    })
    public ResponseEntity<ApiFlightByIdResponse> getFlightById(@PathVariable Long id) {
        Optional<FlightDTO> dto = flightService.getFlightById(id);
        return dto.map(flight -> {
            ApiFlightByIdResponse res = new ApiFlightByIdResponse();
            res.setMessage("Thành công");
            res.setStatusCode(200);
            res.setData(flight);
            res.setSuccess(true);
            return ResponseEntity.ok(res);
        }).orElse(ResponseEntity.status(404)
            .body(new ApiFlightByIdResponse("Flight not found", 404, null, false)));
    }

    @PostMapping
    @Operation(summary = "Create flight", description = "Create a new flight")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Flight created successfully", content = @Content(schema = @Schema(implementation = ApiCreateFlightResponse.class)))
    })
    public ResponseEntity<ApiCreateFlightResponse> createFlight(@RequestBody CreateFlightRequest request) {
        FlightDTO dto = flightService.createFlightFromRequest(request);
        ApiCreateFlightResponse res = new ApiCreateFlightResponse();
        res.setMessage("Thành công");
        res.setStatusCode(200);
        res.setData(dto);
        res.setSuccess(true);
        return ResponseEntity.ok(res);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update flight", description = "Update an existing flight with validated request data")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Flight updated successfully", 
            content = @Content(schema = @Schema(implementation = ApiUpdateFlightResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", 
            description = "Flight not found", 
            content = @Content(schema = @Schema(implementation = ApiUpdateFlightResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "Invalid input data", 
            content = @Content(schema = @Schema(implementation = ApiUpdateFlightResponse.class))
        )
    })
    public ResponseEntity<ApiUpdateFlightResponse> updateFlight(
            @PathVariable Long id, 
            @Valid @RequestBody UpdateFlightRequest request) {
        try {
            FlightDTO updatedDto = flightService.updateFlightFromRequest(id, request);
            ApiUpdateFlightResponse res = new ApiUpdateFlightResponse();
            res.setMessage("Cập nhật chuyến bay thành công");
            res.setStatusCode(200);
            res.setData(updatedDto);
            res.setSuccess(true);
            return ResponseEntity.ok(res);
        } catch (RuntimeException ex) {
            String errorMessage = ex.getMessage();
            if (errorMessage.contains("Flight not found")) {
                return ResponseEntity.status(404)
                    .body(new ApiUpdateFlightResponse("Không tìm thấy chuyến bay", 404, null, false));
            } else if (errorMessage.contains("airport not found")) {
                return ResponseEntity.status(400)
                    .body(new ApiUpdateFlightResponse("Sân bay không hợp lệ: " + errorMessage, 400, null, false));
            } else {
                return ResponseEntity.status(400)
                    .body(new ApiUpdateFlightResponse("Dữ liệu không hợp lệ: " + errorMessage, 400, null, false));
            }
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete flight", description = "Delete a flight by ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Flight deleted successfully", content = @Content(schema = @Schema(implementation = ApiDeleteFlightResponse.class)))
    })
    public ResponseEntity<ApiDeleteFlightResponse> deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
        ApiDeleteFlightResponse res = new ApiDeleteFlightResponse();
        res.setMessage("Thành công");
        res.setStatusCode(204);
        res.setData(null);
        res.setSuccess(true);
        return ResponseEntity.status(204).body(res);
    }

    @GetMapping("/search")
    @Operation(summary = "Search flights", description = "Search flights by keyword")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully searched flights", content = @Content(schema = @Schema(implementation = ApiSearchFlightsResponse.class)))
    })
    public ResponseEntity<ApiSearchFlightsResponse> searchFlights(
            @RequestParam(value = "keyword", required = false) String keyword) {
        List<FlightDTO> dtos = flightService.searchFlights(keyword);
        ApiSearchFlightsResponse res = new ApiSearchFlightsResponse();
        res.setMessage("Thành công");
        res.setStatusCode(200);
        res.setData(dtos);
        res.setSuccess(true);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/today")
    @Operation(summary = "Get today flights", description = "Retrieve flights for today")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved today flights", content = @Content(schema = @Schema(implementation = ApiSearchFlightsResponse.class)))
    })
    public ResponseEntity<ApiSearchFlightsResponse> getTodayFlights() {
        List<FlightDTO> dtos = flightService.getTodayFlights();  
        if (!dtos.isEmpty()) {
            logger.info("Today flights: {}", dtos.get(0).getDepartureAirport());
        } else {
            logger.info("Today flights: empty list");
        }
        ApiSearchFlightsResponse res = new ApiSearchFlightsResponse();
        res.setMessage("Thành công");
        res.setStatusCode(200);
        res.setData(dtos);
        res.setSuccess(true);
        return ResponseEntity.ok(res);
    }

    // Endpoint tìm kiếm chuyến bay theo ngày (đúng hoàn toàn)
    @GetMapping("/searchByDate")
    @Operation(summary = "Search flights by exact date", description = "Search flights by exact date (YYYY-MM-DD format)")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved flights by date", content = @Content(schema = @Schema(implementation = ApiSearchFlightsResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid date format", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<ApiSearchFlightsResponse> searchFlightByDate(@RequestParam("date") String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr); // format YYYY-MM-DD
            List<FlightDTO> dtos = flightService.getFlightsByExactDate(date);
            ApiSearchFlightsResponse res = new ApiSearchFlightsResponse();
            res.setMessage("Thành công");
            res.setStatusCode(200);
            res.setData(dtos);
            res.setSuccess(true);
            return ResponseEntity.ok(res);
        } catch (DateTimeParseException ex) {
            return ResponseEntity.badRequest()
                .body(new ApiSearchFlightsResponse("Invalid date format", 400, null, false));
        }
    }

    @GetMapping("/searchByDateAndKeyword")
    @Operation(summary = "Search flights by date and keyword", description = "Search flights by date and optional keyword")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved flights by date and keyword", content = @Content(schema = @Schema(implementation = ApiSearchFlightsResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid date format", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<ApiSearchFlightsResponse> searchFlightByDateAndKeyword(
            @RequestParam("date") String dateStr,
            @RequestParam(value = "keyword", required = false) String keyword) {
        try {
            LocalDate date = LocalDate.parse(dateStr); // format YYYY-MM-DD
            List<FlightDTO> dtos = flightService.getFlightsByDateAndKeyword(date, keyword);
            ApiSearchFlightsResponse res = new ApiSearchFlightsResponse();
            res.setMessage("Thành công");
            res.setStatusCode(200);
            res.setData(dtos);
            res.setSuccess(true);
            return ResponseEntity.ok(res);
        } catch (DateTimeParseException ex) {
            return ResponseEntity.badRequest()
                .body(new ApiSearchFlightsResponse("Invalid date format", 400, null, false));
        }
    }

    @PatchMapping("/{id}/times")
    @Operation(summary = "Update flight times", description = "Update flight departure/arrival times")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Flight times updated successfully")
    })
    public ResponseEntity<?> updateFlightTimes(
            @PathVariable Long id,
            @RequestBody FlightTimeUpdateRequest payload) {
        flightService.updateFlightTimes(id, payload);
        // trả wrapper hoặc plain text tuỳ bạn
        return ResponseEntity.ok(Map.of("success", true, "message", "Thành công"));
    }

    @PatchMapping("/{id}/actual-time-notify")
    @Operation(summary = "Update actual time and notify", description = "Update actual flight time and send notifications to relevant users")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Actual time updated and notifications sent"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request or flight not found")
    })
    public ResponseEntity<?> updateActualTimeAndNotify(
            @PathVariable Long id,
            @RequestBody FlightTimeUpdateRequest payload) {
        // Cập nhật actual time
        flightService.updateFlightTimes(id, payload);
        Flight flight = flightService.getFlightEntityById(id);
        if (flight == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Không tìm thấy chuyến bay"));
        }
        // Lấy actual time vừa nhập
        LocalTime actualTime = null;
        String eventType = payload.getEventType();
        if (payload.getActualDepartureTimeAtArrival() != null) {
            actualTime = LocalTime.parse(payload.getActualDepartureTimeAtArrival());
            if (eventType == null || eventType.isEmpty()) eventType = "actualDepartureTimeAtArrival";
        } else if (payload.getActualArrivalTime() != null) {
            actualTime = LocalTime.parse(payload.getActualArrivalTime());
            if (eventType == null || eventType.isEmpty()) eventType = "actualArrivalTime";
        }
        if (actualTime == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Chưa nhập giờ thực tế"));
        }
        // Lấy ngày chuyến bay
        LocalDate flightDate = flight.getFlightDate();
        // Lấy userId phục vụ chuyến bay
        Set<Integer> userIds = new HashSet<>(userFlightShiftService.getUserIdsByFlightAndDate(id, flightDate));
        // Lấy userId trực chung, lọc theo actual time
        userIds.addAll(userShiftService.getUserIdsOnDutyAtTime(flightDate, actualTime));
        // Gửi notification
        String title = "Thông báo chuyến bay " + flight.getFlightNumber() ; 
        String content;
        if ("actualArrivalTime".equals(eventType)) {
            content = "Hạ cánh thực tế tại " + (flight.getArrivalAirport() != null ? flight.getArrivalAirport().getAirportCode() : "?") + ": " + actualTime;
        } else if ("actualDepartureTimeAtArrival".equals(eventType)) {
            content = "Cất cánh thực tế tại " + (flight.getArrivalAirport() != null ? flight.getArrivalAirport().getAirportCode() : "?") + ": " + actualTime;
        } else {
            content = "Chuyến bay " + flight.getFlightNumber() + " đã cập nhật giờ thực tế: " + actualTime;
        }
        notificationService.createNotifications(
            userIds.stream().toList(),
            "FLIGHT",
            title,
            content,
            id.intValue(),
            false
        );
        return ResponseEntity.ok(Map.of("success", true, "message", "Đã gửi notification cho " + userIds.size() + " nhân viên."));
    }

    @GetMapping("/live-tracking-group")
    @Operation(summary = "Get live tracking flights", description = "Get flights for live tracking (today + yesterday with specific conditions)")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved live tracking flights", content = @Content(schema = @Schema(implementation = ApiSearchFlightsResponse.class)))
    })
    public ResponseEntity<ApiSearchFlightsResponse> getLiveTrackingGroup() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        List<FlightDTO> allToday = flightService.getFlightsByExactDate(today);
        List<FlightDTO> allYesterday = flightService.getFlightsByExactDate(yesterday);
        // Hôm nay: chỉ cần 1 trong 2 trường actualDepartureTime hoặc actualArrivalTime có data
        List<FlightDTO> todayFiltered = allToday.stream()
            .filter(f -> f.getActualDepartureTime() != null || f.getActualArrivalTime() != null)
            .toList();
        // Hôm qua: actualDepartureTimeAtArrival == null và cả 2 trường actualDepartureTime, actualArrivalTime đều có data
        List<FlightDTO> yesterdayFiltered = allYesterday.stream()
            .filter(f -> f.getActualDepartureTimeAtArrival() == null
                      && f.getActualDepartureTime() != null
                      && f.getActualArrivalTime() != null)
            .toList();
        List<FlightDTO> result = new java.util.ArrayList<>();
        result.addAll(todayFiltered);
        result.addAll(yesterdayFiltered);
        
        ApiSearchFlightsResponse res = new ApiSearchFlightsResponse();
        res.setMessage("Thành công");
        res.setStatusCode(200);
        res.setData(result);
        res.setSuccess(true);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/searchByCriteria")
    @Operation(summary = "Search flights by date and flight number", description = "Search flights by date (YYYY-MM-DD format) and flight number. Both parameters are optional.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved flights by criteria", content = @Content(schema = @Schema(implementation = ApiSearchFlightsResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid date format or parameters", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<ApiSearchFlightsResponse> searchFlightsByCriteria(
            @RequestParam(value = "date", required = false) String dateStr,
            @RequestParam(value = "flightNumber", required = false) String flightNumber) {
        
        // 🔍 Debug logging - Controller
        System.out.println("=== FLIGHT SEARCH CRITERIA DEBUG ===");
        System.out.println("📅 Date: " + dateStr);
        System.out.println("✈️ Flight Number: " + flightNumber);
        System.out.println("=====================================");
        
        try {
            List<FlightDTO> dtos = flightService.searchFlightsByCriteria(dateStr, flightNumber);
            
            // 🔍 Debug result
            System.out.println("📊 Results found: " + (dtos != null ? dtos.size() : "NULL"));
            if (dtos != null && !dtos.isEmpty()) {
                System.out.println("🎯 First result: " + dtos.get(0).getFlightNumber() + " - " + dtos.get(0).getFlightDate());
            }
            
            ApiSearchFlightsResponse res = new ApiSearchFlightsResponse();
            res.setMessage("Thành công");
            res.setStatusCode(200);
            res.setData(dtos);
            res.setSuccess(true);
            return ResponseEntity.ok(res);
        } catch (DateTimeParseException ex) {
            System.err.println("❌ Date parse error: " + ex.getMessage());
            return ResponseEntity.badRequest()
                .body(new ApiSearchFlightsResponse("Invalid date format", 400, null, false));
        } catch (Exception ex) {
            System.err.println("❌ General error: " + ex.getMessage());
            ex.printStackTrace();
            return ResponseEntity.badRequest()
                .body(new ApiSearchFlightsResponse("Error: " + ex.getMessage(), 400, null, false));
        }
    }
}
