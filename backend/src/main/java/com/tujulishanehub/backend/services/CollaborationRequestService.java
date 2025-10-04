package com.tujulishanehub.backend.services;

import com.tujulishanehub.backend.models.Announcement;
import com.tujulishanehub.backend.models.CollaborationRequest;
import com.tujulishanehub.backend.models.CollaborationRequestStatus;
import com.tujulishanehub.backend.models.User;
import com.tujulishanehub.backend.repositories.AnnouncementRepository;
import com.tujulishanehub.backend.repositories.CollaborationRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CollaborationRequestService {
    
    private static final Logger logger = LoggerFactory.getLogger(CollaborationRequestService.class);
    
    @Autowired
    private CollaborationRequestRepository collaborationRequestRepository;
    
    @Autowired
    private AnnouncementRepository announcementRepository;
    
    @Autowired
    private UserService userService;
    
    /**
     * Create collaboration request
     */
    public CollaborationRequest createCollaborationRequest(Long announcementId, 
            CollaborationRequest request, String userEmail) {
        
        logger.info("Creating collaboration request for announcement ID: {} by user: {}", 
                   announcementId, userEmail);
        
        User user = userService.getUserByEmail(userEmail);
        Optional<Announcement> announcementOpt = announcementRepository.findById(announcementId);
        
        if (!announcementOpt.isPresent()) {
            throw new RuntimeException("Announcement not found");
        }
        
        Announcement announcement = announcementOpt.get();
        
        // Check if user already has a request for this announcement
        if (collaborationRequestRepository.existsByAnnouncementAndRequestingUser(announcement, user)) {
            throw new RuntimeException("You have already submitted a request for this announcement");
        }
        
        // Can't request to collaborate on own announcement
        if (announcement.getCreatedBy().equals(user)) {
            throw new RuntimeException("You cannot request collaboration on your own announcement");
        }
        
        request.setAnnouncement(announcement);
        request.setRequestingUser(user);
        request.setRequestingOrganization(user.getOrganization());
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        
        CollaborationRequest saved = collaborationRequestRepository.save(request);
        logger.info("Collaboration request created successfully with ID: {}", saved.getId());
        return saved;
    }
    
    /**
     * Get pending collaboration requests (for MOH review)
     */
    public List<CollaborationRequest> getPendingRequests() {
        logger.info("Retrieving all pending collaboration requests");
        return collaborationRequestRepository.findByStatusOrderByCreatedAtDesc(
            CollaborationRequestStatus.PENDING);
    }
    
    /**
     * Get requests for user's projects/announcements
     */
    public List<CollaborationRequest> getRequestsForUserProjects(String userEmail) {
        logger.info("Retrieving collaboration requests for user projects: {}", userEmail);
        return collaborationRequestRepository.findRequestsForUserProjects(userEmail);
    }
    
    /**
     * Get requests by requesting user
     */
    public List<CollaborationRequest> getMyCollaborationRequests(String userEmail) {
        logger.info("Retrieving collaboration requests submitted by user: {}", userEmail);
        User user = userService.getUserByEmail(userEmail);
        return collaborationRequestRepository.findByRequestingUserOrderByCreatedAtDesc(user);
    }
    
    /**
     * Approve collaboration request (MOH only)
     */
    public boolean approveCollaborationRequest(Long requestId, String reviewerEmail, String notes) {
        logger.info("Approving collaboration request ID: {} by reviewer: {}", requestId, reviewerEmail);
        
        User reviewer = userService.getUserByEmail(reviewerEmail);
        if (!reviewer.isSuperAdmin()) {
            throw new RuntimeException("Only MOH administrators can approve collaboration requests");
        }
        
        Optional<CollaborationRequest> requestOpt = collaborationRequestRepository.findById(requestId);
        if (requestOpt.isPresent()) {
            CollaborationRequest request = requestOpt.get();
            request.setStatus(CollaborationRequestStatus.APPROVED);
            request.setReviewedBy(reviewer);
            request.setReviewedAt(LocalDateTime.now());
            request.setReviewNotes(notes);
            request.setUpdatedAt(LocalDateTime.now());
            
            collaborationRequestRepository.save(request);
            
            logger.info("Collaboration request {} approved successfully", requestId);
            // TODO: Send notification emails to involved parties
            
            return true;
        }
        
        logger.warn("Collaboration request {} not found", requestId);
        return false;
    }
    
    /**
     * Decline collaboration request (MOH only)
     */
    public boolean declineCollaborationRequest(Long requestId, String reviewerEmail, String notes) {
        logger.info("Declining collaboration request ID: {} by reviewer: {}", requestId, reviewerEmail);
        
        User reviewer = userService.getUserByEmail(reviewerEmail);
        if (!reviewer.isSuperAdmin()) {
            throw new RuntimeException("Only MOH administrators can decline collaboration requests");
        }
        
        Optional<CollaborationRequest> requestOpt = collaborationRequestRepository.findById(requestId);
        if (requestOpt.isPresent()) {
            CollaborationRequest request = requestOpt.get();
            request.setStatus(CollaborationRequestStatus.DECLINED);
            request.setReviewedBy(reviewer);
            request.setReviewedAt(LocalDateTime.now());
            request.setReviewNotes(notes);
            request.setUpdatedAt(LocalDateTime.now());
            
            collaborationRequestRepository.save(request);
            
            logger.info("Collaboration request {} declined", requestId);
            // TODO: Send notification emails to involved parties
            
            return true;
        }
        
        logger.warn("Collaboration request {} not found", requestId);
        return false;
    }
    
    /**
     * Get collaboration request by ID
     */
    public Optional<CollaborationRequest> getCollaborationRequestById(Long id) {
        return collaborationRequestRepository.findById(id);
    }
}