package com.tujulishanehub.backend.repositories;

import com.tujulishanehub.backend.models.Project;
import com.tujulishanehub.backend.models.ProjectCollaborator;
import com.tujulishanehub.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectCollaboratorRepository extends JpaRepository<ProjectCollaborator, Long> {
    
    List<ProjectCollaborator> findByProjectAndIsActive(Project project, Boolean isActive);
    
    List<ProjectCollaborator> findByUser(User user);
    
    List<ProjectCollaborator> findByUserAndIsActive(User user, Boolean isActive);
    
    Optional<ProjectCollaborator> findByProjectAndUserAndIsActive(Project project, User user, Boolean isActive);
    
    boolean existsByProjectAndUserAndIsActive(Project project, User user, Boolean isActive);
    
    @Query("SELECT pc FROM ProjectCollaborator pc WHERE pc.project.id = :projectId AND pc.isActive = true")
    List<ProjectCollaborator> findActiveCollaboratorsByProjectId(@Param("projectId") Long projectId);
    
    @Query("SELECT pc FROM ProjectCollaborator pc WHERE pc.user.email = :email AND pc.isActive = true")
    List<ProjectCollaborator> findActiveCollaborationsByUserEmail(@Param("email") String email);
}