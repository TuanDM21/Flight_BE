package com.project.quanlycanghangkhong.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.project.quanlycanghangkhong.request.FlightTimeUpdateRequest;
import com.project.quanlycanghangkhong.request.CreateFlightRequest;
import com.project.quanlycanghangkhong.request.UpdateFlightRequest;
import com.project.quanlycanghangkhong.model.Flight;
import com.project.quanlycanghangkhong.service.FlightService;
import com.project.quanlycanghangkhong.service.UserFlightShiftService;
import com.project.quanlycanghangkhong.service.UserShiftService;
import com.project.quanlycanghangkhong.service.NotificationService;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;

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
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved all flights", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<List<FlightDTO>>> getAllFlights() {
        List<FlightDTO> dtos = flightService.getAllFlights();
        return ResponseEntity.ok(ApiResponseCustom.success(dtos));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get flight by ID", description = "Retrieve a flight by its ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved flight", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Flight not found", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<FlightDTO>> getFlightById(@PathVariable Long id) {
        Optional<FlightDTO> dto = flightService.getFlightById(id);
        return dto.map(flight -> ResponseEntity.ok(ApiResponseCustom.success(flight)))
            .orElse(ResponseEntity.status(404).body(ApiResponseCustom.notFound("Flight not found")));
    }

    @PostMapping
    @Operation(summary = "Create flight", description = "Create a new flight")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Flight created successfully", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<FlightDTO>> createFlight(@RequestBody CreateFlightRequest request) {
        FlightDTO dto = flightService.createFlightFromRequest(request);
        return ResponseEntity.ok(ApiResponseCustom.created(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update flight", description = "Update an existing flight with validated request data")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Flight updated successfully", 
            content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", 
            description = "Flight not found", 
            content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "Invalid input data", 
            content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))
        )
    })
    public ResponseEntity<ApiResponseCustom<FlightDTO>> updateFlight(
            @PathVariable Long id, 
            @Valid @RequestBody UpdateFlightRequest request) {
        try {
            FlightDTO updatedDto = flightService.updateFlightFromRequest(id, request);
            return ResponseEntity.ok(ApiResponseCustom.updated(updatedDto));
        } catch (RuntimeException ex) {
            String errorMessage = ex.getMessage();
            if (errorMessage.contains("Flight not found")) {
                return ResponseEntity.status(404).body(ApiResponseCustom.notFound("Không tìm thấy chuyến bay"));
            } else if (errorMessage.contains("airport not found")) {
                return ResponseEntity.status(400).body(ApiResponseCustom.error("Sân bay không hợp lệ: " + errorMessage));
            } else {
                return ResponseEntity.status(400).body(ApiResponseCustom.error("Dữ liệu không hợp lệ: " + errorMessage));
            }
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete flight", description = "Delete a flight by ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Flight deleted successfully", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<Void>> deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
        return ResponseEntity.status(204).body(ApiResponseCustom.deleted());
    }

    @GetMapping("/search")
    @Operation(summary = "Search flights", description = "Search flights by keyword")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully searched flights", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<List<FlightDTO>>> searchFlights(
            @RequestParam(value = "keyword", required = false) String keyword) {
        List<FlightDTO> dtos = flightService.searchFlights(keyword);
        return ResponseEntity.ok(ApiResponseCustom.success(dtos));
    }

    @GetMapping("/today")
    @Operation(summary = "Get today flights", description = "Retrieve flights for today")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved today flights", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<List<FlightDTO>>> getTodayFlights() {
        List<FlightDTO> dtos = flightService.getTodayFlights();  
        if (!dtos.isEmpty()) {
            logger.info("Today flights: {}", dtos.get(0).getDepartureAirport());
        } else {
            logger.info("Today flights: empty list");
        }
        return ResponseEntity.ok(ApiResponseCustom.success(dtos));
    }

    // Endpoint tìm kiếm chuyến bay theo ngày (đúng hoàn toàn)
    @GetMapping("/searchByDate")
    @Operation(summary = "Search flights by exact date", description = "Search flights by exact date (YYYY-MM-DD format)")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved flights by date", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid date format", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<List<FlightDTO>>> searchFlightByDate(@RequestParam("date") String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr); // format YYYY-MM-DD
            List<FlightDTO> dtos = flightService.getFlightsByExactDate(date);
            return ResponseEntity.ok(ApiResponseCustom.success(dtos));
        } catch (DateTimeParseException ex) {
            return ResponseEntity.badRequest().body(ApiResponseCustom.error("Invalid date format"));
        }
    }

    @GetMapping("/searchByDateAndKeyword")
    @Operation(summary = "Search flights by date and keyword", description = "Search flights by date and optional keyword")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved flights by date and keyword", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid date format", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<List<FlightDTO>>> searchFlightByDateAndKeyword(
            @RequestParam("date") String dateStr,
            @RequestParam(value = "keyword", required = false) String keyword) {
        try {
            LocalDate date = LocalDate.parse(dateStr); // format YYYY-MM-DD
            List<FlightDTO> dtos = flightService.getFlightsByDateAndKeyword(date, keyword);
            return ResponseEntity.ok(ApiResponseCustom.success(dtos));
        } catch (DateTimeParseException ex) {
            return ResponseEntity.badRequest().body(ApiResponseCustom.error("Invalid date format"));
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
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved live tracking flights", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<List<FlightDTO>>> getLiveTrackingGroup() {
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
        
        return ResponseEntity.ok(ApiResponseCustom.success(result));
    }

    @GetMapping("/searchByCriteria")
    @Operation(summary = "Search flights by multiple criteria", description = "Search flights by date (YYYY-MM-DD format), flight number, departure airport, and arrival airport. All parameters are optional.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved flights by criteria", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid date format or parameters", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<List<FlightDTO>>> searchFlightsByCriteria(
            @RequestParam(value = "date", required = false) String dateStr,
            @RequestParam(value = "flightNumber", required = false) String flightNumber,
            @RequestParam(value = "departureAirport", required = false) String departureAirport,
            @RequestParam(value = "arrivalAirport", required = false) String arrivalAirport) {
        
        // 🔍 Debug logging - Controller
        System.out.println("=== FLIGHT SEARCH CRITERIA DEBUG ===");
        System.out.println("📅 Date: " + dateStr);
        System.out.println("✈️ Flight Number: " + flightNumber);
        System.out.println("🛫 Departure Airport: " + departureAirport);
        System.out.println("🛬 Arrival Airport: " + arrivalAirport);
        System.out.println("=====================================");
        
        try {
            List<FlightDTO> dtos = flightService.searchFlightsByCriteria(dateStr, flightNumber, departureAirport, arrivalAirport);
            
            // 🔍 Debug result
            System.out.println("📊 Results found: " + (dtos != null ? dtos.size() : "NULL"));
            if (dtos != null && !dtos.isEmpty()) {
                System.out.println("🎯 First result: " + dtos.get(0).getFlightNumber() + " - " + dtos.get(0).getFlightDate());
            }
            
            return ResponseEntity.ok(ApiResponseCustom.success(dtos));
        } catch (DateTimeParseException ex) {
            System.err.println("❌ Date parse error: " + ex.getMessage());
            return ResponseEntity.badRequest().body(ApiResponseCustom.error("Invalid date format"));
        } catch (Exception ex) {
            System.err.println("❌ General error: " + ex.getMessage());
            ex.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponseCustom.error("Error: " + ex.getMessage()));
        }
    }

    @GetMapping("/debug/airports")
    @Operation(summary = "Debug - Check airports in database", description = "Debug endpoint to check all airports and search for specific codes")
    public ResponseEntity<?> debugAirports(
            @RequestParam(value = "search", required = false) String search) {
        
        try {
            System.out.println("=== DEBUG: CHECKING AIRPORTS ===");
            
            // Get all airports from database
            List<com.project.quanlycanghangkhong.model.Airport> allAirports = 
                ((com.project.quanlycanghangkhong.repository.AirportRepository) 
                 flightService.getClass().getDeclaredField("airportRepository").get(flightService))
                .findAll();
                
            System.out.println("📊 Total airports in database: " + allAirports.size());
            
            if (!allAirports.isEmpty()) {
                System.out.println("🎯 All airports:");
                for (com.project.quanlycanghangkhong.model.Airport airport : allAirports) {
                    System.out.println("   [" + airport.getId() + "] " + airport.getAirportCode() + " - " + airport.getAirportName());
                }
            }
            
            // Search for specific airport if provided
            if (search != null && !search.trim().isEmpty()) {
                System.out.println("🔍 Searching for airports containing: " + search);
                List<com.project.quanlycanghangkhong.model.Airport> matchingAirports = allAirports.stream()
                    .filter(a -> a.getAirportCode().toLowerCase().contains(search.toLowerCase()) ||
                               a.getAirportName().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
                System.out.println("📅 Matching airports: " + matchingAirports.size());
                
                return ResponseEntity.ok(Map.of(
                    "totalAirports", allAirports.size(),
                    "searchTerm", search,
                    "matchingAirports", matchingAirports.stream()
                        .map(a -> Map.of("id", a.getId(), "code", a.getAirportCode(), "name", a.getAirportName()))
                        .collect(Collectors.toList()),
                    "allAirports", allAirports.stream()
                        .map(a -> Map.of("id", a.getId(), "code", a.getAirportCode(), "name", a.getAirportName()))
                        .collect(Collectors.toList())
                ));
            }
            
            return ResponseEntity.ok(Map.of(
                "totalAirports", allAirports.size(),
                "airports", allAirports.stream()
                    .map(a -> Map.of("id", a.getId(), "code", a.getAirportCode(), "name", a.getAirportName()))
                    .collect(Collectors.toList())
            ));
            
        } catch (Exception e) {
            System.err.println("❌ Error checking airports: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage(),
                "message", "Failed to check airports"
            ));
        }
    }
}
