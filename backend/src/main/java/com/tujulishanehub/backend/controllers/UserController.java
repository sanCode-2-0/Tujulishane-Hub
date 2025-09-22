package com.tujulishanehub.backend.controllers;

import com.tujulishanehub.backend.models.ApprovalStatus;
import com.tujulishanehub.backend.models.User;
import com.tujulishanehub.backend.payload.ApiResponse;
import com.tujulishanehub.backend.services.UserService;
import com.tujulishanehub.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.expiration:3600}")
    private Long jwtExpiration;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Object>> register(@RequestBody Map<String, String> payload) {
        try {
            String name = payload.get("name");
            String email = payload.get("email");
            if (name == null || name.isEmpty() || email == null || email.isEmpty()) {
                ApiResponse<Object> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Name and email are required.", null);
                return ResponseEntity.badRequest().body(response);
            }
            userService.registerUser(name, email);
            ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), "Registration received. Check your email for the verification OTP.", null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Log full stacktrace for diagnostics (EmailService will also log)
            logger.error("Error during registration for {}: {}", payload.get("email"), e.getMessage(), e);
            ApiResponse<Object> response = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Object>> verify(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String otp = payload.get("otp");
        if (userService.verifyOtp(email, otp)) {
            ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), "User verified successfully.", null);
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<Object> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Invalid OTP.", null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> login(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        if (email == null || email.isEmpty()) {
            ApiResponse<Object> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Email is required.", null);
            return ResponseEntity.badRequest().body(response);
        }

        var userOpt = userService.findByEmail(email);
        if (userOpt.isEmpty()) {
            ApiResponse<Object> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "User not found.", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        var user = userOpt.get();
        if (!"ACTIVE".equals(user.getStatus())) {
            ApiResponse<Object> response = new ApiResponse<>(HttpStatus.FORBIDDEN.value(), "Account not active. Complete verification first.", null);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        // Send a fresh login OTP
        try {
            userService.sendLoginOtp(email);
            ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), "Login OTP sent to your email.", null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("Failed to send login OTP to {}: {}", email, e.getMessage(), e);
            ApiResponse<Object> response = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to send email", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/verify/login")
    public ResponseEntity<ApiResponse<Object>> verifyLogin(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String otp = payload.get("otp");
        if (email == null || email.isEmpty() || otp == null || otp.isEmpty()) {
            ApiResponse<Object> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Email and OTP are required.", null);
            return ResponseEntity.badRequest().body(response);
        }

        var userOpt = userService.findByEmail(email);
        if (userOpt.isEmpty()) {
            ApiResponse<Object> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "User not found.", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        var user = userOpt.get();
        if (!"ACTIVE".equals(user.getStatus())) {
            ApiResponse<Object> response = new ApiResponse<>(HttpStatus.FORBIDDEN.value(), "Account not active. Complete verification first.", null);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        boolean ok = userService.loginWithOtp(email, otp);
        if (!ok) {
            ApiResponse<Object> response = new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "Invalid OTP.", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String token = jwtUtil.generateToken(email);
        HashMap<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("expiresIn", jwtExpiration);

        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), "Login successful.", data);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send-login-otp")
    public ResponseEntity<ApiResponse<Object>> sendLoginOtp(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        if (email == null || email.isEmpty()) {
            ApiResponse<Object> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Email is required.", null);
            return ResponseEntity.badRequest().body(response);
        }

        var userOpt = userService.findByEmail(email);
        if (userOpt.isEmpty()) {
            ApiResponse<Object> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "User not found.", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        var user = userOpt.get();
        if (!"ACTIVE".equals(user.getStatus())) {
            ApiResponse<Object> response = new ApiResponse<>(HttpStatus.FORBIDDEN.value(), "Account not active. Complete verification first.", null);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        try {
            userService.sendLoginOtp(email);
            ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), "Login OTP sent to your email.", null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("Failed to send login OTP to {}: {}", email, e.getMessage(), e);
            ApiResponse<Object> response = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to send email", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // ==================== ADMIN ENDPOINTS ====================
    
    /**
     * Get all pending users (Admin only)
     */
    @GetMapping("/admin/pending-users")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<User>>> getPendingUsers() {
        try {
            List<User> pendingUsers = userService.getPendingUsers();
            ApiResponse<List<User>> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Pending users retrieved successfully", 
                pendingUsers
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error retrieving pending users: {}", e.getMessage(), e);
            ApiResponse<List<User>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to retrieve pending users", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Approve a user (Admin only)
     */
    @PostMapping("/admin/approve-user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> approveUser(@PathVariable Long userId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String adminEmail = auth.getName();
            User admin = userService.getUserByEmail(adminEmail);
            
            boolean success = userService.approveUser(userId, admin.getId());
            if (success) {
                ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.OK.value(), 
                    "User approved successfully", 
                    null
                );
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(), 
                    "User not found", 
                    null
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            logger.error("Error approving user {}: {}", userId, e.getMessage(), e);
            ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to approve user", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Reject a user (Admin only)
     */
    @PostMapping("/admin/reject-user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> rejectUser(
            @PathVariable Long userId, 
            @RequestBody Map<String, String> payload) {
        try {
            String reason = payload.getOrDefault("reason", "No reason provided");
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String adminEmail = auth.getName();
            User admin = userService.getUserByEmail(adminEmail);
            
            boolean success = userService.rejectUser(userId, admin.getId(), reason);
            if (success) {
                ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.OK.value(), 
                    "User rejected successfully", 
                    null
                );
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(), 
                    "User not found", 
                    null
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            logger.error("Error rejecting user {}: {}", userId, e.getMessage(), e);
            ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to reject user", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get users by approval status (Admin only)
     */
    @GetMapping("/admin/users/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<User>>> getUsersByStatus(@PathVariable String status) {
        try {
            ApprovalStatus approvalStatus = ApprovalStatus.valueOf(status.toUpperCase());
            List<User> users = userService.getUsersByApprovalStatus(approvalStatus);
            ApiResponse<List<User>> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Users retrieved successfully", 
                users
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ApiResponse<List<User>> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(), 
                "Invalid approval status. Valid values: PENDING, APPROVED, REJECTED", 
                null
            );
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Error retrieving users by status {}: {}", status, e.getMessage(), e);
            ApiResponse<List<User>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to retrieve users", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get all users (Admin only)
     */
    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            ApiResponse<List<User>> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "All users retrieved successfully", 
                users
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error retrieving all users: {}", e.getMessage(), e);
            ApiResponse<List<User>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to retrieve users", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get current user profile
     */
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<User>> getCurrentUser() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            User user = userService.getUserByEmail(email);
            
            ApiResponse<User> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "User profile retrieved successfully", 
                user
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error retrieving user profile: {}", e.getMessage(), e);
            ApiResponse<User> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to retrieve user profile", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ========== SUPER-ADMIN ENDPOINTS ==========

    /**
     * Get users by approval status (Super-Admin only)
     */
    @GetMapping("/admin/users/by-status/{status}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<User>>> getUsersByApprovalStatus(@PathVariable String status) {
        try {
            ApprovalStatus approvalStatus = ApprovalStatus.valueOf(status.toUpperCase());
            List<User> users = userService.getUsersByApprovalStatus(approvalStatus);
            
            ApiResponse<List<User>> response = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Users retrieved successfully", 
                users
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ApiResponse<List<User>> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(), 
                "Invalid approval status: " + status, 
                null
            );
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Error retrieving users by approval status: {}", e.getMessage(), e);
            ApiResponse<List<User>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                "Failed to retrieve users", 
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
