package com.tujulishanehub.backend.repositories;

import com.tujulishanehub.backend.models.Announcement;
import com.tujulishanehub.backend.models.CollaborationRequest;
import com.tujulishanehub.backend.models.CollaborationRequestStatus;
import com.tujulishanehub.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollaborationRequestRepository extends JpaRepository<CollaborationRequest, Long> {
    List<CollaborationRequest> findByAnnouncementIdOrderByCreatedAtDesc(Long announcementId);
    List<CollaborationRequest> findByRequestingUserOrderByCreatedAtDesc(User requestingUser);
    List<CollaborationRequest> findByStatusOrderByCreatedAtDesc(CollaborationRequestStatus status);
    @Query("SELECT cr FROM CollaborationRequest cr " +
           "LEFT JOIN FETCH cr.requestingUser " +
           "LEFT JOIN FETCH cr.requestingOrganization " +
           "LEFT JOIN FETCH cr.announcement " +
           "ORDER BY cr.createdAt DESC")
    List<CollaborationRequest> findAllByOrderByCreatedAtDesc();
    
    boolean existsByAnnouncementAndRequestingUser(Announcement announcement, User requestingUser);
    
    @Query("SELECT cr FROM CollaborationRequest cr JOIN cr.announcement a JOIN a.project p " +
           "WHERE p.contactPersonEmail = :userEmail OR a.createdBy.email = :userEmail " +
           "ORDER BY cr.createdAt DESC")
    List<CollaborationRequest> findRequestsForUserProjects(@Param("userEmail") String userEmail);
}