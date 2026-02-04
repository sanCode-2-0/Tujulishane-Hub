package com.tujulishanehub.backend.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "past_projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PastProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String partner;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_theme")
    private ProjectTheme projectTheme;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_category")
    private ProjectCategory projectCategory;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "activity_type")
    private String activityType;

    private String county;

    @Column(name = "sub_county")
    private String subCounty;

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

    @Column(precision = 15, scale = 2)
    private java.math.BigDecimal budget;

    // Coordinates
    private Double latitude;
    private Double longitude;

    // Original project metadata
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // Archival metadata
    @Column(name = "archived_at", nullable = false)
    private LocalDateTime archivedAt;

    @Column(name = "archived_by", nullable = false)
    private String archivedBy; // Email of user who archived

    // Archival fields for learning
    @Column(name = "final_status", nullable = false)
    private String finalStatus; // "completed", "abandoned", etc.

    @Column(name = "completion_percentage")
    private Integer completionPercentage = 100;

    @Column(columnDefinition = "TEXT")
    private String lessonsLearned;

    @Column(columnDefinition = "TEXT")
    private String successFactors;

    @Column(columnDefinition = "TEXT")
    private String challenges;

    @Column(columnDefinition = "TEXT")
    private String recommendations;

    @Column(columnDefinition = "TEXT")
    private String finalReport; // Link or summary

    // Bidirectional relationship with ProjectReports (archived reports)
    @OneToMany(mappedBy = "pastProject", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference(value = "pastproject-reports")
    private Set<ProjectReport> reports = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        archivedAt = LocalDateTime.now();
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

    // Helper method to get project duration in months
    public Integer getProjectDurationMonths() {
        if (startDate != null && endDate != null) {
            return (endDate.getYear() - startDate.getYear()) * 12 + (endDate.getMonthValue() - startDate.getMonthValue());
        }
        return null;
    }
}