package com.tujulishanehub.backend.repositories;

import com.tujulishanehub.backend.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    // Find projects by partner
    List<Project> findByPartnerContainingIgnoreCase(String partner);
    
    // Find projects by title
    List<Project> findByTitleContainingIgnoreCase(String title);
    
    // Find projects by status
    List<Project> findByStatus(String status);
    
    // Find projects by county
    List<Project> findByCountyContainingIgnoreCase(String county);
    
    // Find projects by sub-county
    List<Project> findBySubCountyContainingIgnoreCase(String subCounty);
    
    // Find projects by activity type
    List<Project> findByActivityTypeContainingIgnoreCase(String activityType);
    
    // Find projects by project theme
    List<Project> findByProjectThemeContainingIgnoreCase(String projectTheme);
    
    // Find projects by date range
    List<Project> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Find projects that end after a specific date
    List<Project> findByEndDateAfter(LocalDate date);
    
    // Find projects that are currently active (started but not ended)
    @Query("SELECT p FROM Project p WHERE p.startDate <= :currentDate AND (p.endDate IS NULL OR p.endDate >= :currentDate)")
    List<Project> findActiveProjects(@Param("currentDate") LocalDate currentDate);
    
    // Find projects with coordinates (for map display)
    @Query("SELECT p FROM Project p WHERE p.latitude IS NOT NULL AND p.longitude IS NOT NULL")
    List<Project> findProjectsWithCoordinates();
    
    // Find projects without coordinates (need geocoding)
    @Query("SELECT p FROM Project p WHERE (p.latitude IS NULL OR p.longitude IS NULL) AND p.mapsAddress IS NOT NULL")
    List<Project> findProjectsNeedingGeocoding();
    
    // Find projects by contact person email
    Optional<Project> findByContactPersonEmail(String email);
    
    // Find projects within a geographic bounding box
    @Query("SELECT p FROM Project p WHERE p.latitude BETWEEN :minLat AND :maxLat AND p.longitude BETWEEN :minLng AND :maxLng")
    List<Project> findProjectsInBoundingBox(
        @Param("minLat") Double minLatitude,
        @Param("maxLat") Double maxLatitude,
        @Param("minLng") Double minLongitude,
        @Param("maxLng") Double maxLongitude
    );
    
    // Search projects by multiple criteria
    @Query("SELECT p FROM Project p WHERE " +
           "(:partner IS NULL OR LOWER(p.partner) LIKE LOWER(CONCAT('%', :partner, '%'))) AND " +
           "(:title IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:county IS NULL OR LOWER(p.county) LIKE LOWER(CONCAT('%', :county, '%'))) AND " +
           "(:activityType IS NULL OR LOWER(p.activityType) LIKE LOWER(CONCAT('%', :activityType, '%')))")
    List<Project> searchProjects(
        @Param("partner") String partner,
        @Param("title") String title,
        @Param("status") String status,
        @Param("county") String county,
        @Param("activityType") String activityType
    );
    
    // Count projects by status
    @Query("SELECT p.status, COUNT(p) FROM Project p GROUP BY p.status")
    List<Object[]> countProjectsByStatus();
    
    // Count projects by county
    @Query("SELECT p.county, COUNT(p) FROM Project p WHERE p.county IS NOT NULL GROUP BY p.county")
    List<Object[]> countProjectsByCounty();
}