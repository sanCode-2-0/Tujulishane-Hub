package com.tujulishanehub.backend.services;

import com.tujulishanehub.backend.models.PastProject;
import com.tujulishanehub.backend.models.Project;
import com.tujulishanehub.backend.models.ProjectReport;
import com.tujulishanehub.backend.repositories.PastProjectRepository;
import com.tujulishanehub.backend.repositories.ProjectReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class PastProjectService {

    private static final Logger logger = LoggerFactory.getLogger(PastProjectService.class);

    @Autowired
    private PastProjectRepository pastProjectRepository;

    @Autowired
    private ProjectReportRepository projectReportRepository;

    /**
     * Archive a completed project to the past projects repository
     */
    public PastProject archiveProject(Project project, String archivedBy, String lessonsLearned,
                                    String successFactors, String challenges, String recommendations) {
        logger.info("Archiving project: {} (ID: {})", project.getTitle(), project.getId());

        PastProject pastProject = new PastProject();
        // Copy all fields from Project to PastProject
        pastProject.setPartner(project.getPartner());
        pastProject.setTitle(project.getTitle());
        pastProject.setProjectTheme(project.getProjectTheme());
        pastProject.setProjectCategory(project.getProjectCategory());
        pastProject.setStartDate(project.getStartDate());
        pastProject.setEndDate(project.getEndDate());
        pastProject.setActivityType(project.getActivityType());
        pastProject.setCounty(project.getCounty());
        pastProject.setSubCounty(project.getSubCounty());
        pastProject.setMapsAddress(project.getMapsAddress());
        pastProject.setContactPersonName(project.getContactPersonName());
        pastProject.setContactPersonRole(project.getContactPersonRole());
        pastProject.setContactPersonEmail(project.getContactPersonEmail());
        pastProject.setObjectives(project.getObjectives());
        pastProject.setBudget(project.getBudget());
        pastProject.setLatitude(project.getLatitude());
        pastProject.setLongitude(project.getLongitude());
        pastProject.setCreatedAt(project.getCreatedAt());
        pastProject.setUpdatedAt(project.getUpdatedAt());
        pastProject.setCompletedAt(project.getCompletedAt());

        // Set archival metadata
        pastProject.setArchivedBy(archivedBy);
        pastProject.setFinalStatus(project.getStatus());
        pastProject.setCompletionPercentage(project.getCompletionPercentage());

        // Set learning fields
        pastProject.setLessonsLearned(lessonsLearned);
        pastProject.setSuccessFactors(successFactors);
        pastProject.setChallenges(challenges);
        pastProject.setRecommendations(recommendations);

        PastProject savedPastProject = pastProjectRepository.save(pastProject);

        // Move associated reports to past project
        Set<ProjectReport> reports = project.getReports();
        if (reports != null && !reports.isEmpty()) {
            for (ProjectReport report : reports) {
                report.setPastProject(savedPastProject);
                report.setProject(null); // Remove link to active project
                projectReportRepository.save(report);
            }
        }

        logger.info("Project archived successfully with ID: {}", savedPastProject.getId());
        return savedPastProject;
    }

    /**
     * Get all past projects with pagination
     */
    public Page<PastProject> getPastProjects(Pageable pageable) {
        return pastProjectRepository.findAll(pageable);
    }

    /**
     * Get past project by ID
     */
    public Optional<PastProject> getPastProjectById(Long id) {
        return pastProjectRepository.findById(id);
    }

    /**
     * Search past projects by criteria
     */
    public List<PastProject> searchPastProjects(String partner, String title, String finalStatus,
                                              String county, String activityType, LocalDate archivedAfter) {
        return pastProjectRepository.searchPastProjects(partner, title, finalStatus, county, activityType, archivedAfter);
    }

    /**
     * Get past projects with coordinates for map display
     */
    public List<PastProject> getPastProjectsWithCoordinates() {
        return pastProjectRepository.findPastProjectsWithCoordinates();
    }

    /**
     * Get past projects within geographic bounds
     */
    public List<PastProject> getPastProjectsInBoundingBox(Double minLat, Double maxLat, Double minLng, Double maxLng) {
        return pastProjectRepository.findPastProjectsInBoundingBox(minLat, maxLat, minLng, maxLng);
    }

    /**
     * Get statistics for past projects
     */
    public List<Object[]> getPastProjectsByStatus() {
        return pastProjectRepository.countPastProjectsByStatus();
    }

    public List<Object[]> getPastProjectsByCounty() {
        return pastProjectRepository.countPastProjectsByCounty();
    }

    /**
     * Create a new past project directly
     */
    public PastProject createPastProject(PastProject pastProject) {
        logger.info("Creating new past project: {}", pastProject.getTitle());

        // Set default values if not provided
        if (pastProject.getArchivedAt() == null) {
            pastProject.setArchivedAt(LocalDateTime.now());
        }
        if (pastProject.getArchivedBy() == null) {
            pastProject.setArchivedBy("admin");
        }
        if (pastProject.getFinalStatus() == null) {
            pastProject.setFinalStatus("completed");
        }

        return pastProjectRepository.save(pastProject);
    }

    /**
     * Update past project (for adding learning information)
     */
    public PastProject updatePastProject(PastProject pastProject) {
        logger.info("Updating past project: {} (ID: {})", pastProject.getTitle(), pastProject.getId());
        return pastProjectRepository.save(pastProject);
    }

    /**
     * Delete past project
     */
    public void deletePastProject(Long id) {
        logger.info("Deleting past project with ID: {}", id);
        pastProjectRepository.deleteById(id);
    }
}