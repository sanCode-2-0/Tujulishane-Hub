package com.tujulishanehub.backend.services;

import com.tujulishanehub.backend.models.ApprovalStatus;
import com.tujulishanehub.backend.models.Organization;
import com.tujulishanehub.backend.repositories.OrganizationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrganizationService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrganizationService.class);
    
    @Autowired
    private OrganizationRepository organizationRepository;
    
    /**
     * Create a new organization
     */
    public Organization createOrganization(Organization organization) {
        logger.info("Creating new organization: {}", organization.getName());
        
        // Check if organization name already exists
        if (organizationRepository.existsByNameIgnoreCase(organization.getName())) {
            throw new RuntimeException("Organization with this name already exists");
        }
        
        // Check if contact email already exists
        if (organization.getContactEmail() != null && 
            organizationRepository.existsByContactEmail(organization.getContactEmail())) {
            throw new RuntimeException("Organization with this contact email already exists");
        }
        
        // Check if registration number already exists
        if (organization.getRegistrationNumber() != null && 
            organizationRepository.existsByRegistrationNumber(organization.getRegistrationNumber())) {
            throw new RuntimeException("Organization with this registration number already exists");
        }
        
        // Set default values
        organization.setApprovalStatus(ApprovalStatus.PENDING);
        
        return organizationRepository.save(organization);
    }
    
    /**
     * Get organization by ID
     */
    public Optional<Organization> getOrganizationById(Long id) {
        return organizationRepository.findById(id);
    }
    
    /**
     * Get organization by name
     */
    public Optional<Organization> getOrganizationByName(String name) {
        return organizationRepository.findByNameIgnoreCase(name);
    }
    
    /**
     * Find an existing organization by name or create a new one
     * Returns the organization ID
     */
    public Long findOrCreateOrganization(String organizationName) {
        if (organizationName == null || organizationName.trim().isEmpty()) {
            return null;
        }
        
        String trimmedName = organizationName.trim();
        
        // Try to find existing organization
        Optional<Organization> existingOrg = organizationRepository.findByNameIgnoreCase(trimmedName);
        if (existingOrg.isPresent()) {
            logger.info("Found existing organization: {} (ID: {})", trimmedName, existingOrg.get().getId());
            return existingOrg.get().getId();
        }
        
        // Create new organization
        Organization newOrg = new Organization();
        newOrg.setName(trimmedName);
        newOrg.setApprovalStatus(ApprovalStatus.PENDING); // New organizations need approval
        newOrg.setOrganizationType(Organization.OrganizationType.OTHER); // Default to OTHER
        newOrg.setCreatedAt(LocalDateTime.now());
        
        Organization savedOrg = organizationRepository.save(newOrg);
        logger.info("Created new organization: {} (ID: {})", trimmedName, savedOrg.getId());
        
        return savedOrg.getId();
    }
    
    /**
     * Get all organizations with pagination
     */
    public Page<Organization> getAllOrganizations(Pageable pageable) {
        return organizationRepository.findAll(pageable);
    }
    
    /**
     * Get organizations by approval status
     */
    public List<Organization> getOrganizationsByApprovalStatus(ApprovalStatus approvalStatus) {
        return organizationRepository.findByApprovalStatus(approvalStatus);
    }
    
    /**
     * Get organizations by approval status with pagination
     */
    public Page<Organization> getOrganizationsByApprovalStatus(ApprovalStatus approvalStatus, Pageable pageable) {
        return organizationRepository.findByApprovalStatus(approvalStatus, pageable);
    }
    
    /**
     * Get organizations by type
     */
    public List<Organization> getOrganizationsByType(Organization.OrganizationType organizationType) {
        return organizationRepository.findByOrganizationType(organizationType);
    }
    
    /**
     * Get approved organizations
     */
    public List<Organization> getApprovedOrganizations() {
        return organizationRepository.findApprovedOrganizations();
    }
    
    /**
     * Get pending organizations for admin review
     */
    public List<Organization> getPendingOrganizations() {
        return organizationRepository.findPendingOrganizations();
    }
    
    /**
     * Search organizations by name
     */
    public List<Organization> searchOrganizationsByName(String keyword) {
        return organizationRepository.searchByNameContaining(keyword);
    }
    
    /**
     * Search organizations by name with pagination
     */
    public Page<Organization> searchOrganizationsByName(String keyword, Pageable pageable) {
        return organizationRepository.searchByNameContaining(keyword, pageable);
    }
    
    /**
     * Update organization
     */
    public Organization updateOrganization(Long id, Organization organizationDetails) {
        logger.info("Updating organization with ID: {}", id);
        
        Organization existingOrganization = organizationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Organization not found with ID: " + id));
        
        // Check if new name conflicts with existing organizations (excluding current one)
        if (!existingOrganization.getName().equalsIgnoreCase(organizationDetails.getName()) &&
            organizationRepository.existsByNameIgnoreCase(organizationDetails.getName())) {
            throw new RuntimeException("Organization with this name already exists");
        }
        
        // Update fields
        existingOrganization.setName(organizationDetails.getName());
        existingOrganization.setOrganizationType(organizationDetails.getOrganizationType());
        existingOrganization.setDescription(organizationDetails.getDescription());
        existingOrganization.setContactEmail(organizationDetails.getContactEmail());
        existingOrganization.setContactPhone(organizationDetails.getContactPhone());
        existingOrganization.setAddress(organizationDetails.getAddress());
        existingOrganization.setWebsiteUrl(organizationDetails.getWebsiteUrl());
        existingOrganization.setRegistrationNumber(organizationDetails.getRegistrationNumber());
        
        // Update logo if provided
        if (organizationDetails.getLogoData() != null && organizationDetails.getLogoData().length > 0) {
            existingOrganization.setLogoData(organizationDetails.getLogoData());
            existingOrganization.setLogoContentType(organizationDetails.getLogoContentType());
            logger.info("Logo updated for organization: {}", id);
        }
        
        return organizationRepository.save(existingOrganization);
    }
    
    /**
     * Approve organization (Admin only)
     */
    public boolean approveOrganization(Long organizationId, Long adminId) {
        logger.info("Approving organization with ID: {} by admin: {}", organizationId, adminId);
        
        Optional<Organization> organizationOpt = organizationRepository.findById(organizationId);
        if (organizationOpt.isEmpty()) {
            return false;
        }
        
        Organization organization = organizationOpt.get();
        organization.setApprovalStatus(ApprovalStatus.APPROVED);
        organization.setApprovedBy(adminId);
        organization.setApprovedAt(LocalDateTime.now());
        organization.setRejectionReason(null); // Clear any previous rejection reason
        
        organizationRepository.save(organization);
        logger.info("Organization {} approved successfully", organization.getName());
        return true;
    }
    
    /**
     * Reject organization (Admin only)
     */
    public boolean rejectOrganization(Long organizationId, Long adminId, String reason) {
        logger.info("Rejecting organization with ID: {} by admin: {}", organizationId, adminId);
        
        Optional<Organization> organizationOpt = organizationRepository.findById(organizationId);
        if (organizationOpt.isEmpty()) {
            return false;
        }
        
        Organization organization = organizationOpt.get();
        organization.setApprovalStatus(ApprovalStatus.REJECTED);
        organization.setApprovedBy(adminId);
        organization.setApprovedAt(LocalDateTime.now());
        organization.setRejectionReason(reason);
        
        organizationRepository.save(organization);
        logger.info("Organization {} rejected: {}", organization.getName(), reason);
        return true;
    }
    
    /**
     * Delete organization
     */
    public boolean deleteOrganization(Long id) {
        logger.info("Deleting organization with ID: {}", id);
        
        if (!organizationRepository.existsById(id)) {
            return false;
        }
        
        organizationRepository.deleteById(id);
        logger.info("Organization with ID {} deleted successfully", id);
        return true;
    }
    
    /**
     * Get organization statistics
     */
    public OrganizationStats getOrganizationStats() {
        Long totalOrganizations = organizationRepository.count();
        Long pendingOrganizations = organizationRepository.countByApprovalStatus(ApprovalStatus.PENDING);
        Long approvedOrganizations = organizationRepository.countByApprovalStatus(ApprovalStatus.APPROVED);
        Long rejectedOrganizations = organizationRepository.countByApprovalStatus(ApprovalStatus.REJECTED);
        
        return new OrganizationStats(totalOrganizations, pendingOrganizations, 
                                   approvedOrganizations, rejectedOrganizations);
    }
    
    /**
     * Check if organization name exists
     */
    public boolean organizationNameExists(String name) {
        return organizationRepository.existsByNameIgnoreCase(name);
    }
    
    /**
     * Check if contact email exists
     */
    public boolean contactEmailExists(String email) {
        return organizationRepository.existsByContactEmail(email);
    }
    
    /**
     * Inner class for organization statistics
     */
    public static class OrganizationStats {
        private final Long total;
        private final Long pending;
        private final Long approved;
        private final Long rejected;
        
        public OrganizationStats(Long total, Long pending, Long approved, Long rejected) {
            this.total = total;
            this.pending = pending;
            this.approved = approved;
            this.rejected = rejected;
        }
        
        public Long getTotal() { return total; }
        public Long getPending() { return pending; }
        public Long getApproved() { return approved; }
        public Long getRejected() { return rejected; }
    }
}