package com.tujulishanehub.backend.repositories;

import com.tujulishanehub.backend.models.Announcement;
import com.tujulishanehub.backend.models.AnnouncementStatus;
import com.tujulishanehub.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByStatusOrderByCreatedAtDesc(AnnouncementStatus status);
    List<Announcement> findByCreatedBy(User createdBy);
    List<Announcement> findByCreatedByAndStatusOrderByCreatedAtDesc(User createdBy, AnnouncementStatus status);
    List<Announcement> findByProjectIdAndStatus(Long projectId, AnnouncementStatus status);
    
    @Query("SELECT a FROM Announcement a WHERE a.status = :status AND " +
           "(a.deadline IS NULL OR a.deadline >= CURRENT_DATE) " +
           "ORDER BY a.createdAt DESC")
    List<Announcement> findActiveAnnouncements(@Param("status") AnnouncementStatus status);
}