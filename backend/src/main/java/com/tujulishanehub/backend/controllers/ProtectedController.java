package com.tujulishanehub.backend.controllers;

import com.tujulishanehub.backend.models.ProjectDocument;
import com.tujulishanehub.backend.models.ProjectReport;
import com.tujulishanehub.backend.models.UserDocument;
import com.tujulishanehub.backend.payload.ApiResponse;
import com.tujulishanehub.backend.repositories.ProjectDocumentRepository;
import com.tujulishanehub.backend.repositories.ProjectReportRepository;
import com.tujulishanehub.backend.repositories.UserDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ProtectedController {

    @Autowired
    private ProjectDocumentRepository projectDocumentRepository;

    @Autowired
    private UserDocumentRepository userDocumentRepository;

    @Autowired
    private ProjectReportRepository projectReportRepository;

    @GetMapping("/protected")
    public ResponseEntity<ApiResponse<Object>> protectedEndpoint(Principal principal) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("user", principal != null ? principal.getName() : null);
        ApiResponse<Object> response = new ApiResponse<>(200, "Access granted to protected resource.", data);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all project documents for admin dashboard
     */
    @GetMapping("/project-documents")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('SUPER_ADMIN_REVIEWER') or hasRole('SUPER_ADMIN_APPROVER')")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllProjectDocuments() {
        try {
            List<ProjectDocument> documents = projectDocumentRepository.findAllActiveWithRelationships();

            List<Map<String, Object>> documentData = documents.stream().map(doc -> {
                Map<String, Object> data = new HashMap<>();
                data.put("id", doc.getId());
                data.put("fileName", doc.getFileName());
                data.put("project", doc.getProject() != null ? doc.getProject().getTitle() : "Unknown");
                data.put("uploadedBy", doc.getUploadedBy() != null ? doc.getUploadedBy().getName() : "Unknown");
                data.put("type", doc.getFileType());
                data.put("size", doc.getFileSize());
                data.put("status", doc.getStatus() != null ? doc.getStatus().toString() : "ACTIVE");
                data.put("date", doc.getUploadDate() != null ? doc.getUploadDate().toString() : null);
                data.put("createdAt", doc.getCreatedAt() != null ? doc.getCreatedAt().toString() : null);
                return data;
            }).collect(Collectors.toList());

            ApiResponse<List<Map<String, Object>>> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Project documents retrieved successfully",
                documentData
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse<List<Map<String, Object>>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Failed to retrieve project documents: " + e.getMessage(),
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get all user documents for admin dashboard
     */
    @GetMapping("/user-documents")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('SUPER_ADMIN_REVIEWER') or hasRole('SUPER_ADMIN_APPROVER')")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllUserDocuments() {
        try {
            List<UserDocument> documents = userDocumentRepository.findAllActiveWithRelationships();

            List<Map<String, Object>> documentData = documents.stream().map(doc -> {
                Map<String, Object> data = new HashMap<>();
                data.put("id", doc.getId());
                data.put("fileName", doc.getFileName());
                data.put("user", doc.getUser() != null ? doc.getUser().getName() : "Unknown");
                data.put("uploadedBy", doc.getUploadedBy() != null ? doc.getUploadedBy().getName() : "Unknown");
                data.put("type", doc.getFileType());
                data.put("size", doc.getFileSize());
                data.put("status", doc.getStatus() != null ? doc.getStatus().toString() : "ACTIVE");
                data.put("date", doc.getUploadDate() != null ? doc.getUploadDate().toString() : null);
                data.put("createdAt", doc.getCreatedAt() != null ? doc.getCreatedAt().toString() : null);
                return data;
            }).collect(Collectors.toList());

            ApiResponse<List<Map<String, Object>>> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "User documents retrieved successfully",
                documentData
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse<List<Map<String, Object>>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Failed to retrieve user documents: " + e.getMessage(),
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get all project reports for admin dashboard
     */
    @GetMapping("/project-reports")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('SUPER_ADMIN_REVIEWER') or hasRole('SUPER_ADMIN_APPROVER')")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllProjectReports() {
        try {
            List<ProjectReport> reports = projectReportRepository.findAllWithRelationships();

            List<Map<String, Object>> reportData = reports.stream().map(report -> {
                Map<String, Object> data = new HashMap<>();
                data.put("id", report.getId());
                data.put("title", report.getTitle());
                data.put("project", report.getProject() != null ? report.getProject().getTitle() : "Unknown");
                data.put("submittedBy", report.getSubmittedBy() != null ? report.getSubmittedBy().toString() : "Unknown");
                data.put("reportType", report.getReportType() != null ? report.getReportType().toString() : "UNKNOWN");
                data.put("reportStatus", report.getReportStatus() != null ? report.getReportStatus().toString() : "DRAFT");
                data.put("submittedAt", report.getSubmittedAt() != null ? report.getSubmittedAt().toString() : null);
                data.put("createdAt", report.getCreatedAt() != null ? report.getCreatedAt().toString() : null);
                return data;
            }).collect(Collectors.toList());

            ApiResponse<List<Map<String, Object>>> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Project reports retrieved successfully",
                reportData
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse<List<Map<String, Object>>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Failed to retrieve project reports: " + e.getMessage(),
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
