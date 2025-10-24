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
@Table(name = "project_theme_assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectThemeAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_theme", nullable = false)
    private ProjectTheme projectTheme;

    @Column(name = "assigned_at", updatable = false)
    private LocalDateTime assignedAt;

    @PrePersist
    protected void onCreate() {
        assignedAt = LocalDateTime.now();
    }

    // Helper method to get display name
    public String getThemeDisplayName() {
        return projectTheme != null ? projectTheme.getDisplayName() : null;
    }

    // Helper method to get theme code
    public String getThemeCode() {
        return projectTheme != null ? projectTheme.getCode() : null;
    }
}