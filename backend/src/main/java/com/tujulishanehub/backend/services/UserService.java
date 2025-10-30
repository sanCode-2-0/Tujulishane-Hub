package com.tujulishanehub.backend.services;

import com.tujulishanehub.backend.models.ApprovalStatus;
import com.tujulishanehub.backend.models.Organization;
import com.tujulishanehub.backend.models.User;
import com.tujulishanehub.backend.models.Announcement;
import com.tujulishanehub.backend.models.CollaborationRequest;
import com.tujulishanehub.backend.models.ProjectCollaborator;
import com.tujulishanehub.backend.repositories.UserRepository;
import com.tujulishanehub.backend.repositories.AnnouncementRepository;
import com.tujulishanehub.backend.repositories.CollaborationRequestRepository;
import com.tujulishanehub.backend.repositories.ProjectCollaboratorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private CollaborationRequestRepository collaborationRequestRepository;

    @Autowired
    private ProjectCollaboratorRepository projectCollaboratorRepository;

    // Register user (name + email + optional organization). Create INACTIVE account and send OTP
    public void registerUser(String name, String email, Long organizationId) {
        registerUser(name, email, organizationId, User.Role.PARTNER, null);
    }
    
    // Register donor account
    public void registerDonor(String name, String email, Long organizationId) {
        registerUser(name, email, organizationId, User.Role.DONOR, null);
    }
    
    // Register partner account with optional parent donor
    public void registerPartner(String name, String email, Long organizationId, Long parentDonorId) {
        registerUser(name, email, organizationId, User.Role.PARTNER, parentDonorId);
    }
    
    // Main registration method with role and parent donor support
    private void registerUser(String name, String email, Long organizationId, User.Role role, Long parentDonorId) {
        // Check if user already exists
        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isPresent()) {
            throw new RuntimeException("User with this email already exists");
        }
        
        // Validate parent donor if provided
        if (parentDonorId != null) {
            Optional<User> parentDonor = userRepository.findById(parentDonorId);
            if (!parentDonor.isPresent() || parentDonor.get().getRole() != User.Role.DONOR) {
                throw new RuntimeException("Invalid parent donor ID");
            }
        }
        
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setStatus("ACTIVE");  // Set to ACTIVE for new registrations
        user.setApprovalStatus(com.tujulishanehub.backend.models.ApprovalStatus.PENDING);
        user.setRole(role);
        
        // Set parent donor for partners
        if (role == User.Role.PARTNER && parentDonorId != null) {
            User parentDonor = userRepository.findById(parentDonorId).orElse(null);
            user.setParentDonor(parentDonor);
        }
        
        String otp = generateOtp();
        user.setOtp(otp);
        user.setOtpExpiry(java.time.LocalDateTime.now().plusMinutes(10));
        if (organizationId != null) {
            organizationService.getOrganizationById(organizationId)
                .ifPresent(user::setOrganization);
        }
        userRepository.save(user);
        
        // Send OTP via email
        String accountType = role == User.Role.DONOR ? "Donor" : "Partner";
        String subject = "Tujulishane Hub - " + accountType + " Account Email Verification";
        String body = String.format(
            "Hello %s,\n\n" +
            "Thank you for registering as a %s with Tujulishane Hub!\n\n" +
            "Your verification OTP is: %s\n\n" +
            "This OTP will expire in 10 minutes.\n\n" +
            "Please note: Your account is pending approval by the MOH administrator. " +
            "You will receive another email once your account is approved.\n\n" +
            "Best regards,\n" +
            "Tujulishane Hub Team",
            name, accountType.toLowerCase(), otp
        );
        emailService.sendEmail(email, subject, body);
    }
    
    // Backward compatibility method
    public void registerUser(String name, String email) {
        registerUser(name, email, null);
    }

    // Verify OTP and mark email as verified (but user still needs admin approval)
    public boolean verifyOtp(String email, String otp) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return false;
        
        User user = userOpt.get();
        
        // Check if OTP matches and hasn't expired
        if (otp != null && otp.equals(user.getOtp()) && 
            user.getOtpExpiry() != null && 
            user.getOtpExpiry().isAfter(java.time.LocalDateTime.now())) {
            
            // Mark email as verified but keep account INACTIVE until admin approves
            user.setEmailVerified(true);
            user.setVerified(true);
            user.setOtp(null);
            user.setOtpExpiry(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // Login using email + otp. User must be ACTIVE and APPROVED.
    public boolean loginWithOtp(String email, String otp) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return false;
        
        User user = userOpt.get();
        
        // User must be ACTIVE and APPROVED
        if (!"ACTIVE".equals(user.getStatus()) || !user.isApproved()) {
            return false;
        }
        
        // Verify OTP matches and hasn't expired
        if (otp != null && otp.equals(user.getOtp()) && 
            user.getOtpExpiry() != null && 
            user.getOtpExpiry().isAfter(java.time.LocalDateTime.now())) {
            
            user.setLastLogin(java.time.LocalDateTime.now());
            user.setOtp(null);
            user.setOtpExpiry(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    private String generateOtp() {
        // Generate a random 6-digit OTP
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        // Organization is now eagerly loaded due to FetchType.EAGER
        // But we can explicitly access it to ensure it's loaded
        if (user != null && user.getOrganization() != null) {
            // This will trigger loading if for some reason it wasn't loaded
            user.getOrganization().getName();
        }
        return user;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // Helper to (re)send login OTP for ACTIVE and APPROVED users
    public void sendLoginOtp(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        User user = userOpt.get();
        
        // User must be ACTIVE and APPROVED to receive login OTP
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new RuntimeException("Account not active");
        }
        
        if (!user.isApproved()) {
            throw new RuntimeException("Account not approved by administrator");
        }
        
        // Generate and save new OTP
        String otp = generateOtp();
        user.setOtp(otp);
        user.setOtpExpiry(java.time.LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);
        
        // Send OTP via email
        String subject = "Tujulishane Hub - Login OTP";
        String body = String.format(
            "Hello %s,\n\n" +
            "Your login OTP is: %s\n\n" +
            "This OTP will expire in 10 minutes.\n\n" +
            "If you did not request this, please ignore this email.\n\n" +
            "Best regards,\n" +
            "Tujulishane Hub Team",
            user.getName(), otp
        );
        emailService.sendEmail(email, subject, body);
    }

    // ========== ADMIN METHODS ==========

    /**
     * Get all users (Super-Admin only)
     */
    public java.util.List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get total users count
     */
    public long getAllUsersCount() {
        return userRepository.count();
    }

    /**
     * Get users by approval status
     */
    public java.util.List<User> getUsersByApprovalStatus(ApprovalStatus approvalStatus) {
        return userRepository.findByApprovalStatus(approvalStatus);
    }

    /**
     * Get pending users
     */
    public java.util.List<User> getPendingUsers() {
        return userRepository.findByApprovalStatus(ApprovalStatus.PENDING);
    }

    /**
     * Approve user (Super-Admin only)
     */
    public boolean approveUser(Long userId, Long approvedBy) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setApprovalStatus(ApprovalStatus.APPROVED);
            user.setApprovedBy(approvedBy);
            user.setApprovedAt(java.time.LocalDateTime.now());
            user.setRejectionReason(null);
            user.setStatus("ACTIVE"); // Activate user when approved
            user.setEmailVerified(true); // Email is considered verified when manually approved by admin
            
            userRepository.save(user);
            
            // Send notification to user
            emailService.sendEmail(user.getEmail(), 
                "Account Approved", 
                "Your account has been approved by MOH and you can now access the system.");
            
            return true;
        }
        return false;
    }

    /**
     * Reject user (Super-Admin only)
     */
    public boolean rejectUser(Long userId, Long rejectedBy, String reason) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setApprovalStatus(ApprovalStatus.REJECTED);
            user.setApprovedBy(rejectedBy);
            user.setRejectionReason(reason);
            user.setStatus("INACTIVE"); // Deactivate rejected user
            
            userRepository.save(user);
            
            // Send notification to user
            emailService.sendEmail(user.getEmail(), 
                "Account Rejected", 
                "Your account has been rejected. Reason: " + reason);
            
            return true;
        }
        return false;
    }

    /**
     * Update user role (Super-Admin only)
     */
    public boolean updateUserRole(Long userId, User.Role newRole) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            User.Role oldRole = user.getRole();
            user.setRole(newRole);
            userRepository.save(user);
            
            // Send notification to user
            emailService.sendEmail(user.getEmail(), 
                "Role Updated", 
                "Your role has been updated from " + oldRole + " to " + newRole + " by MOH administrator.");
            
            return true;
        }
        return false;
    }

    /**
     * Check if user can create projects
     */
    public boolean canUserCreateProjects(String email) {
        User user = getUserByEmail(email);
        return user != null && user.canCreateProjects();
    }
    
    /**
     * Get users by role
     */
    public java.util.List<User> getUsersByRole(User.Role role) {
        return userRepository.findByRole(role);
    }
    
    /**
     * Save user (for direct database operations)
     */
    public User saveUser(User user) {
        return userRepository.save(user);
    }
    
    /**
     * Get all partners linked to a specific donor
     */
    public java.util.List<User> getPartnersByDonor(Long donorId) {
        return userRepository.findByParentDonorId(donorId);
    }
    
    /**
     * Get all partners linked to a specific donor email
     */
    public java.util.List<User> getPartnersByDonorEmail(String donorEmail) {
        Optional<User> donor = userRepository.findByEmail(donorEmail);
        if (donor.isPresent() && donor.get().getRole() == User.Role.DONOR) {
            return getPartnersByDonor(donor.get().getId());
        }
        return java.util.Collections.emptyList();
    }
    
    /**
     * Link a partner to a donor
     */
    public boolean linkPartnerToDonor(Long partnerId, Long donorId) {
        Optional<User> partnerOptional = userRepository.findById(partnerId);
        Optional<User> donorOptional = userRepository.findById(donorId);
        
        if (partnerOptional.isPresent() && donorOptional.isPresent()) {
            User partner = partnerOptional.get();
            User donor = donorOptional.get();
            
            if (partner.getRole() == User.Role.PARTNER && donor.getRole() == User.Role.DONOR) {
                partner.setParentDonor(donor);
                userRepository.save(partner);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Unlink a partner from their donor
     */
    public boolean unlinkPartnerFromDonor(Long partnerId) {
        Optional<User> partnerOptional = userRepository.findById(partnerId);
        
        if (partnerOptional.isPresent()) {
            User partner = partnerOptional.get();
            if (partner.getRole() == User.Role.PARTNER) {
                partner.setParentDonor(null);
                userRepository.save(partner);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get all donors
     */
    public java.util.List<User> getAllDonors() {
        return getUsersByRole(User.Role.DONOR);
    }
    
    /**
     * Get available partners (approved partners not linked to any donor)
     */
    public java.util.List<User> getAvailablePartners() {
        return userRepository.findByRoleAndParentDonorIsNull(User.Role.PARTNER)
                .stream()
                .filter(user -> user.getApprovalStatus() == ApprovalStatus.APPROVED)
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Check if user can manage partners (donors can manage their linked partners)
     */
    public boolean canUserManagePartner(String userEmail, Long partnerId) {
        Optional<User> userOptional = userRepository.findByEmail(userEmail);
        if (!userOptional.isPresent()) {
            return false;
        }
        
        User user = userOptional.get();
        
        // Super admins can manage all partners
        if (user.getRole() == User.Role.SUPER_ADMIN) {
            return true;
        }
        
        // Donors can manage their linked partners
        if (user.getRole() == User.Role.DONOR) {
            Optional<User> partnerOptional = userRepository.findById(partnerId);
            if (partnerOptional.isPresent()) {
                User partner = partnerOptional.get();
                return partner.getParentDonor() != null && partner.getParentDonor().getId().equals(user.getId());
            }
        }
        
        return false;
    }

    /**
     * Delete user (Super-Admin only)
     */
    @Transactional
    public boolean deleteUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // Delete dependent records first
            // Delete announcements created by this user
            java.util.List<Announcement> announcements = announcementRepository.findByCreatedBy(user);
            for (Announcement announcement : announcements) {
                // Delete collaboration requests on this announcement
                java.util.List<CollaborationRequest> requestsOnAnnouncement = collaborationRequestRepository.findByAnnouncementIdOrderByCreatedAtDesc(announcement.getId());
                collaborationRequestRepository.deleteAll(requestsOnAnnouncement);
            }
            announcementRepository.deleteAll(announcements);
            
            // Delete collaboration requests by this user
            java.util.List<CollaborationRequest> collaborationRequests = collaborationRequestRepository.findByRequestingUserOrderByCreatedAtDesc(user);
            collaborationRequestRepository.deleteAll(collaborationRequests);
            
            // Delete project collaborators for this user
            java.util.List<ProjectCollaborator> collaborators = projectCollaboratorRepository.findByUser(user);
            projectCollaboratorRepository.deleteAll(collaborators);
            
            // Finally delete the user
            userRepository.delete(user);
            return true;
        }
        return false;
    }
}
