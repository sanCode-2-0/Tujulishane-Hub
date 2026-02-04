package com.tujulishanehub.backend.repositories;

import com.tujulishanehub.backend.models.ProjectReportDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectReportDocumentRepository extends JpaRepository<ProjectReportDocument, Long> {
    
    // Find all report documents for a specific project
    List<ProjectReportDocument> findByProjectId(Long projectId);
    
    // Count report documents for a project
    long countByProjectId(Long projectId);
}
