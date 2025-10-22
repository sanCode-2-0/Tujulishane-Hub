package com.tujulishanehub.backend.services;

import com.tujulishanehub.backend.models.PastProject;
import com.tujulishanehub.backend.models.Project;
import com.tujulishanehub.backend.repositories.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProjectService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private GeocodingService geocodingService;
    
    @Autowired
    private PastProjectService pastProjectService;
    
    /**
     * Create a new project with automatic coordinate extraction
     */
    public Project createProject(Project project) {
        logger.info("Creating new project: {}", project.getTitle());
        
        // Extract coordinates from maps_address if provided
        if (project.getMapsAddress() != null && !project.getMapsAddress().trim().isEmpty()) {
            extractAndSetCoordinates(project);
        }
        
        // Set default status if not provided
        if (project.getStatus() == null || project.getStatus().trim().isEmpty()) {
            project.setStatus("pending");
        }
        
        Project savedProject = projectRepository.save(project);
        logger.info("Project created successfully with ID: {}", savedProject.getId());
        return savedProject;
    }
    
    /**
     * Update an existing project
     */
    public Project updateProject(Long id, Project updatedProject, String modifiedByEmail) {
        logger.info("Updating project with ID: {} by user: {}", id, modifiedByEmail);
        
        Optional<Project> existingProjectOpt = projectRepository.findById(id);
        if (existingProjectOpt.isEmpty()) {
            throw new RuntimeException("Project not found with ID: " + id);
        }
        
        Project existingProject = existingProjectOpt.get();
        
        // Track who modified the project
        existingProject.setLastModifiedBy(modifiedByEmail);
        existingProject.setLastModifiedAt(java.time.LocalDateTime.now());
        
        // Update fields
        updateProjectFields(existingProject, updatedProject);
        
        // Re-extract coordinates if maps_address has changed
        if (updatedProject.getMapsAddress() != null && 
            !updatedProject.getMapsAddress().equals(existingProject.getMapsAddress())) {
            extractAndSetCoordinates(existingProject);
        }
        
        Project savedProject = projectRepository.save(existingProject);
        logger.info("Project updated successfully: {} by {}", savedProject.getId(), modifiedByEmail);
        return savedProject;
    }
    
    /**
     * Get project by ID
     */
    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }
    
    /**
     * Get all projects
     */
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }
    
    /**
     * Get projects with pagination
     */
    public Page<Project> getProjects(Pageable pageable) {
        return projectRepository.findAll(pageable);
    }

    /**
     * Get past projects (completed and stalled) with pagination
     */
    public Page<Project> getPastProjects(Pageable pageable) {
        return projectRepository.findByStatusIn(Arrays.asList("completed", "stalled"), pageable);
    }

    /**
     * Delete project
     */
    public void deleteProject(Long id) {
        logger.info("Deleting project with ID: {}", id);
        if (!projectRepository.existsById(id)) {
            throw new RuntimeException("Project not found with ID: " + id);
        }
        projectRepository.deleteById(id);
        logger.info("Project deleted successfully: {}", id);
    }
    
    /**
     * Search projects by various criteria
     */
    public List<Project> searchProjects(String partner, String title, String status, 
                                       String county, String activityType) {
        return projectRepository.searchProjects(partner, title, status, county, activityType);
    }
    
    /**
     * Get projects by status
     */
    public List<Project> getProjectsByStatus(String status) {
        return projectRepository.findByStatus(status);
    }
    
    /**
     * Get projects by county
     */
    public List<Project> getProjectsByCounty(String county) {
        return projectRepository.findByCountyContainingIgnoreCase(county);
    }
    
    /**
     * Get projects by partner
     */
    public List<Project> getProjectsByPartner(String partner) {
        return projectRepository.findByPartnerContainingIgnoreCase(partner);
    }
    
    /**
     * Get projects by date range
     */
    public List<Project> getProjectsByDateRange(LocalDate startDate, LocalDate endDate) {
        return projectRepository.findByStartDateBetween(startDate, endDate);
    }
    
    /**
     * Get currently active projects
     */
    public List<Project> getActiveProjects() {
        return projectRepository.findActiveProjects(LocalDate.now());
    }
    
    /**
     * Get projects with coordinates (for map display)
     */
    public List<Project> getProjectsWithCoordinates() {
        return projectRepository.findProjectsWithCoordinates();
    }
    
    /**
     * Get projects within a geographic bounding box
     */
    public List<Project> getProjectsInBoundingBox(Double minLat, Double maxLat, 
                                                 Double minLng, Double maxLng) {
        return projectRepository.findProjectsInBoundingBox(minLat, maxLat, minLng, maxLng);
    }
    
    /**
     * Batch process projects that need geocoding
     */
    public void processProjectsNeedingGeocoding() {
        logger.info("Starting batch geocoding process");
        List<Project> projectsNeedingGeocoding = projectRepository.findProjectsNeedingGeocoding();
        
        logger.info("Found {} projects needing geocoding", projectsNeedingGeocoding.size());
        
        for (Project project : projectsNeedingGeocoding) {
            try {
                extractAndSetCoordinates(project);
                projectRepository.save(project);
                logger.debug("Geocoded project: {}", project.getTitle());
                
                // Add a small delay to be respectful to geocoding services
                Thread.sleep(100);
            } catch (Exception e) {
                logger.error("Failed to geocode project {}: {}", project.getId(), e.getMessage());
            }
        }
        
        logger.info("Batch geocoding process completed");
    }
    
    /**
     * Get project statistics
     */
    public ProjectStatistics getProjectStatistics() {
        List<Object[]> statusCounts = projectRepository.countProjectsByStatus();
        List<Object[]> countyCounts = projectRepository.countProjectsByCounty();
        long totalProjects = projectRepository.count();
        long projectsWithCoordinates = projectRepository.findProjectsWithCoordinates().size();
        
        return new ProjectStatistics(statusCounts, countyCounts, totalProjects, projectsWithCoordinates);
    }
    
    /**
     * Archive a completed project to past projects repository
     */
    public PastProject archiveProject(Long projectId, String archivedBy, String lessonsLearned,
                                    String successFactors, String challenges, String recommendations) {
        logger.info("Archiving project with ID: {}", projectId);
        
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (!projectOpt.isPresent()) {
            throw new RuntimeException("Project not found with ID: " + projectId);
        }
        
        Project project = projectOpt.get();
        
        // Validate that project is completed
        if (!"completed".equalsIgnoreCase(project.getStatus())) {
            throw new RuntimeException("Only completed projects can be archived");
        }
        
        // Archive the project
        PastProject pastProject = pastProjectService.archiveProject(
            project, archivedBy, lessonsLearned, successFactors, challenges, recommendations);
        
        // Remove the project from active projects (optional - could keep for history)
        // projectRepository.delete(project);
        
        return pastProject;
    }
    
    /**
     * Get projects eligible for archival (completed and older than specified years)
     */
    public List<Project> getProjectsEligibleForArchival(int yearsOld) {
        LocalDate cutoffDate = LocalDate.now().minusYears(yearsOld);
        return projectRepository.findByEndDateBeforeAndStatus(cutoffDate, "completed");
    }
    
    /**
     * Extract coordinates from maps_address and set lat/lng fields
     */
    private void extractAndSetCoordinates(Project project) {
        try {
            GeocodingService.CoordinateResult result = geocodingService.extractCoordinates(project.getMapsAddress());
            
            if (result.isValid()) {
                project.setLatitude(result.getLatitude());
                project.setLongitude(result.getLongitude());
                logger.debug("Extracted coordinates for project '{}': {}", 
                    project.getTitle(), result);
            } else {
                logger.warn("Could not extract coordinates for project '{}': {}", 
                    project.getTitle(), result.getMessage());
            }
        } catch (Exception e) {
            logger.error("Error extracting coordinates for project '{}': {}", 
                project.getTitle(), e.getMessage(), e);
        }
    }
    
    /**
     * Update project fields from another project object
     */
    private void updateProjectFields(Project existing, Project updated) {
        if (updated.getPartner() != null) existing.setPartner(updated.getPartner());
        if (updated.getTitle() != null) existing.setTitle(updated.getTitle());
        if (updated.getProjectTheme() != null) existing.setProjectTheme(updated.getProjectTheme());
        if (updated.getProjectCategory() != null) existing.setProjectCategory(updated.getProjectCategory());
        if (updated.getStartDate() != null) existing.setStartDate(updated.getStartDate());
        if (updated.getEndDate() != null) existing.setEndDate(updated.getEndDate());
        if (updated.getActivityType() != null) existing.setActivityType(updated.getActivityType());
        if (updated.getCounty() != null) existing.setCounty(updated.getCounty());
        if (updated.getSubCounty() != null) existing.setSubCounty(updated.getSubCounty());
        if (updated.getMapsAddress() != null) existing.setMapsAddress(updated.getMapsAddress());
        if (updated.getContactPersonName() != null) existing.setContactPersonName(updated.getContactPersonName());
        if (updated.getContactPersonRole() != null) existing.setContactPersonRole(updated.getContactPersonRole());
        if (updated.getContactPersonEmail() != null) existing.setContactPersonEmail(updated.getContactPersonEmail());
        if (updated.getObjectives() != null) existing.setObjectives(updated.getObjectives());
        if (updated.getBudget() != null) existing.setBudget(updated.getBudget());
        if (updated.getStatus() != null) existing.setStatus(updated.getStatus());
        
        // Allow manual override of coordinates
        if (updated.getLatitude() != null) existing.setLatitude(updated.getLatitude());
        if (updated.getLongitude() != null) existing.setLongitude(updated.getLongitude());
    }
    
    /**
     * Create a project by partner
     */
    public Project createProjectByPartner(Project project, String userEmail) {
        logger.info("Creating project by partner: {} for email: {}", project.getTitle(), userEmail);
        
        // Set partner email for tracking ownership
        project.setPartner(userEmail);
        
        // Set contact person email if not provided
        if (project.getContactPersonEmail() == null) {
            project.setContactPersonEmail(userEmail);
        }
        
        return createProject(project);
    }
    
    /**
     * Approve project (Admin only)
     */
    public boolean approveProject(Long projectId, Long approvedBy) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            project.setApprovalStatus(com.tujulishanehub.backend.models.ApprovalStatus.APPROVED);
            project.setApprovedBy(approvedBy);
            project.setApprovedAt(java.time.LocalDateTime.now());
            project.setRejectionReason(null);
            project.setStatus("active"); // Make project active when approved
            projectRepository.save(project);
            logger.info("Project {} approved by admin {}", projectId, approvedBy);
            return true;
        }
        return false;
    }
    
    /**
     * Reject project (Admin only)
     */
    public boolean rejectProject(Long projectId, Long rejectedBy, String reason) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            project.setApprovalStatus(com.tujulishanehub.backend.models.ApprovalStatus.REJECTED);
            project.setApprovedBy(rejectedBy);
            project.setRejectionReason(reason);
            project.setStatus("rejected"); // Set project status to rejected
            projectRepository.save(project);
            logger.info("Project {} rejected by admin {} with reason: {}", projectId, rejectedBy, reason);
            return true;
        }
        return false;
    }
    
    /**
     * Get projects by approval status (Admin only)
     */
    public List<Project> getProjectsByApprovalStatus(com.tujulishanehub.backend.models.ApprovalStatus approvalStatus) {
        return projectRepository.findByApprovalStatus(approvalStatus);
    }
    
    /**
     * Get projects by partner email
     */
    public List<Project> getProjectsByPartnerEmail(String email) {
        logger.info("Fetching projects for partner email: {}", email);
        
        // Try both partner field and contactPersonEmail for backward compatibility
        List<Project> projectsByPartner = projectRepository.findByPartner(email);
        List<Project> projectsByContact = projectRepository.findByContactPersonEmail(email);
        
        // Combine and deduplicate
        java.util.Set<Project> combinedProjects = new java.util.HashSet<>(projectsByPartner);
        combinedProjects.addAll(projectsByContact);
        
        List<Project> result = new java.util.ArrayList<>(combinedProjects);
        logger.info("Found {} projects for email: {}", result.size(), email);
        
        return result;
    }
    
    /**
     * Check if user is the owner of a project
     */
    public boolean isProjectOwner(Long projectId, String userEmail) {
        Optional<Project> project = projectRepository.findById(projectId);
        return project.isPresent() && project.get().getContactPersonEmail().equals(userEmail);
    }
    
    /**
     * Get admin dashboard statistics
     */
    public java.util.Map<String, Object> getAdminDashboardStats() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        
        // Project counts by status
        stats.put("totalProjects", projectRepository.count());
        stats.put("pendingProjects", projectRepository.countByApprovalStatus(com.tujulishanehub.backend.models.ApprovalStatus.PENDING));
        stats.put("submittedProjects", projectRepository.countByApprovalStatus(com.tujulishanehub.backend.models.ApprovalStatus.SUBMITTED));
        stats.put("approvedProjects", projectRepository.countByApprovalStatus(com.tujulishanehub.backend.models.ApprovalStatus.APPROVED));
        stats.put("rejectedProjects", projectRepository.countByApprovalStatus(com.tujulishanehub.backend.models.ApprovalStatus.REJECTED));
        
        // Recent projects
        List<Project> recentProjects = projectRepository.findTop10ByOrderByCreatedAtDesc();
        stats.put("recentProjects", recentProjects);
        
        return stats;
    }

    /**
     * Mark a project as completed
     */
    public Project completeProject(Long projectId, String completedBy) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            throw new RuntimeException("Project not found");
        }

        Project project = projectOpt.get();
        if (!"active".equalsIgnoreCase(project.getStatus())) {
            throw new RuntimeException("Only active projects can be marked as completed");
        }

        project.markAsCompleted();
        projectRepository.save(project);

        logger.info("Project {} marked as completed by {}", projectId, completedBy);
        return project;
    }

    /**
     * Mark a project as stalled
     */
    public Project stallProject(Long projectId, String stalledBy) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            throw new RuntimeException("Project not found");
        }

        Project project = projectOpt.get();
        if (!"active".equalsIgnoreCase(project.getStatus())) {
            throw new RuntimeException("Only active projects can be marked as stalled");
        }

        project.setStatus("stalled");
        projectRepository.save(project);

        logger.info("Project {} marked as stalled by {}", projectId, stalledBy);
        return project;
    }

    /**
     * Statistics class for project data
     */
    public static class ProjectStatistics {
        private final List<Object[]> statusCounts;
        private final List<Object[]> countyCounts;
        private final long totalProjects;
        private final long projectsWithCoordinates;
        
        public ProjectStatistics(List<Object[]> statusCounts, List<Object[]> countyCounts, 
                               long totalProjects, long projectsWithCoordinates) {
            this.statusCounts = statusCounts;
            this.countyCounts = countyCounts;
            this.totalProjects = totalProjects;
            this.projectsWithCoordinates = projectsWithCoordinates;
        }
        
        public List<Object[]> getStatusCounts() { return statusCounts; }
        public List<Object[]> getCountyCounts() { return countyCounts; }
        public long getTotalProjects() { return totalProjects; }
        public long getProjectsWithCoordinates() { return projectsWithCoordinates; }
        public double getCoordinatesCoveragePercentage() {
            return totalProjects > 0 ? (double) projectsWithCoordinates / totalProjects * 100 : 0;
        }
    }
}