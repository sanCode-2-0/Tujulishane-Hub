package com.tujulishanehub.backend.repositories;

import com.tujulishanehub.backend.models.UserDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDocumentRepository extends JpaRepository<UserDocument, Long> {
    List<UserDocument> findByUserId(Long userId);

    /**
     * Get all user documents with relationships loaded for admin dashboard
     */
    @Query("SELECT d FROM UserDocument d JOIN FETCH d.user u JOIN FETCH d.uploadedBy ub WHERE d.status = 'ACTIVE'")
    List<UserDocument> findAllActiveWithRelationships();
}
