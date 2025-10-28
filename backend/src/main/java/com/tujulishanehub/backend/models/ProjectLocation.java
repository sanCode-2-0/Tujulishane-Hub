package com.tujulishanehub.backend.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

@Entity
@Table(name = "project_locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Project project;

    @Column(nullable = false)
    private String county;

    @Column(name = "sub_county")
    private String subCounty;

    @Column(name = "maps_address", columnDefinition = "TEXT")
    private String mapsAddress;

    // Geographic coordinates
    private Double latitude;
    private Double longitude;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
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

    // Helper method to get full location string
    public String getFullLocationString() {
        StringBuilder sb = new StringBuilder();
        if (county != null) {
            sb.append(county);
        }
        if (subCounty != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(subCounty);
        }
        return sb.toString();
    }
}