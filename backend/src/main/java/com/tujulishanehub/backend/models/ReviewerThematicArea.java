package com.tujulishanehub.backend.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

/**
 * Join entity for many-to-many relationship between Reviewers and Thematic Areas
 * Allows one reviewer to be assigned to multiple thematic areas
 */
@Entity
@Table(name = "reviewer_thematic_areas", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "thematic_area"}))
@Data
@EqualsAndHashCode(exclude = {"user"})
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ReviewerThematicArea {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "thematic_area", nullable = false)
    private ProjectTheme thematicArea;
    
    @Column(name = "assigned_at", updatable = false)
    private LocalDateTime assignedAt;
    
    @Column(name = "assigned_by")
    private Long assignedBy;
    
    @PrePersist
    protected void onCreate() {
        assignedAt = LocalDateTime.now();
    }
    
    // Constructor for easy creation
    public ReviewerThematicArea(User user, ProjectTheme thematicArea, Long assignedBy) {
        this.user = user;
        this.thematicArea = thematicArea;
        this.assignedBy = assignedBy;
    }
}
