package com.tujulishanehub.backend.repositories;

import com.tujulishanehub.backend.models.ProjectDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface ProjectDocumentRepository extends JpaRepository<ProjectDocument, Long> {

    /**
     * Get document metadata WITHOUT loading the actual file data (LOB)
     * This is MUCH faster than loading the entire entity
     */
    @Query("SELECT new map(d.id as id, d.fileName as fileName, d.fileType as fileType, d.fileSize as size) " +
           "FROM ProjectDocument d WHERE d.project.id = :projectId")
    List<Map<String, Object>> findMetadataByProjectId(@Param("projectId") Long projectId);

    /**
     * Get all project documents with relationships loaded for admin dashboard
     */
    @Query("SELECT d FROM ProjectDocument d JOIN FETCH d.project p JOIN FETCH d.uploadedBy u WHERE d.status = 'ACTIVE'")
    List<ProjectDocument> findAllActiveWithRelationships();
}

