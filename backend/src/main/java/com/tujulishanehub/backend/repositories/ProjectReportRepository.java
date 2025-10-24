package com.tujulishanehub.backend.repositories;

import com.tujulishanehub.backend.models.ProjectReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectReportRepository extends JpaRepository<ProjectReport, Long> {
    
    /**
     * Find reports by project ID
     */
    List<ProjectReport> findByProjectId(Long projectId);
    
    /**
     * Find reports by project ID with pagination
     */
    Page<ProjectReport> findByProjectId(Long projectId, Pageable pageable);
    
    /**
     * Find reports by report status
     */
    List<ProjectReport> findByReportStatus(ProjectReport.ReportStatus reportStatus);
    
    /**
     * Find reports by report status with pagination
     */
    Page<ProjectReport> findByReportStatus(ProjectReport.ReportStatus reportStatus, Pageable pageable);
    
    /**
     * Find reports by report type
     */
    List<ProjectReport> findByReportType(ProjectReport.ReportType reportType);
    
    /**
     * Find reports by report type with pagination
     */
    Page<ProjectReport> findByReportType(ProjectReport.ReportType reportType, Pageable pageable);
    
    /**
     * Find published reports
     */
    @Query("SELECT r FROM ProjectReport r WHERE r.reportStatus = 'PUBLISHED' ORDER BY r.publishedAt DESC")
    List<ProjectReport> findPublishedReports();
    
    /**
     * Find published reports with pagination
     */
    @Query("SELECT r FROM ProjectReport r WHERE r.reportStatus = 'PUBLISHED' ORDER BY r.publishedAt DESC")
    Page<ProjectReport> findPublishedReports(Pageable pageable);
    
    /**
     * Find reports submitted for review
     */
    @Query("SELECT r FROM ProjectReport r WHERE r.reportStatus IN ('SUBMITTED', 'UNDER_REVIEW') ORDER BY r.submittedAt ASC")
    List<ProjectReport> findReportsForReview();
    
    /**
     * Find reports by submitted user
     */
    List<ProjectReport> findBySubmittedBy(Long userId);
    
    /**
     * Find reports by submitted user with pagination
     */
    Page<ProjectReport> findBySubmittedBy(Long userId, Pageable pageable);
    
    /**
     * Search reports by title containing keyword
     */
    @Query("SELECT r FROM ProjectReport r WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ProjectReport> searchByTitleContaining(@Param("keyword") String keyword);
    
    /**
     * Search reports by title containing keyword with pagination
     */
    @Query("SELECT r FROM ProjectReport r WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<ProjectReport> searchByTitleContaining(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * Search reports by content containing keyword
     */
    @Query("SELECT r FROM ProjectReport r WHERE LOWER(r.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(r.summary) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ProjectReport> searchByContentContaining(@Param("keyword") String keyword);
    
    /**
     * Find reports by project and report type
     */
    List<ProjectReport> findByProjectIdAndReportType(Long projectId, ProjectReport.ReportType reportType);
    
    /**
     * Find reports by project and report status
     */
    List<ProjectReport> findByProjectIdAndReportStatus(Long projectId, ProjectReport.ReportStatus reportStatus);
    
    /**
     * Find reports published between dates
     */
    @Query("SELECT r FROM ProjectReport r WHERE r.reportStatus = 'PUBLISHED' AND r.publishedAt BETWEEN :startDate AND :endDate ORDER BY r.publishedAt DESC")
    List<ProjectReport> findPublishedReportsBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find reports by project theme (through project relationship)
     */
    @Query("SELECT DISTINCT r FROM ProjectReport r JOIN r.project p JOIN p.themes pta WHERE pta.projectTheme = :projectTheme AND r.reportStatus = 'PUBLISHED'")
    List<ProjectReport> findPublishedReportsByProjectTheme(@Param("projectTheme") String projectTheme);
    
    /**
     * Count reports by status
     */
    Long countByReportStatus(ProjectReport.ReportStatus reportStatus);
    
    /**
     * Count reports by type
     */
    Long countByReportType(ProjectReport.ReportType reportType);
    
    /**
     * Count reports by project
     */
    Long countByProjectId(Long projectId);
    
    /**
     * Find latest report for a project
     */
    @Query("SELECT r FROM ProjectReport r WHERE r.project.id = :projectId ORDER BY r.createdAt DESC LIMIT 1")
    Optional<ProjectReport> findLatestReportByProjectId(@Param("projectId") Long projectId);
    
    /**
     * Find completion reports for projects
     */
    @Query("SELECT r FROM ProjectReport r WHERE r.reportType = 'COMPLETION' AND r.reportStatus = 'PUBLISHED' ORDER BY r.publishedAt DESC")
    List<ProjectReport> findCompletionReports();
    
    /**
     * Find completion reports with pagination
     */
    @Query("SELECT r FROM ProjectReport r WHERE r.reportType = 'COMPLETION' AND r.reportStatus = 'PUBLISHED' ORDER BY r.publishedAt DESC")
    Page<ProjectReport> findCompletionReports(Pageable pageable);
    
    /**
     * Search reports by multiple criteria
     */
    @Query("SELECT r FROM ProjectReport r WHERE " +
           "(:projectId IS NULL OR r.project.id = :projectId) AND " +
           "(:reportType IS NULL OR r.reportType = :reportType) AND " +
           "(:reportStatus IS NULL OR r.reportStatus = :reportStatus) AND " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "  LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "  LOWER(r.summary) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY r.createdAt DESC")
    List<ProjectReport> searchReports(
        @Param("projectId") Long projectId,
        @Param("reportType") ProjectReport.ReportType reportType,
        @Param("reportStatus") ProjectReport.ReportStatus reportStatus,
        @Param("keyword") String keyword
    );
    
    /**
     * Search reports with pagination
     */
    @Query("SELECT r FROM ProjectReport r WHERE " +
           "(:projectId IS NULL OR r.project.id = :projectId) AND " +
           "(:reportType IS NULL OR r.reportType = :reportType) AND " +
           "(:reportStatus IS NULL OR r.reportStatus = :reportStatus) AND " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "  LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "  LOWER(r.summary) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY r.createdAt DESC")
    Page<ProjectReport> searchReports(
        @Param("projectId") Long projectId,
        @Param("reportType") ProjectReport.ReportType reportType,
        @Param("reportStatus") ProjectReport.ReportStatus reportStatus,
        @Param("keyword") String keyword,
        Pageable pageable
    );
    
    /**
     * Check if project has completion report
     */
    @Query("SELECT COUNT(r) > 0 FROM ProjectReport r WHERE r.project.id = :projectId AND r.reportType = 'COMPLETION'")
    boolean hasCompletionReport(@Param("projectId") Long projectId);
    
    /**
     * Check if project has published reports
     */
    @Query("SELECT COUNT(r) > 0 FROM ProjectReport r WHERE r.project.id = :projectId AND r.reportStatus = 'PUBLISHED'")
    boolean hasPublishedReports(@Param("projectId") Long projectId);
}