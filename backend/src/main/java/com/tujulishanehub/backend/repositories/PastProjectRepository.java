package com.tujulishanehub.backend.repositories;

import com.tujulishanehub.backend.models.PastProject;
import com.tujulishanehub.backend.models.ProjectTheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PastProjectRepository extends JpaRepository<PastProject, Long> {

    // Find past projects by partner
    List<PastProject> findByPartnerContainingIgnoreCase(String partner);

    // Find past projects by title
    List<PastProject> findByTitleContainingIgnoreCase(String title);

    // Find past projects by final status
    List<PastProject> findByFinalStatus(String finalStatus);

    // Find past projects by county
    List<PastProject> findByCountyContainingIgnoreCase(String county);

    // Find past projects by sub-county
    List<PastProject> findBySubCountyContainingIgnoreCase(String subCounty);

    // Find past projects by activity type
    List<PastProject> findByActivityTypeContainingIgnoreCase(String activityType);

    // Find past projects by project theme
    List<PastProject> findByProjectTheme(ProjectTheme projectTheme);

    // Find past projects by project theme with case-insensitive string search
    @Query("SELECT p FROM PastProject p WHERE LOWER(CAST(p.projectTheme AS string)) LIKE LOWER(CONCAT('%', :theme, '%'))")
    List<PastProject> findByProjectThemeContaining(@Param("theme") String theme);

    // Find past projects by date range
    List<PastProject> findByStartDateBetween(LocalDate startDate, LocalDate endDate);

    // Find past projects that ended after a specific date
    List<PastProject> findByEndDateAfter(LocalDate date);

    // Find past projects with coordinates (for map display)
    @Query("SELECT p FROM PastProject p WHERE p.latitude IS NOT NULL AND p.longitude IS NOT NULL")
    List<PastProject> findPastProjectsWithCoordinates();

    // Find past projects within a geographic bounding box
    @Query("SELECT p FROM PastProject p WHERE p.latitude BETWEEN :minLat AND :maxLat AND p.longitude BETWEEN :minLng AND :maxLng")
    List<PastProject> findPastProjectsInBoundingBox(
        @Param("minLat") Double minLatitude,
        @Param("maxLat") Double maxLatitude,
        @Param("minLng") Double minLongitude,
        @Param("maxLng") Double maxLongitude
    );

    // Search past projects by multiple criteria
    @Query("SELECT p FROM PastProject p WHERE " +
           "(:partner IS NULL OR :partner = '' OR LOWER(CAST(p.partner AS string)) LIKE LOWER(CONCAT('%', :partner, '%'))) AND " +
           "(:title IS NULL OR :title = '' OR LOWER(CAST(p.title AS string)) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:finalStatus IS NULL OR :finalStatus = '' OR p.finalStatus = :finalStatus) AND " +
           "(:county IS NULL OR :county = '' OR LOWER(CAST(p.county AS string)) LIKE LOWER(CONCAT('%', :county, '%'))) AND " +
           "(:activityType IS NULL OR :activityType = '' OR LOWER(CAST(p.activityType AS string)) LIKE LOWER(CONCAT('%', :activityType, '%'))) AND " +
           "(:archivedAfter IS NULL OR p.archivedAt >= :archivedAfter)")
    List<PastProject> searchPastProjects(
        @Param("partner") String partner,
        @Param("title") String title,
        @Param("finalStatus") String finalStatus,
        @Param("county") String county,
        @Param("activityType") String activityType,
        @Param("archivedAfter") LocalDate archivedAfter
    );

    // Count past projects by final status
    @Query("SELECT p.finalStatus, COUNT(p) FROM PastProject p GROUP BY p.finalStatus")
    List<Object[]> countPastProjectsByStatus();

    // Count past projects by county
    @Query("SELECT p.county, COUNT(p) FROM PastProject p WHERE p.county IS NOT NULL GROUP BY p.county")
    List<Object[]> countPastProjectsByCounty();

    // Find past projects archived within last N years
    @Query("SELECT p FROM PastProject p WHERE p.archivedAt >= :sinceDate ORDER BY p.archivedAt DESC")
    List<PastProject> findRecentlyArchived(@Param("sinceDate") LocalDate sinceDate);
}