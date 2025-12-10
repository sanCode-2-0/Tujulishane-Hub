package com.tujulishanehub.backend.repositories;

import com.tujulishanehub.backend.models.ReviewerThematicArea;
import com.tujulishanehub.backend.models.ProjectTheme;
import com.tujulishanehub.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewerThematicAreaRepository extends JpaRepository<ReviewerThematicArea, Long> {
    
    /**
     * Find all thematic area assignments for a specific user
     */
    List<ReviewerThematicArea> findByUser(User user);
    
    /**
     * Find all thematic area assignments by user ID
     */
    List<ReviewerThematicArea> findByUserId(Long userId);
    
    /**
     * Find all reviewers assigned to a specific thematic area
     */
    List<ReviewerThematicArea> findByThematicArea(ProjectTheme thematicArea);
    
    /**
     * Find specific assignment
     */
    Optional<ReviewerThematicArea> findByUserIdAndThematicArea(Long userId, ProjectTheme thematicArea);
    
    /**
     * Check if a user is assigned to a specific thematic area
     */
    boolean existsByUserIdAndThematicArea(Long userId, ProjectTheme thematicArea);
    
    /**
     * Delete all assignments for a user
     */
    void deleteByUserId(Long userId);
    
    /**
     * Delete specific assignment
     */
    void deleteByUserIdAndThematicArea(Long userId, ProjectTheme thematicArea);
    
    /**
     * Get all users assigned to a thematic area with SUPER_ADMIN_REVIEWER role
     */
    @Query("SELECT DISTINCT rta.user FROM ReviewerThematicArea rta " +
           "WHERE rta.thematicArea = :thematicArea " +
           "AND rta.user.role = 'SUPER_ADMIN_REVIEWER'")
    List<User> findReviewersByThematicArea(@Param("thematicArea") ProjectTheme thematicArea);
}
