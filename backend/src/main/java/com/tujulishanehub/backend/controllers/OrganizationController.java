package com.tujulishanehub.backend.controllers;

import com.tujulishanehub.backend.models.ApprovalStatus;
import com.tujulishanehub.backend.models.Organization;
import com.tujulishanehub.backend.models.User;
import com.tujulishanehub.backend.payload.ApiResponse;
import com.tujulishanehub.backend.services.OrganizationService;
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
@RequestMapping("/api/organizations")
public class OrganizationController {
    
    private static final Logger logger = LoggerFactory.getLogger(OrganizationController.class);
    
    @Autowired
    private OrganizationService organizationService;
    
    @Autowired
    private UserService userService;
    
    /**
     * Create a new organization (Authenticated users only)
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Organization>> createOrganization(@Valid @RequestBody Organization organization) {
        try {
            logger.info("Creating new organization: {}", organization.getName());
            
            Organization createdOrganization = organizationService.createOrganization(organization);
            
            ApiResponse<Organization> response = new ApiResponse<>(
                HttpStatus.CREATED.value(), 
                "Organization created successfully", 
                createdOrganization
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (RuntimeException e) {
            logger.error("Error creating organization: {}", e.getMessage());
            ApiResponse<Organization> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(), 
                e.getMessage(), 
                null
            );
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Unexpected error creating organization: {}", e.getMessage(), e);
            ApiResponse<Organization> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to create organization", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get all organizations with pagination
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllOrganizations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Organization> organizationPage = organizationService.getAllOrganizations(pageable);
            
            Map<String, Object> data = new HashMap<>();
            data.put("organizations", organizationPage.getContent());
            data.put("currentPage", organizationPage.getNumber());
            data.put("totalItems", organizationPage.getTotalElements());
            data.put("totalPages", organizationPage.getTotalPages());
            data.put("hasNext", organizationPage.hasNext());
            data.put("hasPrevious", organizationPage.hasPrevious());
            
            ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Organizations retrieved successfully", 
                data
            );
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving organizations: {}", e.getMessage(), e);
            ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to retrieve organizations", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get organization by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Organization>> getOrganizationById(@PathVariable Long id) {
        try {
            Optional<Organization> organizationOpt = organizationService.getOrganizationById(id);
            
            if (organizationOpt.isPresent()) {
                ApiResponse<Organization> response = new ApiResponse<>(
                    HttpStatus.OK.value(), 
                    "Organization retrieved successfully", 
                    organizationOpt.get()
                );
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<Organization> response = new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(), 
                    "Organization not found", 
                    null
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
        } catch (Exception e) {
            logger.error("Error retrieving organization with ID {}: {}", id, e.getMessage(), e);
            ApiResponse<Organization> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to retrieve organization", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Search organizations by name
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Organization>>> searchOrganizations(
            @RequestParam String keyword) {
        
        try {
            List<Organization> organizations = organizationService.searchOrganizationsByName(keyword);
            
            ApiResponse<List<Organization>> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Organizations search completed", 
                organizations
            );
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error searching organizations: {}", e.getMessage(), e);
            ApiResponse<List<Organization>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to search organizations", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get approved organizations
     */
    @GetMapping("/approved")
    public ResponseEntity<ApiResponse<List<Organization>>> getApprovedOrganizations() {
        try {
            List<Organization> organizations = organizationService.getApprovedOrganizations();
            
            ApiResponse<List<Organization>> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Approved organizations retrieved successfully", 
                organizations
            );
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving approved organizations: {}", e.getMessage(), e);
            ApiResponse<List<Organization>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to retrieve approved organizations", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Update organization (Organization members or Admin only)
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Organization>> updateOrganization(
            @PathVariable Long id, 
            @Valid @RequestBody Organization organizationDetails) {
        
        try {
            // TODO: Add authorization check to ensure user belongs to organization or is admin
            
            Organization updatedOrganization = organizationService.updateOrganization(id, organizationDetails);
            
            ApiResponse<Organization> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Organization updated successfully", 
                updatedOrganization
            );
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            logger.error("Error updating organization: {}", e.getMessage());
            ApiResponse<Organization> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(), 
                e.getMessage(), 
                null
            );
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Unexpected error updating organization: {}", e.getMessage(), e);
            ApiResponse<Organization> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to update organization", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // ==================== ADMIN ENDPOINTS ====================
    
    /**
     * Get all pending organizations (Admin only)
     */
    @GetMapping("/admin/pending")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<Organization>>> getPendingOrganizations() {
        try {
            List<Organization> pendingOrganizations = organizationService.getPendingOrganizations();
            
            ApiResponse<List<Organization>> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Pending organizations retrieved successfully", 
                pendingOrganizations
            );
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving pending organizations: {}", e.getMessage(), e);
            ApiResponse<List<Organization>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to retrieve pending organizations", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get organizations by approval status (Admin only)
     */
    @GetMapping("/admin/status/{status}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<Organization>>> getOrganizationsByStatus(@PathVariable String status) {
        try {
            ApprovalStatus approvalStatus = ApprovalStatus.valueOf(status.toUpperCase());
            List<Organization> organizations = organizationService.getOrganizationsByApprovalStatus(approvalStatus);
            
            ApiResponse<List<Organization>> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Organizations retrieved successfully", 
                organizations
            );
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            ApiResponse<List<Organization>> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(), 
                "Invalid approval status. Valid values: PENDING, APPROVED, REJECTED", 
                null
            );
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Error retrieving organizations by status {}: {}", status, e.getMessage(), e);
            ApiResponse<List<Organization>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to retrieve organizations", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Approve organization (Admin only)
     */
    @PostMapping("/admin/approve/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> approveOrganization(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String adminEmail = auth.getName();
            User admin = userService.getUserByEmail(adminEmail);
            
            boolean success = organizationService.approveOrganization(id, admin.getId());
            
            if (success) {
                ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.OK.value(), 
                    "Organization approved successfully", 
                    null
                );
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(), 
                    "Organization not found", 
                    null
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
        } catch (Exception e) {
            logger.error("Error approving organization {}: {}", id, e.getMessage(), e);
            ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to approve organization", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Reject organization (Admin only)
     */
    @PostMapping("/admin/reject/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> rejectOrganization(
            @PathVariable Long id, 
            @RequestBody Map<String, String> payload) {
        
        try {
            String reason = payload.getOrDefault("reason", "No reason provided");
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String adminEmail = auth.getName();
            User admin = userService.getUserByEmail(adminEmail);
            
            boolean success = organizationService.rejectOrganization(id, admin.getId(), reason);
            
            if (success) {
                ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.OK.value(), 
                    "Organization rejected successfully", 
                    null
                );
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(), 
                    "Organization not found", 
                    null
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
        } catch (Exception e) {
            logger.error("Error rejecting organization {}: {}", id, e.getMessage(), e);
            ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to reject organization", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get organization statistics (Admin only)
     */
    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<OrganizationService.OrganizationStats>> getOrganizationStats() {
        try {
            OrganizationService.OrganizationStats stats = organizationService.getOrganizationStats();
            
            ApiResponse<OrganizationService.OrganizationStats> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Organization statistics retrieved successfully", 
                stats
            );
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving organization statistics: {}", e.getMessage(), e);
            ApiResponse<OrganizationService.OrganizationStats> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to retrieve organization statistics", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Delete organization (Admin only)
     */
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteOrganization(@PathVariable Long id) {
        try {
            boolean success = organizationService.deleteOrganization(id);
            
            if (success) {
                ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.OK.value(), 
                    "Organization deleted successfully", 
                    null
                );
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(), 
                    "Organization not found", 
                    null
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
        } catch (Exception e) {
            logger.error("Error deleting organization {}: {}", id, e.getMessage(), e);
            ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to delete organization", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}