package com.tujulishanehub.backend.repositories;

import com.tujulishanehub.backend.models.ApprovalStatus;
import com.tujulishanehub.backend.models.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    
    /**
     * Find organization by name (case-insensitive)
     */
    Optional<Organization> findByNameIgnoreCase(String name);
    
    /**
     * Find organizations by approval status
     */
    List<Organization> findByApprovalStatus(ApprovalStatus approvalStatus);
    
    /**
     * Find organizations by approval status with pagination
     */
    Page<Organization> findByApprovalStatus(ApprovalStatus approvalStatus, Pageable pageable);
    
    /**
     * Find organizations by type
     */
    List<Organization> findByOrganizationType(Organization.OrganizationType organizationType);
    
    /**
     * Find organizations by type with pagination
     */
    Page<Organization> findByOrganizationType(Organization.OrganizationType organizationType, Pageable pageable);
    
    /**
     * Find approved organizations
     */
    @Query("SELECT o FROM Organization o WHERE o.approvalStatus = 'APPROVED'")
    List<Organization> findApprovedOrganizations();
    
    /**
     * Find pending organizations for admin review
     */
    @Query("SELECT o FROM Organization o WHERE o.approvalStatus = 'PENDING' ORDER BY o.createdAt ASC")
    List<Organization> findPendingOrganizations();
    
    /**
     * Search organizations by name containing keyword (case-insensitive)
     */
    @Query("SELECT o FROM Organization o WHERE LOWER(o.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Organization> searchByNameContaining(@Param("keyword") String keyword);
    
    /**
     * Search organizations by name containing keyword with pagination
     */
    @Query("SELECT o FROM Organization o WHERE LOWER(o.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Organization> searchByNameContaining(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * Count organizations by approval status
     */
    Long countByApprovalStatus(ApprovalStatus approvalStatus);
    
    /**
     * Count organizations by type
     */
    Long countByOrganizationType(Organization.OrganizationType organizationType);
    
    /**
     * Find organizations by contact email
     */
    Optional<Organization> findByContactEmail(String contactEmail);
    
    /**
     * Find organizations by registration number
     */
    Optional<Organization> findByRegistrationNumber(String registrationNumber);
    
    /**
     * Check if organization name exists (case-insensitive)
     */
    boolean existsByNameIgnoreCase(String name);
    
    /**
     * Check if contact email exists
     */
    boolean existsByContactEmail(String contactEmail);
    
    /**
     * Check if registration number exists
     */
    boolean existsByRegistrationNumber(String registrationNumber);
}