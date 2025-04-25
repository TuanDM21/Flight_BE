package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.dto.ActivityDTO;
import com.project.quanlycanghangkhong.dto.ActivityParticipantDTO;
import com.project.quanlycanghangkhong.model.User;
import com.project.quanlycanghangkhong.repository.UserRepository;
import com.project.quanlycanghangkhong.service.ActivityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.ServerResponse.SseBuilder;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/activities")
@CrossOrigin(origins = "*")
public class ActivityController {
    @Autowired
    private ActivityService activityService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<ActivityDTO> createActivity(@Valid @RequestBody ActivityDTO dto) {
        return ResponseEntity.ok(activityService.createActivity(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActivityDTO> updateActivity(@PathVariable Long id, @Valid @RequestBody ActivityDTO dto) {
        return ResponseEntity.ok(activityService.updateActivity(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityDTO> getActivity(@PathVariable Long id) {
        return ResponseEntity.ok(activityService.getActivity(id));
    }

    @GetMapping
    public ResponseEntity<List<ActivityDTO>> getAllActivities(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location) {
        if (name != null || location != null) {
            return ResponseEntity.ok(activityService.searchActivities(name, location));
        }
        return ResponseEntity.ok(activityService.getAllActivities());
    }

    @GetMapping("/search")
    public ResponseEntity<List<ActivityDTO>> searchActivitiesByMonthYear(
            @RequestParam int month,
            @RequestParam int year) {
                System.out.println("Month: " + month + ", Year: " + year);
        return ResponseEntity.ok(activityService.searchActivitiesByMonthYear(month, year));
    }

    @PostMapping("/{id}/participants")
    public ResponseEntity<List<ActivityParticipantDTO>> addParticipants(
            @PathVariable Long id,
            @RequestBody List<ActivityParticipantDTO> participants) {
        return ResponseEntity.ok(activityService.addParticipants(id, participants));
    }

    @DeleteMapping("/{id}/participants")
    public ResponseEntity<Void> removeParticipant(
            @PathVariable Long id,
            @RequestParam String participantType,
            @RequestParam Long participantId) {
        activityService.removeParticipant(id, participantType, participantId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<ActivityDTO>> getMyActivities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        Integer userId = userOpt.get().getId();
        return ResponseEntity.ok(activityService.getActivitiesForUser(userId));
    }
}
