package com.tujulishanehub.backend.controllers;

import com.tujulishanehub.backend.models.PastProject;
import com.tujulishanehub.backend.models.Project;
import com.tujulishanehub.backend.payload.ApiResponse;
import com.tujulishanehub.backend.services.PastProjectService;
import com.tujulishanehub.backend.services.ProjectService;
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
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/past-projects")
public class PastProjectController {

    private static final Logger logger = LoggerFactory.getLogger(PastProjectController.class);

    @Autowired
    private PastProjectService pastProjectService;

    @Autowired
    private ProjectService projectService;

    /**
     * Get all past projects (completed and stalled) with pagination
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllPastProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "endDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Project> completedProjectPage = projectService.getPastProjects(pageable);

            Map<String, Object> data = new HashMap<>();
            data.put("pastProjects", completedProjectPage.getContent());
            data.put("currentPage", completedProjectPage.getNumber());
            data.put("totalItems", completedProjectPage.getTotalElements());
            data.put("totalPages", completedProjectPage.getTotalPages());

            ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Past projects retrieved successfully",
                data
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error retrieving past projects: {}", e.getMessage(), e);
            ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Failed to retrieve past projects: " + e.getMessage(),
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get past project by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PastProject>> getPastProjectById(@PathVariable Long id) {
        try {
            Optional<PastProject> pastProject = pastProjectService.getPastProjectById(id);

            if (pastProject.isPresent()) {
                ApiResponse<PastProject> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Past project retrieved successfully",
                    pastProject.get()
                );
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<PastProject> response = new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(),
                    "Past project not found",
                    null
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            logger.error("Error retrieving past project: {}", e.getMessage(), e);
            ApiResponse<PastProject> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Failed to retrieve past project: " + e.getMessage(),
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Search past projects
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PastProject>>> searchPastProjects(
            @RequestParam(required = false) String partner,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String finalStatus,
            @RequestParam(required = false) String county,
            @RequestParam(required = false) String activityType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate archivedAfter) {

        try {
            List<PastProject> pastProjects = pastProjectService.searchPastProjects(
                partner, title, finalStatus, county, activityType, archivedAfter);

            ApiResponse<List<PastProject>> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Past projects search completed successfully",
                pastProjects
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error searching past projects: {}", e.getMessage(), e);
            ApiResponse<List<PastProject>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Failed to search past projects: " + e.getMessage(),
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get past projects with coordinates for map display
     */
    @GetMapping("/map")
    public ResponseEntity<ApiResponse<List<PastProject>>> getPastProjectsForMap() {
        try {
            List<PastProject> pastProjects = pastProjectService.getPastProjectsWithCoordinates();

            ApiResponse<List<PastProject>> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Past projects for map retrieved successfully",
                pastProjects
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error retrieving past projects for map: {}", e.getMessage(), e);
            ApiResponse<List<PastProject>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Failed to retrieve past projects for map: " + e.getMessage(),
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Create a new past project directly (Admin only)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PastProject>> createPastProject(@Valid @RequestBody PastProject pastProject) {
        try {
            logger.info("Creating new past project: {}", pastProject.getTitle());

            PastProject createdProject = pastProjectService.createPastProject(pastProject);

            ApiResponse<PastProject> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "Past project created successfully",
                createdProject
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            logger.error("Error creating past project", e);
            ApiResponse<PastProject> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Failed to create past project: " + e.getMessage(),
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Archive a completed project (Admin only)
     */
    @PostMapping("/archive/{projectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PastProject>> archiveProject(
            @PathVariable Long projectId,
            @RequestBody Map<String, String> archivalData) {

        try {
            Optional<Project> projectOpt = projectService.getProjectById(projectId);

            if (!projectOpt.isPresent()) {
                ApiResponse<PastProject> response = new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(),
                    "Project not found",
                    null
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Project project = projectOpt.get();

            // Check if project is completed
            if (!"completed".equalsIgnoreCase(project.getStatus())) {
                ApiResponse<PastProject> response = new ApiResponse<>(
                    HttpStatus.BAD_REQUEST.value(),
                    "Only completed projects can be archived",
                    null
                );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            String archivedBy = "admin"; // TODO: Get from authentication context
            String lessonsLearned = archivalData.get("lessonsLearned");
            String successFactors = archivalData.get("successFactors");
            String challenges = archivalData.get("challenges");
            String recommendations = archivalData.get("recommendations");

            PastProject pastProject = pastProjectService.archiveProject(
                project, archivedBy, lessonsLearned, successFactors, challenges, recommendations);

            ApiResponse<PastProject> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "Project archived successfully",
                pastProject
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            logger.error("Error archiving project: {}", e.getMessage(), e);
            ApiResponse<PastProject> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Failed to archive project: " + e.getMessage(),
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Update past project (for adding learning information)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PastProject>> updatePastProject(
            @PathVariable Long id,
            @Valid @RequestBody PastProject pastProject) {

        try {
            if (!id.equals(pastProject.getId())) {
                ApiResponse<PastProject> response = new ApiResponse<>(
                    HttpStatus.BAD_REQUEST.value(),
                    "ID mismatch",
                    null
                );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            PastProject updatedPastProject = pastProjectService.updatePastProject(pastProject);

            ApiResponse<PastProject> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Past project updated successfully",
                updatedPastProject
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error updating past project: {}", e.getMessage(), e);
            ApiResponse<PastProject> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Failed to update past project: " + e.getMessage(),
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get past projects statistics
     */
    @GetMapping("/stats/status")
    public ResponseEntity<ApiResponse<List<Object[]>>> getPastProjectsByStatus() {
        try {
            List<Object[]> stats = pastProjectService.getPastProjectsByStatus();

            ApiResponse<List<Object[]>> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Past projects status statistics retrieved successfully",
                stats
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error retrieving past projects statistics: {}", e.getMessage(), e);
            ApiResponse<List<Object[]>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Failed to retrieve past projects statistics: " + e.getMessage(),
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}