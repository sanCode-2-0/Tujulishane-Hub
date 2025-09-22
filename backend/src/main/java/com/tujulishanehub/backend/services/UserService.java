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
        Optional<User> existingUser = userRepository.findByEmail(email);
        String otp = generateOtp();
        
        // Validate organization if provided
        Organization organization = null;
        if (organizationId != null) {
            Optional<Organization> orgOpt = organizationService.getOrganizationById(organizationId);
            if (orgOpt.isEmpty()) {
                throw new RuntimeException("Organization not found with ID: " + organizationId);
            }
            organization = orgOpt.get();
            if (!organization.isApproved()) {
                throw new RuntimeException("Organization must be approved before users can register with it");
            }
        }
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setName(name);
            user.setOtp(otp);
            user.setVerified(false);
            user.setEmailVerified(false);
            user.setStatus("INACTIVE");
            user.setOrganization(organization);
            userRepository.save(user);
            emailService.sendEmail(email, "Your verification OTP", "Your OTP is: " + otp);
        } else {
            User newUser = new User();
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setOtp(otp);
            newUser.setVerified(false);
            newUser.setEmailVerified(false);
            newUser.setStatus("INACTIVE");
            newUser.setOrganization(organization);
            userRepository.save(newUser);
            emailService.sendEmail(email, "Your verification OTP", "Your OTP is: " + otp);
        }
    }
    
    // Backward compatibility method
    public void registerUser(String name, String email) {
        registerUser(name, email, null);
    }

    // Verify OTP and activate user
    public boolean verifyOtp(String email, String otp) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getOtp() != null && user.getOtp().equals(otp)) {
                user.setVerified(true);
                user.setStatus("ACTIVE");
                user.setOtp(null);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    // Login using email + otp. User must be ACTIVE.
    public boolean loginWithOtp(String email, String otp) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (!"ACTIVE".equals(user.getStatus())) {
                return false;
            }
            if (user.getOtp() != null && user.getOtp().equals(otp)) {
                // clear OTP after successful login
                user.setOtp(null);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    // Helper to (re)send login OTP for ACTIVE users if needed
    public void sendLoginOtp(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String otp = generateOtp();
            user.setOtp(otp);
            userRepository.save(user);
            emailService.sendEmail(email, "Your login OTP", "Your OTP is: " + otp);
        }
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
}
