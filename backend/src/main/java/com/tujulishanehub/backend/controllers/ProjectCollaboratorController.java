package com.tujulishanehub.backend.controllers;

import com.tujulishanehub.backend.models.CollaboratorRole;
import com.tujulishanehub.backend.models.ProjectCollaborator;
import com.tujulishanehub.backend.payload.ApiResponse;
import com.tujulishanehub.backend.services.ProjectCollaboratorService;
import com.tujulishanehub.backend.services.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectCollaboratorController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectCollaboratorController.class);

    @Autowired
    private ProjectCollaboratorService collaboratorService;

    @Autowired
    private ProjectService projectService;

    /**
     * Get all collaborators for a specific project
     */
    @GetMapping("/{projectId}/collaborators")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<ProjectCollaborator>>> getProjectCollaborators(
            @PathVariable Long projectId) {
        
        try {
            List<ProjectCollaborator> collaborators = collaboratorService.getProjectCollaborators(projectId);
            
            ApiResponse<List<ProjectCollaborator>> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Collaborators retrieved successfully",
                collaborators
            );
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving collaborators for project {}: {}", projectId, e.getMessage(), e);
            ApiResponse<List<ProjectCollaborator>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Failed to retrieve collaborators: " + e.getMessage(),
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get all projects where the authenticated user is a collaborator
     */
    @GetMapping("/collaborations/my-projects")
    @PreAuthorize("hasRole('PARTNER')")
    public ResponseEntity<ApiResponse<List<ProjectCollaborator>>> getMyCollaborations() {
        
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = auth.getName();
            
            List<ProjectCollaborator> collaborations = collaboratorService.getUserCollaborations(userEmail);
            
            ApiResponse<List<ProjectCollaborator>> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Your collaborations retrieved successfully",
                collaborations
            );
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving user collaborations: {}", e.getMessage(), e);
            ApiResponse<List<ProjectCollaborator>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Failed to retrieve collaborations: " + e.getMessage(),
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Add a collaborator to a project (Project owner or admin only)
     * Note: This is for manual addition. Automatic addition happens via collaboration request approval
     */
    @PostMapping("/{projectId}/collaborators")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ProjectCollaborator>> addCollaborator(
            @PathVariable Long projectId,
            @RequestParam String collaboratorEmail,
            @RequestParam CollaboratorRole role) {
        
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String addedByEmail = auth.getName();
            
            // Check if user is admin, super admin, or project owner
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SUPER_ADMIN"));
            boolean isOwner = projectService.isProjectOwner(projectId, addedByEmail);
            
            if (!isAdmin && !isOwner) {
                logger.warn("User {} attempted to add collaborator to project {} without permission", addedByEmail, projectId);
                ApiResponse<ProjectCollaborator> response = new ApiResponse<>(
                    HttpStatus.FORBIDDEN.value(),
                    "Only project owners or admins can add collaborators",
                    null
                );
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            ProjectCollaborator collaborator = collaboratorService.addCollaboratorByEmail(
                    projectId, 
                    collaboratorEmail, 
                    role, 
                    addedByEmail,
                    "Manually added by " + addedByEmail
            );
            
            ApiResponse<ProjectCollaborator> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "Collaborator added successfully",
                collaborator
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (RuntimeException e) {
            logger.error("Error adding collaborator to project {}: {}", projectId, e.getMessage());
            ApiResponse<ProjectCollaborator> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            
        } catch (Exception e) {
            logger.error("Error adding collaborator to project {}: {}", projectId, e.getMessage(), e);
            ApiResponse<ProjectCollaborator> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Failed to add collaborator: " + e.getMessage(),
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Update collaborator role (Project owner or admin only)
     */
    @PutMapping("/{projectId}/collaborators/{collaboratorId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ProjectCollaborator>> updateCollaboratorRole(
            @PathVariable Long projectId,
            @PathVariable Long collaboratorId,
            @RequestParam CollaboratorRole newRole) {
        
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = auth.getName();
            
            // Check if user is admin, super admin, or project owner
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SUPER_ADMIN"));
            boolean isOwner = projectService.isProjectOwner(projectId, userEmail);
            
            if (!isAdmin && !isOwner) {
                logger.warn("User {} attempted to update collaborator role on project {} without permission", userEmail, projectId);
                ApiResponse<ProjectCollaborator> response = new ApiResponse<>(
                    HttpStatus.FORBIDDEN.value(),
                    "Only project owners or admins can update collaborator roles",
                    null
                );
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            ProjectCollaborator updated = collaboratorService.updateCollaboratorRole(collaboratorId, newRole, userEmail);
            
            ApiResponse<ProjectCollaborator> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Collaborator role updated successfully",
                updated
            );
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            logger.error("Error updating collaborator role: {}", e.getMessage());
            ApiResponse<ProjectCollaborator> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            
        } catch (Exception e) {
            logger.error("Error updating collaborator role: {}", e.getMessage(), e);
            ApiResponse<ProjectCollaborator> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Failed to update collaborator role: " + e.getMessage(),
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Remove a collaborator from a project (Project owner or admin only)
     */
    @DeleteMapping("/{projectId}/collaborators/{collaboratorId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> removeCollaborator(
            @PathVariable Long projectId,
            @PathVariable Long collaboratorId) {
        
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = auth.getName();
            
            // Check if user is admin, super admin, or project owner
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SUPER_ADMIN"));
            boolean isOwner = projectService.isProjectOwner(projectId, userEmail);
            
            if (!isAdmin && !isOwner) {
                logger.warn("User {} attempted to remove collaborator from project {} without permission", userEmail, projectId);
                ApiResponse<Void> response = new ApiResponse<>(
                    HttpStatus.FORBIDDEN.value(),
                    "Only project owners or admins can remove collaborators",
                    null
                );
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            collaboratorService.removeCollaborator(collaboratorId, userEmail);
            
            ApiResponse<Void> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Collaborator removed successfully",
                null
            );
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            logger.error("Error removing collaborator: {}", e.getMessage());
            ApiResponse<Void> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            
        } catch (Exception e) {
            logger.error("Error removing collaborator: {}", e.getMessage(), e);
            ApiResponse<Void> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Failed to remove collaborator: " + e.getMessage(),
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
