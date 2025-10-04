
package com.tujulishanehub.backend.config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tujulishanehub.backend.models.ApprovalStatus;
import com.tujulishanehub.backend.models.User;
import com.tujulishanehub.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class DatabaseSeeder {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner seedUsers() {
        return args -> {
            try {
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
                    logger.info("Super admin user created: {}", superAdminEmail);
                } else {
                    logger.info("Super admin user already exists: {}", superAdminEmail);
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
                    logger.info("Partner user created: {}", partnerEmail);
                } else {
                    logger.info("Partner user already exists: {}", partnerEmail);
                }
                
                // Second Partner (for testing collaboration requests)
                String partner2Email = "partner2@example.com";
                if (userRepository.findByEmail(partner2Email).isEmpty()) {
                    User partner2 = new User();
                    partner2.setEmail(partner2Email);
                    partner2.setName("Partner Two");
                    partner2.setPassword(passwordEncoder.encode("partner2password"));
                    partner2.setRole(User.Role.PARTNER);
                    partner2.setStatus("ACTIVE");
                    partner2.setEmailVerified(true);
                    partner2.setVerified(true);
                    partner2.setApprovalStatus(ApprovalStatus.APPROVED);
                    partner2.setCreatedAt(LocalDateTime.now());
                    partner2.setUpdatedAt(LocalDateTime.now());
                    partner2.setApprovedAt(LocalDateTime.now());
                    userRepository.save(partner2);
                    logger.info("Second partner user created: {}", partner2Email);
                } else {
                    logger.info("Second partner user already exists: {}", partner2Email);
                }
            } catch (Exception e) {
                logger.error("Error seeding users: {}", e.getMessage(), e);
            }
        };
    }
}
