package com.tujulishanehub.backend.controllers;

import com.tujulishanehub.backend.models.GeneralAnnouncement;
import com.tujulishanehub.backend.payload.ApiResponse;
import com.tujulishanehub.backend.payload.GeneralAnnouncementRequest;
import com.tujulishanehub.backend.services.GeneralAnnouncementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/general-announcements")
public class GeneralAnnouncementController {

    private static final Logger logger = LoggerFactory.getLogger(GeneralAnnouncementController.class);

    @Autowired
    private GeneralAnnouncementService generalAnnouncementService;

    /**
     * Create new general announcement (SUPER_ADMIN_APPROVER and SUPER_ADMIN_REVIEWER only)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_APPROVER', 'SUPER_ADMIN_REVIEWER')")
    public ResponseEntity<ApiResponse<GeneralAnnouncement>> createAnnouncement(
            @Valid @RequestBody GeneralAnnouncementRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = auth.getName();

            GeneralAnnouncement created = generalAnnouncementService.createAnnouncement(
                request.getTitle(), request.getBody(), userEmail);

            ApiResponse<GeneralAnnouncement> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "Announcement created successfully",
                created
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            logger.error("Error creating announcement: {}", e.getMessage());
            ApiResponse<GeneralAnnouncement> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                "Failed to create announcement: " + e.getMessage(),
                null
            );
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Error creating announcement: {}", e.getMessage(), e);
            ApiResponse<GeneralAnnouncement> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Failed to create announcement: " + e.getMessage(),
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get all general announcements (Public)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<GeneralAnnouncement>>> getAllAnnouncements() {
        try {
            logger.info("Fetching all general announcements");
            List<GeneralAnnouncement> announcements = generalAnnouncementService.getAllAnnouncements();
            logger.info("Found {} general announcements", announcements.size());

            ApiResponse<List<GeneralAnnouncement>> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Announcements retrieved successfully",
                announcements
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error retrieving announcements: {}", e.getMessage(), e);
            ApiResponse<List<GeneralAnnouncement>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Failed to retrieve announcements: " + e.getMessage(),
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get specific announcement by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GeneralAnnouncement>> getAnnouncementById(@PathVariable Long id) {
        try {
            return generalAnnouncementService.getAnnouncementById(id)
                .map(announcement -> {
                    ApiResponse<GeneralAnnouncement> response = new ApiResponse<>(
                        HttpStatus.OK.value(),
                        "Announcement retrieved successfully",
                        announcement
                    );
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    ApiResponse<GeneralAnnouncement> response = new ApiResponse<>(
                        HttpStatus.NOT_FOUND.value(),
                        "Announcement not found",
                        null
                    );
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });

        } catch (Exception e) {
            logger.error("Error retrieving announcement: {}", e.getMessage(), e);
            ApiResponse<GeneralAnnouncement> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Failed to retrieve announcement",
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}