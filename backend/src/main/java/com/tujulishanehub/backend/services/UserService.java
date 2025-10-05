package com.tujulishanehub.backend.services;

import com.tujulishanehub.backend.models.ApprovalStatus;
import com.tujulishanehub.backend.models.Organization;
import com.tujulishanehub.backend.models.User;
import com.tujulishanehub.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    // Register user (name + email + optional organization). Create INACTIVE account and send OTP
    public void registerUser(String name, String email, Long organizationId) {
        // Check if user already exists
        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isPresent()) {
            return; // User already exists, do nothing
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setStatus("INACTIVE");
        user.setApprovalStatus(com.tujulishanehub.backend.models.ApprovalStatus.PENDING);
        user.setRole(com.tujulishanehub.backend.models.User.Role.PARTNER);
        user.setOtp(generateOtp());
        user.setOtpExpiry(java.time.LocalDateTime.now().plusMinutes(10));
        if (organizationId != null) {
            organizationService.getOrganizationById(organizationId)
                .ifPresent(user::setOrganization);
        }
        userRepository.save(user);
        // For demo: skip sending real email, but you could call emailService.sendEmail(email, ...)
    }
    
    // Backward compatibility method
    public void registerUser(String name, String email) {
        registerUser(name, email, null);
    }

    // Verify OTP and activate user
    public boolean verifyOtp(String email, String otp) {
        // DEMO: Only accept 123456 as valid OTP
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return false;
        User user = userOpt.get();
        if ("123456".equals(otp) && user.getOtpExpiry() != null && user.getOtpExpiry().isAfter(java.time.LocalDateTime.now())) {
            user.setStatus("ACTIVE");
            user.setApprovalStatus(com.tujulishanehub.backend.models.ApprovalStatus.APPROVED);
            user.setOtp(null);
            user.setOtpExpiry(null);
            user.setEmailVerified(true);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // Login using email + otp. User must be ACTIVE.
    public boolean loginWithOtp(String email, String otp) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return false;
        User user = userOpt.get();
        if (!"ACTIVE".equals(user.getStatus())) return false;
        // DEMO: Only accept 123456 as valid OTP
        if ("123456".equals(otp)) {
            user.setLastLogin(java.time.LocalDateTime.now());
            userRepository.save(user);
            return true;
        }
        return false;
    }

    private String generateOtp() {
    // Simulate: Always return '123456' for demo
    return "123456";
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

    // Helper to (re)send login OTP for ACTIVE users if needed
    public void sendLoginOtp(String email) {
        // DEMO: Do nothing
        return;
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
}
