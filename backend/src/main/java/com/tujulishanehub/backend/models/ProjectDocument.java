package com.tujulishanehub.backend.models;

import jakarta.persistence.*;

@Entity
public class ProjectDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String fileType;
    private Long fileSize; // Size in bytes

    @Lob
    @Column(length = 10485760) // 10MB max, adjust as needed
    private byte[] data;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public byte[] getData() { return data; }
    public void setData(byte[] data) { this.data = data; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
}
