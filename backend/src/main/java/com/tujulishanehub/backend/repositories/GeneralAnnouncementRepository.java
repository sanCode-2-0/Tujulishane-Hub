package com.tujulishanehub.backend.repositories;

import com.tujulishanehub.backend.models.GeneralAnnouncement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeneralAnnouncementRepository extends JpaRepository<GeneralAnnouncement, Long> {
    List<GeneralAnnouncement> findAllByOrderByCreatedAtDesc();
}