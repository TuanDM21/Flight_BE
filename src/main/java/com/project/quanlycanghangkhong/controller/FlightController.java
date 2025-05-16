package com.project.quanlycanghangkhong.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

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
import com.project.quanlycanghangkhong.model.Flight;
import com.project.quanlycanghangkhong.service.FlightService;
import com.project.quanlycanghangkhong.service.UserFlightShiftService;
import com.project.quanlycanghangkhong.service.UserShiftService;
import com.project.quanlycanghangkhong.service.NotificationService;
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
        if (!dtos.isEmpty()) {
            logger.info("Today flights: {}", dtos.get(0).getDepartureAirport());
        } else {
            logger.info("Today flights: empty list");
        }
        return ResponseEntity.ok(dtos);
    }

    // Endpoint tìm kiếm chuyến bay theo ngày (đúng hoàn toàn)
    @GetMapping("/searchByDate")
    public ResponseEntity<List<FlightDTO>> searchFlightByDate(@RequestParam("date") String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr); // format YYYY-MM-DD
            List<FlightDTO> dtos = flightService.getFlightsByExactDate(date);
            return ResponseEntity.ok(dtos);
        } catch (DateTimeParseException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/searchByDateAndKeyword")
    public ResponseEntity<List<FlightDTO>> searchFlightByDateAndKeyword(
            @RequestParam("date") String dateStr,
            @RequestParam(value = "keyword", required = false) String keyword) {
        try {
            LocalDate date = LocalDate.parse(dateStr); // format YYYY-MM-DD
            List<FlightDTO> dtos = flightService.getFlightsByDateAndKeyword(date, keyword);
            return ResponseEntity.ok(dtos);
        } catch (DateTimeParseException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/times")
    public ResponseEntity<?> updateFlightTimes(
            @PathVariable Long id,
            @RequestBody FlightTimeUpdateRequest payload) {
        flightService.updateFlightTimes(id, payload);
        // trả wrapper hoặc plain text tuỳ bạn
        return ResponseEntity.ok(Map.of("success", true, "message", "Thành công"));
    }

    @PatchMapping("/{id}/actual-time-notify")
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
    public ResponseEntity<List<FlightDTO>> getLiveTrackingGroup() {
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
        return ResponseEntity.ok(result);
    }
}
