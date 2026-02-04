package com.tujulishanehub.backend.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "project_report_document")
public class ProjectReportDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String fileType;
    
    @Column(name = "file_size")
    private Long fileSize; // in bytes

    @Column(columnDefinition = "bytea")
    private byte[] data;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;
    
    @Column(name = "uploaded_by")
    private String uploadedBy; // email of uploader
    
    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }

    // Getters and setters
    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }

    public String getFileName() { 
        return fileName; 
    }
    
    public void setFileName(String fileName) { 
        this.fileName = fileName; 
    }

    public String getFileType() { 
        return fileType; 
    }
    
    public void setFileType(String fileType) { 
        this.fileType = fileType; 
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public byte[] getData() { 
        return data; 
    }
    
    public void setData(byte[] data) { 
        this.data = data; 
    }

    public Project getProject() { 
        return project; 
    }
    
    public void setProject(Project project) { 
        this.project = project; 
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
