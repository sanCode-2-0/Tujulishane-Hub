package com.tujulishanehub.backend.repositories;

import com.tujulishanehub.backend.models.Announcement;
import com.tujulishanehub.backend.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByAnnouncementOrderByCreatedAtAsc(Announcement announcement);
}