package com.tujulishanehub.backend.controllers;

import com.tujulishanehub.backend.models.ApprovalStatus;
import com.tujulishanehub.backend.models.User;
import com.tujulishanehub.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ScaffoldController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/scaffold")
    public ResponseEntity<Map<String, String>> scaffoldUsers() {
        Map<String, String> result = new HashMap<>();
        // Super Admin
        String superAdminEmail = "superadmin@example.com";
        if (userRepository.findByEmail(superAdminEmail).isEmpty()) {
            User superAdmin = new User();
            superAdmin.setEmail(superAdminEmail);
            superAdmin.setName("Super Admin");
            superAdmin.setPassword(passwordEncoder.encode("superadminpassword"));
            superAdmin.setRole(User.Role.SUPER_ADMIN);
            superAdmin.setStatus("ACTIVE");
            superAdmin.setEmailVerified(true);
            superAdmin.setVerified(true);
            superAdmin.setApprovalStatus(ApprovalStatus.APPROVED);
            superAdmin.setCreatedAt(LocalDateTime.now());
            superAdmin.setUpdatedAt(LocalDateTime.now());
            superAdmin.setApprovedAt(LocalDateTime.now());
            userRepository.save(superAdmin);
            result.put("superadmin", "created");
        } else {
            result.put("superadmin", "already exists");
        }
        // Partner
        String partnerEmail = "partner@example.com";
        if (userRepository.findByEmail(partnerEmail).isEmpty()) {
            User partner = new User();
            partner.setEmail(partnerEmail);
            partner.setName("Partner User");
            partner.setPassword(passwordEncoder.encode("partnerpassword"));
            partner.setRole(User.Role.PARTNER);
            partner.setStatus("ACTIVE");
            partner.setEmailVerified(true);
            partner.setVerified(true);
            partner.setApprovalStatus(ApprovalStatus.APPROVED);
            partner.setCreatedAt(LocalDateTime.now());
            partner.setUpdatedAt(LocalDateTime.now());
            partner.setApprovedAt(LocalDateTime.now());
            userRepository.save(partner);
            result.put("partner", "created");
        } else {
            result.put("partner", "already exists");
        }
        return ResponseEntity.ok(result);
    }
}
