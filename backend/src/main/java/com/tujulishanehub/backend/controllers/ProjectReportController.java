package com.tujulishanehub.backend.controllers;

import com.tujulishanehub.backend.models.ProjectReport;
import com.tujulishanehub.backend.models.User;
import com.tujulishanehub.backend.payload.ApiResponse;
import com.tujulishanehub.backend.services.ProjectReportService;
import com.tujulishanehub.backend.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/reports")
public class ProjectReportController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProjectReportController.class);
    
    @Autowired
    private ProjectReportService projectReportService;
    
    @Autowired
    private UserService userService;
    
    /**
     * Create a new project report (Authenticated users only)
     */
    @PostMapping("/projects/{projectId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ProjectReport>> createReport(
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectReport report) {
        
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = auth.getName();
            User user = userService.getUserByEmail(userEmail);
            
            ProjectReport createdReport = projectReportService.createReport(report, projectId, user.getId());
            
            ApiResponse<ProjectReport> response = new ApiResponse<>(
                HttpStatus.CREATED.value(), 
                "Report created successfully", 
                createdReport
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (RuntimeException e) {
            logger.error("Error creating report: {}", e.getMessage());
            ApiResponse<ProjectReport> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(), 
                e.getMessage(), 
                null
            );
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Unexpected error creating report: {}", e.getMessage(), e);
            ApiResponse<ProjectReport> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to create report", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get report by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectReport>> getReportById(@PathVariable Long id) {
        try {
            Optional<ProjectReport> reportOpt = projectReportService.getReportById(id);
            
            if (reportOpt.isPresent()) {
                ProjectReport report = reportOpt.get();
                
                // Check if report is published or user has access
                if (!report.isPublished()) {
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    if (auth == null || !auth.isAuthenticated()) {
                        ApiResponse<ProjectReport> response = new ApiResponse<>(
                            HttpStatus.FORBIDDEN.value(), 
                            "Access denied. Report not published.", 
                            null
                        );
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                    }
                    
                    String userEmail = auth.getName();
                    User user = userService.getUserByEmail(userEmail);
                    
                    // Allow access if user is the author or admin
                    if (!report.getSubmittedBy().equals(user.getId()) && !user.isSuperAdmin()) {
                        ApiResponse<ProjectReport> response = new ApiResponse<>(
                            HttpStatus.FORBIDDEN.value(), 
                            "Access denied", 
                            null
                        );
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                    }
                }
                
                ApiResponse<ProjectReport> response = new ApiResponse<>(
                    HttpStatus.OK.value(), 
                    "Report retrieved successfully", 
                    report
                );
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<ProjectReport> response = new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(), 
                    "Report not found", 
                    null
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
        } catch (Exception e) {
            logger.error("Error retrieving report with ID {}: {}", id, e.getMessage(), e);
            ApiResponse<ProjectReport> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to retrieve report", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get all published reports with pagination
     */
    @GetMapping("/published")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPublishedReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "publishedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<ProjectReport> reportPage = projectReportService.getPublishedReports(pageable);
            
            Map<String, Object> data = new HashMap<>();
            data.put("reports", reportPage.getContent());
            data.put("currentPage", reportPage.getNumber());
            data.put("totalItems", reportPage.getTotalElements());
            data.put("totalPages", reportPage.getTotalPages());
            data.put("hasNext", reportPage.hasNext());
            data.put("hasPrevious", reportPage.hasPrevious());
            
            ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Published reports retrieved successfully", 
                data
            );
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving published reports: {}", e.getMessage(), e);
            ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to retrieve published reports", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get reports for a specific project
     */
    @GetMapping("/projects/{projectId}")
    public ResponseEntity<ApiResponse<List<ProjectReport>>> getProjectReports(@PathVariable Long projectId) {
        try {
            List<ProjectReport> reports = projectReportService.getReportsByProjectId(projectId);
            
            // Filter to published reports for non-authenticated users
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                reports = reports.stream()
                    .filter(ProjectReport::isPublished)
                    .toList();
            } else {
                // For authenticated users, check if they have access to non-published reports
                String userEmail = auth.getName();
                User user = userService.getUserByEmail(userEmail);
                
                if (!user.isSuperAdmin()) {
                    reports = reports.stream()
                        .filter(report -> report.isPublished() || report.getSubmittedBy().equals(user.getId()))
                        .toList();
                }
            }
            
            ApiResponse<List<ProjectReport>> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Project reports retrieved successfully", 
                reports
            );
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving reports for project {}: {}", projectId, e.getMessage(), e);
            ApiResponse<List<ProjectReport>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to retrieve project reports", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get completion reports
     */
    @GetMapping("/completion")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCompletionReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
            Page<ProjectReport> reportPage = projectReportService.getCompletionReports(pageable);
            
            Map<String, Object> data = new HashMap<>();
            data.put("reports", reportPage.getContent());
            data.put("currentPage", reportPage.getNumber());
            data.put("totalItems", reportPage.getTotalElements());
            data.put("totalPages", reportPage.getTotalPages());
            
            ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Completion reports retrieved successfully", 
                data
            );
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving completion reports: {}", e.getMessage(), e);
            ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to retrieve completion reports", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Search reports
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProjectReport>>> searchReports(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String reportType,
            @RequestParam(required = false) String reportStatus,
            @RequestParam(required = false) String keyword) {
        
        try {
            ProjectReport.ReportType type = reportType != null ? 
                ProjectReport.ReportType.valueOf(reportType.toUpperCase()) : null;
            ProjectReport.ReportStatus status = reportStatus != null ? 
                ProjectReport.ReportStatus.valueOf(reportStatus.toUpperCase()) : null;
            
            List<ProjectReport> reports = projectReportService.searchReports(projectId, type, status, keyword);
            
            // Filter based on user access
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                reports = reports.stream()
                    .filter(ProjectReport::isPublished)
                    .toList();
            } else {
                String userEmail = auth.getName();
                User user = userService.getUserByEmail(userEmail);
                
                if (!user.isSuperAdmin()) {
                    reports = reports.stream()
                        .filter(report -> report.isPublished() || report.getSubmittedBy().equals(user.getId()))
                        .toList();
                }
            }
            
            ApiResponse<List<ProjectReport>> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Reports search completed", 
                reports
            );
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            ApiResponse<List<ProjectReport>> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(), 
                "Invalid report type or status", 
                null
            );
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Error searching reports: {}", e.getMessage(), e);
            ApiResponse<List<ProjectReport>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to search reports", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Update report (Author only)
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ProjectReport>> updateReport(
            @PathVariable Long id,
            @Valid @RequestBody ProjectReport reportDetails) {
        
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = auth.getName();
            User user = userService.getUserByEmail(userEmail);
            
            ProjectReport updatedReport = projectReportService.updateReport(id, reportDetails, user.getId());
            
            ApiResponse<ProjectReport> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Report updated successfully", 
                updatedReport
            );
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            logger.error("Error updating report: {}", e.getMessage());
            ApiResponse<ProjectReport> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(), 
                e.getMessage(), 
                null
            );
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Unexpected error updating report: {}", e.getMessage(), e);
            ApiResponse<ProjectReport> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to update report", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Submit report for review (Author only)
     */
    @PostMapping("/{id}/submit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Object>> submitReportForReview(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = auth.getName();
            User user = userService.getUserByEmail(userEmail);
            
            boolean success = projectReportService.submitReportForReview(id, user.getId());
            
            if (success) {
                ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.OK.value(), 
                    "Report submitted for review successfully", 
                    null
                );
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.BAD_REQUEST.value(), 
                    "Failed to submit report", 
                    null
                );
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (RuntimeException e) {
            logger.error("Error submitting report: {}", e.getMessage());
            ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(), 
                e.getMessage(), 
                null
            );
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Unexpected error submitting report: {}", e.getMessage(), e);
            ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to submit report", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // ==================== ADMIN ENDPOINTS ====================
    
    /**
     * Get reports for review (Admin only)
     */
    @GetMapping("/admin/review")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('SUPER_ADMIN_REVIEWER') or hasRole('SUPER_ADMIN_APPROVER')")
    public ResponseEntity<ApiResponse<List<ProjectReport>>> getReportsForReview() {
        try {
            List<ProjectReport> reports = projectReportService.getReportsForReview();
            
            ApiResponse<List<ProjectReport>> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Reports for review retrieved successfully", 
                reports
            );
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving reports for review: {}", e.getMessage(), e);
            ApiResponse<List<ProjectReport>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to retrieve reports for review", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Set report under review (Admin only)
     */
    @PostMapping("/admin/{id}/review")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('SUPER_ADMIN_REVIEWER') or hasRole('SUPER_ADMIN_APPROVER')")
    public ResponseEntity<ApiResponse<Object>> setReportUnderReview(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String adminEmail = auth.getName();
            User admin = userService.getUserByEmail(adminEmail);
            
            boolean success = projectReportService.setReportUnderReview(id, admin.getId());
            
            if (success) {
                ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.OK.value(), 
                    "Report set under review successfully", 
                    null
                );
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(), 
                    "Report not found", 
                    null
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
        } catch (Exception e) {
            logger.error("Error setting report under review: {}", e.getMessage(), e);
            ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to set report under review", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Approve report (Admin only)
     */
    @PostMapping("/admin/{id}/approve")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> approveReport(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String adminEmail = auth.getName();
            User admin = userService.getUserByEmail(adminEmail);
            
            boolean success = projectReportService.approveReport(id, admin.getId());
            
            if (success) {
                ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.OK.value(), 
                    "Report approved successfully", 
                    null
                );
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(), 
                    "Report not found", 
                    null
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
        } catch (Exception e) {
            logger.error("Error approving report: {}", e.getMessage(), e);
            ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to approve report", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Reject report (Admin only)
     */
    @PostMapping("/admin/{id}/reject")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> rejectReport(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String adminEmail = auth.getName();
            User admin = userService.getUserByEmail(adminEmail);
            
            boolean success = projectReportService.rejectReport(id, admin.getId());
            
            if (success) {
                ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.OK.value(), 
                    "Report rejected successfully", 
                    null
                );
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(), 
                    "Report not found", 
                    null
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
        } catch (Exception e) {
            logger.error("Error rejecting report: {}", e.getMessage(), e);
            ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to reject report", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Publish report (Admin only)
     */
    @PostMapping("/admin/{id}/publish")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> publishReport(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String adminEmail = auth.getName();
            User admin = userService.getUserByEmail(adminEmail);
            
            boolean success = projectReportService.publishReport(id, admin.getId());
            
            if (success) {
                ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.OK.value(), 
                    "Report published successfully", 
                    null
                );
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(), 
                    "Report not found", 
                    null
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
        } catch (Exception e) {
            logger.error("Error publishing report: {}", e.getMessage(), e);
            ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to publish report", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get report statistics (Admin only)
     */
    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ProjectReportService.ReportStats>> getReportStats() {
        try {
            ProjectReportService.ReportStats stats = projectReportService.getReportStats();
            
            ApiResponse<ProjectReportService.ReportStats> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Report statistics retrieved successfully", 
                stats
            );
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving report statistics: {}", e.getMessage(), e);
            ApiResponse<ProjectReportService.ReportStats> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to retrieve report statistics", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Delete report (Admin or Author only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Object>> deleteReport(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = auth.getName();
            User user = userService.getUserByEmail(userEmail);
            
            boolean success = projectReportService.deleteReport(id, user.getId());
            
            if (success) {
                ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.OK.value(), 
                    "Report deleted successfully", 
                    null
                );
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(), 
                    "Report not found", 
                    null
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
        } catch (RuntimeException e) {
            logger.error("Error deleting report: {}", e.getMessage());
            ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(), 
                e.getMessage(), 
                null
            );
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Unexpected error deleting report: {}", e.getMessage(), e);
            ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to delete report", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}