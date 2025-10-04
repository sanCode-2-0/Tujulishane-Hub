package com.tujulishanehub.backend.controllers;

import com.tujulishanehub.backend.models.Announcement;
import com.tujulishanehub.backend.payload.ApiResponse;
import com.tujulishanehub.backend.services.AnnouncementService;
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
@RequestMapping("/api/announcements")
public class AnnouncementController {
    
    private static final Logger logger = LoggerFactory.getLogger(AnnouncementController.class);
    
    @Autowired
    private AnnouncementService announcementService;
    
    /**
     * Create new announcement (Partners only)
     */
    @PostMapping
    @PreAuthorize("hasRole('PARTNER')")
    public ResponseEntity<ApiResponse<Announcement>> createAnnouncement(
            @Valid @RequestBody Announcement announcement) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = auth.getName();
            
            Announcement created = announcementService.createAnnouncement(announcement, userEmail);
            
            ApiResponse<Announcement> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "Announcement created successfully",
                created
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (RuntimeException e) {
            logger.error("Error creating announcement: {}", e.getMessage());
            ApiResponse<Announcement> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                "Failed to create announcement: " + e.getMessage(),
                null
            );
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Error creating announcement: {}", e.getMessage(), e);
            ApiResponse<Announcement> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Failed to create announcement: " + e.getMessage(),
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get all active announcements (Public)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Announcement>>> getActiveAnnouncements() {
        try {
            List<Announcement> announcements = announcementService.getActiveAnnouncements();
            
            ApiResponse<List<Announcement>> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Announcements retrieved successfully",
                announcements
            );
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving announcements: {}", e.getMessage(), e);
            ApiResponse<List<Announcement>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Failed to retrieve announcements",
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get my announcements (Partners only)
     */
    @GetMapping("/my-announcements")
    @PreAuthorize("hasRole('PARTNER')")
    public ResponseEntity<ApiResponse<List<Announcement>>> getMyAnnouncements() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = auth.getName();
            
            List<Announcement> announcements = announcementService.getMyAnnouncements(userEmail);
            
            ApiResponse<List<Announcement>> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Your announcements retrieved successfully",
                announcements
            );
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving user announcements: {}", e.getMessage(), e);
            ApiResponse<List<Announcement>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Failed to retrieve your announcements",
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get specific announcement by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Announcement>> getAnnouncementById(@PathVariable Long id) {
        try {
            return announcementService.getAnnouncementById(id)
                .map(announcement -> {
                    ApiResponse<Announcement> response = new ApiResponse<>(
                        HttpStatus.OK.value(),
                        "Announcement retrieved successfully",
                        announcement
                    );
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    ApiResponse<Announcement> response = new ApiResponse<>(
                        HttpStatus.NOT_FOUND.value(),
                        "Announcement not found",
                        null
                    );
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
            
        } catch (Exception e) {
            logger.error("Error retrieving announcement: {}", e.getMessage(), e);
            ApiResponse<Announcement> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Failed to retrieve announcement",
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Close announcement (Owner or Admin only)
     */
    @PostMapping("/{id}/close")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Object>> closeAnnouncement(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = auth.getName();
            
            boolean success = announcementService.closeAnnouncement(id, userEmail);
            
            if (success) {
                ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Announcement closed successfully",
                    null
                );
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(),
                    "Announcement not found or permission denied",
                    null
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
        } catch (Exception e) {
            logger.error("Error closing announcement: {}", e.getMessage(), e);
            ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Failed to close announcement",
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}