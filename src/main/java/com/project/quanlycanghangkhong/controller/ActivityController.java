package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.dto.ActivityDTO;
import com.project.quanlycanghangkhong.dto.ActivityParticipantDTO;
import com.project.quanlycanghangkhong.dto.CalendarDTO;
import com.project.quanlycanghangkhong.dto.request.ActivityRequest;
import com.project.quanlycanghangkhong.dto.request.ActivityParticipantRequest;
import com.project.quanlycanghangkhong.dto.request.ParticipantDeleteRequest;
import com.project.quanlycanghangkhong.dto.request.ParticipantDeleteRequest;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import com.project.quanlycanghangkhong.dto.response.activity.ActivityApiResponse;
import com.project.quanlycanghangkhong.dto.response.activity.ActivityListApiResponse;
import com.project.quanlycanghangkhong.dto.response.activity.ActivityParticipantsApiResponse;
import com.project.quanlycanghangkhong.model.User;
import com.project.quanlycanghangkhong.repository.UserRepository;
import com.project.quanlycanghangkhong.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/activities")
@CrossOrigin(origins = "*")
@Tag(name = "Activity Management", description = "APIs for managing activities and events")
public class ActivityController {
    private static final Logger logger = LoggerFactory.getLogger(ActivityController.class);
    @Autowired
    private ActivityService activityService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    @Operation(summary = "Tạo hoạt động mới", description = "Tạo mới một hoạt động/sự kiện")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo thành công", 
                content = @Content(schema = @Schema(implementation = ActivityApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", 
                content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<ActivityDTO>> createActivity(@Valid @RequestBody ActivityRequest request) {
        ActivityDTO dto = convertToActivityDTO(request);
        ActivityDTO created = activityService.createActivity(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseCustom.created(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật hoạt động", description = "Cập nhật thông tin hoạt động theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công", 
                content = @Content(schema = @Schema(implementation = ActivityApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy hoạt động", 
                content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", 
                content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<ActivityDTO>> updateActivity(
            @Parameter(description = "ID của hoạt động", required = true) @PathVariable Long id, 
            @Valid @RequestBody ActivityRequest request) {
        ActivityDTO dto = convertToActivityDTO(request);
        ActivityDTO updated = activityService.updateActivity(id, dto);
        return ResponseEntity.ok(ApiResponseCustom.updated(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa hoạt động", description = "Xóa hoạt động theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Xóa thành công", 
                content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy hoạt động", 
                content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<Void>> deleteActivity(
            @Parameter(description = "ID của hoạt động", required = true) @PathVariable Long id) {
        activityService.deleteActivity(id);
        return ResponseEntity.ok(ApiResponseCustom.deleted());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết hoạt động", description = "Lấy thông tin chi tiết hoạt động theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                content = @Content(schema = @Schema(implementation = ActivityApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy hoạt động", 
                content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<ActivityDTO>> getActivity(
            @Parameter(description = "ID của hoạt động", required = true) @PathVariable Long id) {
        ActivityDTO activity = activityService.getActivity(id);
        return ResponseEntity.ok(ApiResponseCustom.success(activity));
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách hoạt động cho calendar", description = "Lấy danh sách hoạt động theo loại hoặc tìm kiếm theo từ khóa (tên, ghi chú, địa điểm) và người tham gia. Hỗ trợ lọc theo khoảng ngày cho calendar view. Nếu không truyền type parameter thì trả về calendar trống.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                content = @Content(schema = @Schema(implementation = ActivityListApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Tham số không hợp lệ", 
                content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
            @ApiResponse(responseCode = "401", description = "Không có quyền truy cập hoặc không tìm thấy người dùng", 
                content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<CalendarDTO>> getAllActivities(
            @Parameter(description = "Từ khóa tìm kiếm (tìm trong tên, ghi chú, địa điểm)", example = "Họp định kỳ") 
            @RequestParam(required = false) String keyword,
            @Parameter(description = "Loại người tham gia", example = "USER", schema = @Schema(allowableValues = {"USER", "TEAM", "UNIT"})) 
            @RequestParam(required = false) String participantType,
            @Parameter(description = "ID của người tham gia", example = "1") 
            @RequestParam(required = false) Long participantId,
            @Parameter(description = "Ngày bắt đầu để lọc (format: yyyy-MM-dd)", example = "2025-01-01") 
            @RequestParam(required = false) String startDate,
            @Parameter(description = "Ngày kết thúc để lọc (format: yyyy-MM-dd)", example = "2025-01-31") 
            @RequestParam(required = false) String endDate,
            @Parameter(description = "Loại hoạt động: 'company' (toàn công ty) hoặc 'my' (cá nhân). Nếu không truyền sẽ trả về empty list", 
                      example = "company", schema = @Schema(allowableValues = {"my", "company"}))
            @RequestParam(required = false) String type) {
        
        long requestStartTime = System.currentTimeMillis();

        // Validate date parameters
        java.time.LocalDateTime startDateTime = null;
        java.time.LocalDateTime endDateTime = null;
        
        if (startDate != null) {
            try {
                java.time.LocalDate localStartDate = java.time.LocalDate.parse(startDate);
                startDateTime = localStartDate.atStartOfDay(); // 00:00:00
            } catch (Exception e) {
                logger.warn("[GET /api/activities] Invalid startDate format: {}", startDate);
                return ResponseEntity.badRequest().body(ApiResponseCustom.error("Định dạng startDate không hợp lệ. Sử dụng format: yyyy-MM-dd"));
            }
        }
        
        if (endDate != null) {
            try {
                java.time.LocalDate localEndDate = java.time.LocalDate.parse(endDate);
                endDateTime = localEndDate.atTime(23, 59, 59); // 23:59:59
            } catch (Exception e) {
                logger.warn("[GET /api/activities] Invalid endDate format: {}", endDate);
                return ResponseEntity.badRequest().body(ApiResponseCustom.error("Định dạng endDate không hợp lệ. Sử dụng format: yyyy-MM-dd"));
            }
        }
        
        if (startDateTime != null && endDateTime != null && startDateTime.isAfter(endDateTime)) {
            return ResponseEntity.badRequest().body(ApiResponseCustom.error("Ngày bắt đầu không thể sau ngày kết thúc"));
        }

        // If searching by any field or date range, use search method
        if (keyword != null || (participantType != null && participantId != null) || 
            startDateTime != null || endDateTime != null) {
            List<ActivityDTO> activities = activityService.searchActivities(keyword, participantType, participantId, startDateTime, endDateTime);
            CalendarDTO calendar = CalendarDTO.builder()
                .currentDate(LocalDate.now())
                .activities(activities)
                .metadata(CalendarDTO.CalendarMetadata.builder()
                    .totalActivities(activities.size())
                    .viewType("search")
                    .message(activities.isEmpty() ? "Không tìm thấy hoạt động nào phù hợp" : "Kết quả tìm kiếm")
                    .build())
                .build();
            return ResponseEntity.ok(ApiResponseCustom.success(calendar));
        }

        // If no type parameter is provided, return empty calendar
        if (type == null || type.trim().isEmpty()) {
            logger.info("[GET /api/activities] No type parameter provided, returning empty calendar");
            CalendarDTO emptyCalendar = CalendarDTO.builder()
                .currentDate(LocalDate.now())
                .activities(new ArrayList<>())
                .metadata(CalendarDTO.CalendarMetadata.builder()
                    .totalActivities(0)
                    .viewType("empty")
                    .message("Lịch trống - chọn loại hoạt động để xem")
                    .build())
                .build();
            return ResponseEntity.ok(ApiResponseCustom.success(emptyCalendar));
        }

        // Validate type parameter
        if (!type.equals("my") && !type.equals("company")) {
            logger.warn("[GET /api/activities] Invalid type parameter: {}", type);
            return ResponseEntity.badRequest().body(ApiResponseCustom.error("Tham số type không hợp lệ. Chỉ chấp nhận 'my' hoặc 'company'"));
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        logger.info("[GET /api/activities] Starting request for user: {}, type: {}", email, type);

        List<ActivityDTO> activities;
        
        if ("my".equals(type)) {
            // Get user's personal activities - need to find user first
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                logger.warn("[GET /api/activities] User not found with email: {}", email);
                return ResponseEntity.status(401).body(ApiResponseCustom.unauthorized("Không tìm thấy người dùng"));
            }
            Integer userId = userOpt.get().getId();
            activities = activityService.getActivitiesForUser(userId);
        } else {
            // Get all company activities
            activities = activityService.getAllActivities();
        }

        // Build calendar response
        CalendarDTO calendar = CalendarDTO.builder()
            .currentDate(LocalDate.now())
            .activities(activities)
            .metadata(CalendarDTO.CalendarMetadata.builder()
                .totalActivities(activities.size())
                .viewType(type)
                .message(activities.isEmpty() ? 
                    ("my".equals(type) ? "Bạn chưa có hoạt động nào" : "Công ty chưa có hoạt động nào") :
                    ("my".equals(type) ? "Hoạt động cá nhân" : "Hoạt động công ty"))
                .build())
            .build();

        long duration = System.currentTimeMillis() - requestStartTime;
        logger.info("[GET /api/activities] Completed in {}ms for user: {}, type: {}, returned {} activities",
                duration, email, type, activities.size());

        return ResponseEntity.ok(ApiResponseCustom.success(calendar));
    }

    @GetMapping("/calendar")
    @Operation(summary = "Lấy dữ liệu calendar view", description = "Lấy dữ liệu hoạt động theo định dạng calendar")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
            @ApiResponse(responseCode = "400", description = "Tham số không hợp lệ", 
                content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<CalendarDTO>> getCalendarView(
            @Parameter(description = "Ngày bắt đầu (format: yyyy-MM-dd)", example = "2025-03-01") 
            @RequestParam(required = false) String startDate,
            @Parameter(description = "Ngày kết thúc (format: yyyy-MM-dd)", example = "2025-03-31") 
            @RequestParam(required = false) String endDate,
            @Parameter(description = "Loại hoạt động", example = "my") 
            @RequestParam(required = false, defaultValue = "company") String type) {
        
        long requestStartTime = System.currentTimeMillis();
        
        // Validate type parameter
        if (!"my".equals(type) && !"company".equals(type)) {
            logger.warn("[GET /api/activities/calendar] Invalid type parameter: {}", type);
            return ResponseEntity.badRequest().body(ApiResponseCustom.error("Tham số type không hợp lệ. Chỉ chấp nhận 'my' hoặc 'company'"));
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        logger.info("[GET /api/activities/calendar] Starting request for user: {}, type: {}, startDate: {}, endDate: {}", 
                email, type, startDate, endDate);

        List<ActivityDTO> activities;
        
        // If no date parameters provided, return empty calendar structure
        if (startDate == null && endDate == null) {
            activities = new ArrayList<>();
            logger.info("[GET /api/activities/calendar] No date parameters provided, returning empty calendar");
        } else {
            try {
                java.time.LocalDate start = startDate != null ? java.time.LocalDate.parse(startDate) : null;
                java.time.LocalDate end = endDate != null ? java.time.LocalDate.parse(endDate) : null;
                
                if (start != null && end != null && start.isAfter(end)) {
                    return ResponseEntity.badRequest().body(ApiResponseCustom.error("Ngày bắt đầu không thể sau ngày kết thúc"));
                }
                
                if ("my".equals(type)) {
                    // Get user's personal activities in date range
                    Optional<User> userOpt = userRepository.findByEmail(email);
                    if (userOpt.isEmpty()) {
                        logger.warn("[GET /api/activities/calendar] User not found with email: {}", email);
                        return ResponseEntity.status(401).body(ApiResponseCustom.unauthorized("Không tìm thấy người dùng"));
                    }
                    Integer userId = userOpt.get().getId();
                    // For now, get user activities and filter by date range
                    activities = activityService.getActivitiesForUser(userId);
                } else {
                    // Get all company activities and filter by date range
                    activities = activityService.getAllActivities();
                }
            } catch (Exception e) {
                logger.error("[GET /api/activities/calendar] Error parsing dates: {}", e.getMessage());
                return ResponseEntity.badRequest().body(ApiResponseCustom.error("Định dạng ngày không hợp lệ. Sử dụng format: yyyy-MM-dd"));
            }
        }

        // Build calendar response
        CalendarDTO calendar = CalendarDTO.builder()
            .currentDate(LocalDate.now())
            .activities(activities)
            .metadata(CalendarDTO.CalendarMetadata.builder()
                .totalActivities(activities.size())
                .viewType(type)
                .message(activities.isEmpty() ? 
                    (startDate == null && endDate == null ? "Vui lòng chọn khoảng thời gian để xem hoạt động" :
                    ("my".equals(type) ? "Trong khoảng " + startDate + " đến " + endDate + " bạn chưa có hoạt động nào" : 
                     "Trong khoảng " + startDate + " đến " + endDate + " công ty chưa có hoạt động nào")) :
                    ("my".equals(type) ? "Hoạt động cá nhân từ " + startDate + " đến " + endDate : 
                     "Hoạt động công ty từ " + startDate + " đến " + endDate))
                .build())
            .build();

        long duration = System.currentTimeMillis() - requestStartTime;
        logger.info("[GET /api/activities/calendar] Completed in {}ms for user: {}, type: {}, returned {} activities",
                duration, email, type, activities.size());

        return ResponseEntity.ok(ApiResponseCustom.success(calendar));
    }

    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm hoạt động theo tháng/năm", description = "Lấy danh sách hoạt động trong tháng và năm cụ thể")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                content = @Content(schema = @Schema(implementation = ActivityListApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Tham số không hợp lệ", 
                content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<List<ActivityDTO>>> searchActivitiesByMonthYear(
            @Parameter(description = "Tháng (1-12)", example = "3", required = true) @RequestParam int month,
            @Parameter(description = "Năm", example = "2025", required = true) @RequestParam int year) {
        logger.info("[GET /api/activities/search] Searching activities for month: {}, year: {}", month, year);
        List<ActivityDTO> activities = activityService.searchActivitiesByMonthYear(month, year);
        return ResponseEntity.ok(ApiResponseCustom.success(activities));
    }

    @GetMapping("/search-by-date")
    @Operation(summary = "Lấy hoạt động theo ngày", description = "Lấy danh sách hoạt động trong ngày cụ thể")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                content = @Content(schema = @Schema(implementation = ActivityListApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Định dạng ngày không hợp lệ", 
                content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<List<ActivityDTO>>> getActivitiesByDate(
            @Parameter(description = "Ngày cần tìm (format: yyyy-MM-dd)", example = "2025-03-15", required = true) 
            @RequestParam String date) {
        try {
            java.time.LocalDate localDate = java.time.LocalDate.parse(date);
            List<ActivityDTO> activities = activityService.getActivitiesByDate(localDate);
            return ResponseEntity.ok(ApiResponseCustom.success(activities));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponseCustom.error("Định dạng ngày không hợp lệ. Sử dụng format: yyyy-MM-dd"));
        }
    }

    @GetMapping("/search-by-range")
    @Operation(summary = "Lấy hoạt động theo khoảng thời gian", description = "Lấy danh sách hoạt động trong khoảng thời gian từ ngày bắt đầu đến ngày kết thúc")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                content = @Content(schema = @Schema(implementation = ActivityListApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Định dạng ngày không hợp lệ hoặc khoảng thời gian không hợp lệ", 
                content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<List<ActivityDTO>>> getActivitiesByDateRange(
            @Parameter(description = "Ngày bắt đầu (format: yyyy-MM-dd)", example = "2025-03-01", required = true) 
            @RequestParam String startDate,
            @Parameter(description = "Ngày kết thúc (format: yyyy-MM-dd)", example = "2025-03-31", required = true) 
            @RequestParam String endDate) {
        try {
            java.time.LocalDate start = java.time.LocalDate.parse(startDate);
            java.time.LocalDate end = java.time.LocalDate.parse(endDate);
            
            if (start.isAfter(end)) {
                return ResponseEntity.badRequest().body(ApiResponseCustom.error("Ngày bắt đầu không thể sau ngày kết thúc"));
            }
            
            List<ActivityDTO> activities = activityService.getActivitiesByDateRange(start, end);
            return ResponseEntity.ok(ApiResponseCustom.success(activities));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponseCustom.error("Định dạng ngày không hợp lệ. Sử dụng format: yyyy-MM-dd"));
        }
    }

    @PostMapping("/{id}/participants")
    @Operation(summary = "Thêm người tham gia", description = "Thêm danh sách người tham gia vào hoạt động")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thêm người tham gia thành công", 
                content = @Content(schema = @Schema(implementation = ActivityParticipantsApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy hoạt động", 
                content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", 
                content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<List<ActivityParticipantDTO>>> addParticipants(
            @Parameter(description = "ID của hoạt động", required = true) @PathVariable Long id,
            @RequestBody List<ActivityParticipantRequest> participantRequests) {
        // Convert requests to DTOs - flatten the list since each request can contain multiple IDs
        List<ActivityParticipantDTO> participants = participantRequests.stream()
            .flatMap(request -> convertToParticipantDTOs(request).stream())
            .collect(Collectors.toList());
        List<ActivityParticipantDTO> addedParticipants = activityService.addParticipants(id, participants);
        return ResponseEntity.ok(ApiResponseCustom.success("Đã thêm người tham gia thành công", addedParticipants));
    }

    @DeleteMapping("/{id}/participants")
    @Operation(summary = "Xóa người tham gia", description = "Xóa một người tham gia khỏi hoạt động")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Xóa người tham gia thành công", 
                content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy hoạt động hoặc người tham gia", 
                content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
            @ApiResponse(responseCode = "400", description = "Tham số không hợp lệ", 
                content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<Void>> removeParticipant(
            @Parameter(description = "ID của hoạt động", required = true) @PathVariable Long id,
            @Parameter(description = "Loại người tham gia", example = "USER", required = true) 
            @RequestParam String participantType,
            @Parameter(description = "ID của người tham gia", required = true) 
            @RequestParam Long participantId) {
        
        // Check if activity exists and get current participants count
        ActivityDTO currentActivity = activityService.getActivity(id);
        if (currentActivity.getParticipants().size() <= 1) {
            return ResponseEntity.badRequest().body(
                ApiResponseCustom.error("Không thể xóa người tham gia cuối cùng. Activity phải có ít nhất một người tham gia.")
            );
        }
        
        activityService.removeParticipant(id, participantType, participantId);
        return ResponseEntity.ok(ApiResponseCustom.success("Đã xóa người tham gia thành công", null));
    }

    @DeleteMapping("/{id}/participants/batch")
    @Operation(summary = "Xóa nhiều người tham gia", description = "Xóa nhiều người tham gia khỏi hoạt động cùng một lúc")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Xóa người tham gia thành công", 
                content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy hoạt động", 
                content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
            @ApiResponse(responseCode = "400", description = "Tham số không hợp lệ hoặc không thể xóa hết participants", 
                content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<String>> removeMultipleParticipants(
            @Parameter(description = "ID của hoạt động", required = true) @PathVariable Long id,
            @RequestBody List<ParticipantDeleteRequest> deleteRequests) {
        
        // Check if activity exists and get current participants count
        ActivityDTO currentActivity = activityService.getActivity(id);
        int currentParticipantCount = currentActivity.getParticipants().size();
        
        if (currentParticipantCount <= deleteRequests.size()) {
            return ResponseEntity.badRequest().body(
                ApiResponseCustom.error("Không thể xóa tất cả người tham gia. Activity phải có ít nhất một người tham gia.")
            );
        }
        
        int deletedCount = 0;
        for (ParticipantDeleteRequest request : deleteRequests) {
            try {
                activityService.removeParticipant(id, request.getParticipantType(), request.getParticipantId());
                deletedCount++;
            } catch (Exception e) {
                logger.warn("[removeMultipleParticipants] Failed to delete participant: type={}, id={}. Error: {}", 
                           request.getParticipantType(), request.getParticipantId(), e.getMessage());
            }
        }
        
        return ResponseEntity.ok(ApiResponseCustom.success(
            String.format("Đã xóa thành công %d/%d người tham gia", deletedCount, deleteRequests.size()), 
            String.valueOf(deletedCount)));
    }

    @PutMapping("/{id}/pin")
    @Operation(summary = "Ghim/bỏ ghim hoạt động", description = "Ghim hoặc bỏ ghim hoạt động để ưu tiên hiển thị")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật trạng thái ghim thành công", 
                content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy hoạt động", 
                content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<Void>> pinActivity(
            @Parameter(description = "ID của hoạt động", required = true) @PathVariable Long id, 
            @Parameter(description = "Trạng thái ghim (true: ghim, false: bỏ ghim)", required = true) 
            @RequestParam boolean pinned) {
        activityService.pinActivity(id, pinned);
        String message = pinned ? "Đã ghim hoạt động thành công" : "Đã bỏ ghim hoạt động thành công";
        return ResponseEntity.ok(ApiResponseCustom.success(message, null));
    }

    @GetMapping("/pinned")
    @Operation(summary = "Lấy hoạt động đã ghim", description = "Lấy danh sách tất cả hoạt động đã được ghim")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                content = @Content(schema = @Schema(implementation = ActivityListApiResponse.class)))
    })
    public ResponseEntity<ApiResponseCustom<List<ActivityDTO>>> getPinnedActivities() {
        long start = System.currentTimeMillis();
        List<ActivityDTO> pinned = activityService.getPinnedActivities();
        long duration = System.currentTimeMillis() - start;
        logger.info("[GET /api/activities/pinned] Completed in {}ms, returned {} activities", duration, pinned.size());
        return ResponseEntity.ok(ApiResponseCustom.success(pinned));
    }

    @DeleteMapping("/batch")
    @Operation(summary = "Xóa nhiều hoạt động", description = "Xóa nhiều hoạt động cùng một lúc theo danh sách ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Xóa hoạt động thành công", 
                content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
            @ApiResponse(responseCode = "400", description = "Danh sách ID không hợp lệ", 
                content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<String>> deleteMultipleActivities(
            @Parameter(description = "Danh sách ID các hoạt động cần xóa", required = true)
            @RequestBody List<Long> activityIds) {
        
        if (activityIds.isEmpty()) {
            return ResponseEntity.badRequest().body(
                ApiResponseCustom.error("Danh sách ID hoạt động không được rỗng")
            );
        }
        
        int deletedCount = 0;
        for (Long activityId : activityIds) {
            try {
                activityService.deleteActivity(activityId);
                deletedCount++;
            } catch (Exception e) {
                logger.warn("[deleteMultipleActivities] Failed to delete activity with ID: {}. Error: {}", 
                           activityId, e.getMessage());
            }
        }
        
        return ResponseEntity.ok(ApiResponseCustom.success(
            String.format("Đã xóa thành công %d/%d hoạt động", deletedCount, activityIds.size()),
            String.valueOf(deletedCount)));
    }

    // Helper method to convert ActivityRequest to ActivityDTO
    private ActivityDTO convertToActivityDTO(ActivityRequest request) {
        ActivityDTO dto = new ActivityDTO();
        dto.setName(request.getName());
        dto.setLocation(request.getLocation());
        dto.setStartTime(request.getStartTime());
        dto.setEndTime(request.getEndTime());
        dto.setNotes(request.getNotes());
        dto.setPinned(request.getPinned());
        
        // Convert participants - flatten the list since each request can contain multiple IDs
        if (request.getParticipants() != null) {
            // Validate that participants array is not empty
            if (request.getParticipants().isEmpty()) {
                throw new IllegalArgumentException("Activity phải có ít nhất một người tham gia. Không thể tạo/cập nhật activity với danh sách participants rỗng.");
            }
            
            List<ActivityParticipantDTO> participants = request.getParticipants().stream()
                .flatMap(participantRequest -> convertToParticipantDTOs(participantRequest).stream())
                .collect(Collectors.toList());
            dto.setParticipants(participants);
        } else {
            // Null participants is also not allowed for strict validation
            throw new IllegalArgumentException("Activity phải có ít nhất một người tham gia. Trường participants là bắt buộc.");
        }
        
        return dto;
    }
    
    // Helper method to convert ActivityParticipantRequest to ActivityParticipantDTO
    private List<ActivityParticipantDTO> convertToParticipantDTOs(ActivityParticipantRequest request) {
        // Validate that participantIds is not empty
        if (request.getParticipantIds() == null || request.getParticipantIds().isEmpty()) {
            throw new IllegalArgumentException("Participant IDs cannot be null or empty. At least one participant ID is required.");
        }
        
        // Convert each ID in the array to a separate ActivityParticipantDTO
        return request.getParticipantIds().stream()
            .map(participantId -> {
                ActivityParticipantDTO dto = new ActivityParticipantDTO();
                // Note: We don't set ID as it's not needed for requests and will be auto-generated
                dto.setParticipantType(request.getParticipantType());
                dto.setParticipantId(participantId);
                // Note: We don't set participantName as it will be set by service layer for responses
                return dto;
            })
            .collect(java.util.stream.Collectors.toList());
    }
    

}
