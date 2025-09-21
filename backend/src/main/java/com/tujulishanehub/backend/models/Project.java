package com.tujulishanehub.backend.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String partner;
    
    @Column(nullable = false)
    private String title;
    
    @Column(name = "project_theme")
    private String projectTheme;
    
    @Column(name = "start_date")
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @Column(name = "activity_type")
    private String activityType;
    
    private String county;
    
    @Column(name = "sub_county")
    private String subCounty;
    
    @Column(name = "project_area")
    private String projectArea;
    
    @Column(name = "maps_address", columnDefinition = "TEXT")
    private String mapsAddress;
    
    @Column(name = "contact_person_name")
    private String contactPersonName;
    
    @Column(name = "contact_person_role")
    private String contactPersonRole;
    
    @Column(name = "contact_person_email")
    private String contactPersonEmail;
    
    @Column(columnDefinition = "TEXT")
    private String objectives;
    
    // New latitude and longitude fields extracted from maps_address
    private Double latitude;
    
    private Double longitude;
    
    // Additional metadata fields
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    private String status; // e.g., "active", "pending", "stalled", "completed", "abandoned"
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Helper method to check if coordinates are available
    public boolean hasCoordinates() {
        return latitude != null && longitude != null;
    }
    
    // Helper method to get formatted coordinate string
    public String getCoordinatesString() {
        if (hasCoordinates()) {
            return String.format("%.6f,%.6f", latitude, longitude);
        }
        return null;
    }
}