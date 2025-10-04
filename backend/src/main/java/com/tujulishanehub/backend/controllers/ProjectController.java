package com.tujulishanehub.backend.controllers;

import com.tujulishanehub.backend.models.ApprovalStatus;
import com.tujulishanehub.backend.models.Project;
import com.tujulishanehub.backend.models.User;
import com.tujulishanehub.backend.payload.ApiResponse;
import com.tujulishanehub.backend.services.ProjectService;
import com.tujulishanehub.backend.services.ProjectCollaboratorService;
import com.tujulishanehub.backend.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);
    
    @Autowired
    private ProjectService projectService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ProjectCollaboratorService projectCollaboratorService;
    
    /**
     * Create a new project (Partners/Donors only)
     */
    @PostMapping
    @PreAuthorize("hasRole('PARTNER') and @userService.canUserCreateProjects(authentication.name)")
    public ResponseEntity<ApiResponse<Project>> createProject(@Valid @RequestBody Project project) {
        try {
            logger.info("Creating new project: {}", project.getTitle());
            
            // Get the authenticated user and associate with project
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = auth.getName();
            
            Project createdProject = projectService.createProjectByPartner(project, userEmail);
            
            ApiResponse<Project> response = new ApiResponse<>(
                HttpStatus.CREATED.value(), 
                "Project created successfully", 
                createdProject
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Error creating project: {}", e.getMessage(), e);
            ApiResponse<Project> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to create project: " + e.getMessage(), 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get all projects with pagination
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Project> projectPage = projectService.getProjects(pageable);
            
            Map<String, Object> data = new HashMap<>();
            data.put("projects", projectPage.getContent());
            data.put("currentPage", projectPage.getNumber());
            data.put("totalItems", projectPage.getTotalElements());
            data.put("totalPages", projectPage.getTotalPages());
            data.put("hasNext", projectPage.hasNext());
            data.put("hasPrevious", projectPage.hasPrevious());
            
            ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Projects retrieved successfully", 
                data
            );
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving projects: {}", e.getMessage(), e);
            ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to retrieve projects: " + e.getMessage(), 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get project by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Project>> getProjectById(@PathVariable Long id) {
        try {
            Optional<Project> project = projectService.getProjectById(id);
            
            if (project.isPresent()) {
                ApiResponse<Project> response = new ApiResponse<>(
                    HttpStatus.OK.value(), 
                    "Project found", 
                    project.get()
                );
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<Project> response = new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(), 
                    "Project not found with ID: " + id, 
                    null
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
        } catch (Exception e) {
            logger.error("Error retrieving project {}: {}", id, e.getMessage(), e);
            ApiResponse<Project> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to retrieve project: " + e.getMessage(), 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Update project (Owner, Admin, or Collaborator with EDITOR/CO_OWNER role)
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Project>> updateProject(
            @PathVariable Long id, 
            @Valid @RequestBody Project project) {
        
        try {
            // Get authenticated user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = auth.getName();
            
            // Check if user is admin, super admin, owner, or collaborator with edit permission
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SUPER_ADMIN"));
            boolean isOwner = projectService.isProjectOwner(id, userEmail);
            boolean canEdit = projectCollaboratorService.canEditProject(id, userEmail);
            
            if (!isAdmin && !isOwner && !canEdit) {
                logger.warn("User {} attempted to edit project {} without permission", userEmail, id);
                ApiResponse<Project> response = new ApiResponse<>(
                    HttpStatus.FORBIDDEN.value(), 
                    "You don't have permission to edit this project", 
                    null
                );
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            // Update the project with tracking
            Project updatedProject = projectService.updateProject(id, project, userEmail);
            
            ApiResponse<Project> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Project updated successfully", 
                updatedProject
            );
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            logger.error("Error updating project {}: {}", id, e.getMessage());
            ApiResponse<Project> response = new ApiResponse<>(
                HttpStatus.NOT_FOUND.value(), 
                e.getMessage(), 
                null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            
        } catch (Exception e) {
            logger.error("Error updating project {}: {}", id, e.getMessage(), e);
            ApiResponse<Project> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to update project: " + e.getMessage(), 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Delete project
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteProject(@PathVariable Long id) {
        try {
            projectService.deleteProject(id);
            
            ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Project deleted successfully", 
                null
            );
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            logger.error("Error deleting project {}: {}", id, e.getMessage());
            ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.NOT_FOUND.value(), 
                e.getMessage(), 
                null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            
        } catch (Exception e) {
            logger.error("Error deleting project {}: {}", id, e.getMessage(), e);
            ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to delete project: " + e.getMessage(), 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Search projects
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Project>>> searchProjects(
            @RequestParam(required = false) String partner,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String county,
            @RequestParam(required = false) String activityType) {
        
        try {
            List<Project> projects = projectService.searchProjects(partner, title, status, county, activityType);
            
            ApiResponse<List<Project>> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Search completed successfully", 
                projects
            );
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error searching projects: {}", e.getMessage(), e);
            ApiResponse<List<Project>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to search projects: " + e.getMessage(), 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get projects by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<Project>>> getProjectsByStatus(@PathVariable String status) {
        try {
            List<Project> projects = projectService.getProjectsByStatus(status);
            
            ApiResponse<List<Project>> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Projects retrieved successfully", 
                projects
            );
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving projects by status {}: {}", status, e.getMessage(), e);
            ApiResponse<List<Project>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to retrieve projects: " + e.getMessage(), 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get projects with coordinates (for map display)
     */
    @GetMapping("/with-coordinates")
    public ResponseEntity<ApiResponse<List<Project>>> getProjectsWithCoordinates() {
        try {
            List<Project> projects = projectService.getProjectsWithCoordinates();
            
            ApiResponse<List<Project>> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Projects with coordinates retrieved successfully", 
                projects
            );
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving projects with coordinates: {}", e.getMessage(), e);
            ApiResponse<List<Project>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to retrieve projects: " + e.getMessage(), 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get projects within a geographic bounding box
     */
    @GetMapping("/in-bounds")
    public ResponseEntity<ApiResponse<List<Project>>> getProjectsInBoundingBox(
            @RequestParam Double minLat,
            @RequestParam Double maxLat,
            @RequestParam Double minLng,
            @RequestParam Double maxLng) {
        
        try {
            List<Project> projects = projectService.getProjectsInBoundingBox(minLat, maxLat, minLng, maxLng);
            
            ApiResponse<List<Project>> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Projects in bounding box retrieved successfully", 
                projects
            );
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving projects in bounding box: {}", e.getMessage(), e);
            ApiResponse<List<Project>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to retrieve projects: " + e.getMessage(), 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get projects by date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<Project>>> getProjectsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        try {
            List<Project> projects = projectService.getProjectsByDateRange(startDate, endDate);
            
            ApiResponse<List<Project>> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Projects in date range retrieved successfully", 
                projects
            );
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving projects by date range: {}", e.getMessage(), e);
            ApiResponse<List<Project>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to retrieve projects: " + e.getMessage(), 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get currently active projects
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<Project>>> getActiveProjects() {
        try {
            List<Project> projects = projectService.getActiveProjects();
            
            ApiResponse<List<Project>> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Active projects retrieved successfully", 
                projects
            );
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving active projects: {}", e.getMessage(), e);
            ApiResponse<List<Project>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to retrieve active projects: " + e.getMessage(), 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get project statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProjectStatistics() {
        try {
            ProjectService.ProjectStatistics stats = projectService.getProjectStatistics();
            
            Map<String, Object> data = new HashMap<>();
            data.put("totalProjects", stats.getTotalProjects());
            data.put("projectsWithCoordinates", stats.getProjectsWithCoordinates());
            data.put("coordinatesCoveragePercentage", stats.getCoordinatesCoveragePercentage());
            data.put("statusCounts", stats.getStatusCounts());
            data.put("countyCounts", stats.getCountyCounts());
            
            ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Statistics retrieved successfully", 
                data
            );
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving project statistics: {}", e.getMessage(), e);
            ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to retrieve statistics: " + e.getMessage(), 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Batch process projects that need geocoding
     */
    @PostMapping("/geocode-batch")
    public ResponseEntity<ApiResponse<Object>> batchGeocodeProjects() {
        try {
            projectService.processProjectsNeedingGeocoding();
            
            ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Batch geocoding process completed successfully", 
                null
            );
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error in batch geocoding process: {}", e.getMessage(), e);
            ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to complete batch geocoding: " + e.getMessage(), 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // ==================== ADMIN ENDPOINTS ====================
    
    /**
     * Approve a project (Admin only)
     */
    @PostMapping("/admin/approve/{projectId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> approveProject(@PathVariable Long projectId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String adminEmail = auth.getName();
            User admin = userService.getUserByEmail(adminEmail);
            
            boolean success = projectService.approveProject(projectId, admin.getId());
            if (success) {
                ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.OK.value(), 
                    "Project approved successfully", 
                    null
                );
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(), 
                    "Project not found", 
                    null
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            logger.error("Error approving project {}: {}", projectId, e.getMessage(), e);
            ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to approve project", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Reject a project (Admin only)
     */
    @PostMapping("/admin/reject/{projectId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> rejectProject(
            @PathVariable Long projectId, 
            @RequestBody Map<String, String> payload) {
        try {
            String reason = payload.getOrDefault("reason", "No reason provided");
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String adminEmail = auth.getName();
            User admin = userService.getUserByEmail(adminEmail);
            
            boolean success = projectService.rejectProject(projectId, admin.getId(), reason);
            if (success) {
                ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.OK.value(), 
                    "Project rejected successfully", 
                    null
                );
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(), 
                    "Project not found", 
                    null
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            logger.error("Error rejecting project {}: {}", projectId, e.getMessage(), e);
            ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to reject project", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get projects by approval status (Admin only)
     */
    @GetMapping("/admin/approval-status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<Project>>> getProjectsByApprovalStatus(@PathVariable String status) {
        try {
            ApprovalStatus approvalStatus = ApprovalStatus.valueOf(status.toUpperCase());
            List<Project> projects = projectService.getProjectsByApprovalStatus(approvalStatus);
            ApiResponse<List<Project>> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Projects retrieved successfully", 
                projects
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ApiResponse<List<Project>> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(), 
                "Invalid approval status. Valid values: PENDING, APPROVED, REJECTED", 
                null
            );
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Error retrieving projects by approval status {}: {}", status, e.getMessage(), e);
            ApiResponse<List<Project>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to retrieve projects", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get admin dashboard statistics (Admin only)
     */
    @GetMapping("/admin/dashboard-stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAdminDashboardStats() {
        try {
            Map<String, Object> stats = projectService.getAdminDashboardStats();
            ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Admin dashboard statistics retrieved successfully", 
                stats
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error retrieving admin dashboard stats: {}", e.getMessage(), e);
            ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to retrieve dashboard statistics", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get user's own projects (Authenticated users)
     */
    @GetMapping("/my-projects")
    @PreAuthorize("hasRole('PARTNER')")
    public ResponseEntity<ApiResponse<List<Project>>> getMyProjects() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = auth.getName();
            List<Project> projects = projectService.getProjectsByPartnerEmail(userEmail);
            
            ApiResponse<List<Project>> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Your projects retrieved successfully", 
                projects
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error retrieving user projects: {}", e.getMessage(), e);
            ApiResponse<List<Project>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to retrieve your projects", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ========== SUPER-ADMIN ENDPOINTS ==========

    /**
     * Get all projects (Super-Admin only)
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<Project>>> getAllProjectsAdmin() {
        try {
            List<Project> projects = projectService.getAllProjects();
            ApiResponse<List<Project>> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "All projects retrieved successfully", 
                projects
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error retrieving all projects: {}", e.getMessage(), e);
            ApiResponse<List<Project>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to retrieve projects", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}