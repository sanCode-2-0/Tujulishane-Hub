package com.tujulishanehub.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tujulishanehub.backend.models.ApprovalStatus;
import com.tujulishanehub.backend.models.User;
import com.tujulishanehub.backend.models.Project;
import com.tujulishanehub.backend.models.ProjectLocation;
import com.tujulishanehub.backend.models.ProjectThemeAssignment;
import com.tujulishanehub.backend.models.ProjectCategory;
import com.tujulishanehub.backend.models.ProjectTheme;
import com.tujulishanehub.backend.models.ApprovalWorkflowStatus;
import com.tujulishanehub.backend.models.ReviewerThematicArea;
import com.tujulishanehub.backend.repositories.UserRepository;
import com.tujulishanehub.backend.repositories.ProjectRepository;
import com.tujulishanehub.backend.repositories.ReviewerThematicAreaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class DatabaseSeeder {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ReviewerThematicAreaRepository reviewerThematicAreaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner seedDatabase() {
        return args -> {
            logger.info("Initializing database seeding...");
            
            String defaultPassword = passwordEncoder.encode("Password@123");

            // 1. Braine Strathmore (PARTNER)
            User brainePartner = seedUser("braine.kapolon@strathmore.edu", "Braine Strathmore", User.Role.PARTNER, defaultPassword);

            // 2. Lomogan Reviewer (SUPER_ADMIN_REVIEWER)
            User lomoganReviewer = seedUser("lomogantech@gmail.com", "Lomogan Reviewer", User.Role.SUPER_ADMIN_REVIEWER, defaultPassword);

            // 3. Keegan Kariuki (SUPER_ADMIN_APPROVER)
            User keeganApprover = seedUser("kariukikeegan@gmail.com", "Keegan Kariuki", User.Role.SUPER_ADMIN_APPROVER, defaultPassword);

            // 4. Braine Kapolon (SUPER_ADMIN_APPROVER)
            User braineApprover = seedUser("kapolonbraine@gmail.com", "Braine Kapolon", User.Role.SUPER_ADMIN_APPROVER, defaultPassword);

            // Seed reviewer thematic area assignments
            if (lomoganReviewer != null) {
                seedReviewerTheme(lomoganReviewer, ProjectTheme.MNH);
                seedReviewerTheme(lomoganReviewer, ProjectTheme.AYPSRH);
                seedReviewerTheme(lomoganReviewer, ProjectTheme.FP);
            }

            // Seed projects by the Partner user
            if (brainePartner != null) {
                // Project 1: Approved & Active
                seedProject(
                    "braine.kapolon@strathmore.edu",
                    "Maternal Health Outreach Initiative",
                    "PRJ-001",
                    ProjectCategory.IMPLEMENTING,
                    LocalDate.of(2026, 1, 1),
                    LocalDate.of(2026, 12, 31),
                    "Community outreach, maternal care training",
                    new BigDecimal("5000000.00"),
                    "active",
                    ApprovalStatus.APPROVED,
                    ApprovalWorkflowStatus.APPROVED,
                    ProjectTheme.MNH,
                    new LocationData[] {
                        new LocationData("Nairobi", "Dagoretti North", "Dagoretti Area, Nairobi", -1.2841, 36.7623),
                        new LocationData("Nairobi", "Kibra", "Kibera Clinic, Nairobi", -1.3122, 36.7865)
                    }
                );

                // Project 2: Pending Review
                seedProject(
                    "braine.kapolon@strathmore.edu",
                    "Adolescent SRH Education Program",
                    "PRJ-002",
                    ProjectCategory.RESEARCH,
                    LocalDate.of(2026, 6, 1),
                    LocalDate.of(2027, 5, 31),
                    "School workshops, clinical service links",
                    new BigDecimal("3500000.00"),
                    "pending",
                    ApprovalStatus.PENDING,
                    ApprovalWorkflowStatus.PENDING_REVIEW,
                    ProjectTheme.AYPSRH,
                    new LocationData[] {
                        new LocationData("Mombasa", "Nyali", "Nyali Community Centre, Mombasa", -4.0435, 39.7042)
                    }
                );

                // Project 3: Pending Final Approval
                seedProject(
                    "braine.kapolon@strathmore.edu",
                    "Family Planning Expansion Project",
                    "PRJ-003",
                    ProjectCategory.IMPLEMENTING,
                    LocalDate.of(2026, 3, 15),
                    LocalDate.of(2026, 9, 15),
                    "Contraceptive distribution, provider training",
                    new BigDecimal("7200000.00"),
                    "active",
                    ApprovalStatus.PENDING,
                    ApprovalWorkflowStatus.PENDING_FINAL_APPROVAL,
                    ProjectTheme.FP,
                    new LocationData[] {
                        new LocationData("Kisumu", "Kisumu Central", "Kisumu Referral Hospital", -0.0917, 34.7680)
                    }
                );
            }

            logger.info("Database seeding completed successfully!");
        };
    }

    private User seedUser(String email, String name, User.Role role, String encodedPassword) {
        java.util.Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isEmpty()) {
            User user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setPassword(encodedPassword);
            user.setRole(role);
            user.setStatus("ACTIVE");
            user.setEmailVerified(true);
            user.setVerified(true);
            user.setApprovalStatus(ApprovalStatus.APPROVED);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            user.setApprovedAt(LocalDateTime.now());
            User saved = userRepository.save(user);
            logger.info("Seeded user: {} ({}) with role: {}", name, email, role);
            return saved;
        } else {
            logger.info("User already exists: {} ({})", name, email);
            return existing.get();
        }
    }

    private void seedReviewerTheme(User reviewer, ProjectTheme theme) {
        if (!reviewerRepositoryHasAssignment(reviewer.getId(), theme)) {
            ReviewerThematicArea assignment = new ReviewerThematicArea(reviewer, theme, 1L); // assigned by admin (1)
            reviewerThematicAreaRepository.save(assignment);
            logger.info("Assigned theme {} to reviewer: {}", theme, reviewer.getEmail());
        }
    }

    private boolean reviewerRepositoryHasAssignment(Long userId, ProjectTheme theme) {
        try {
            return reviewerThematicAreaRepository.existsByUserIdAndThematicArea(userId, theme);
        } catch (Exception e) {
            return false;
        }
    }

    private void seedProject(
        String partnerEmail,
        String title,
        String projectNo,
        ProjectCategory category,
        LocalDate startDate,
        LocalDate endDate,
        String activityType,
        BigDecimal budget,
        String status,
        ApprovalStatus approvalStatus,
        ApprovalWorkflowStatus workflowStatus,
        ProjectTheme theme,
        LocationData[] locationsData
    ) {
        if (projectRepository.findByProjectNo(projectNo).isEmpty()) {
            Project project = new Project();
            project.setPartner(partnerEmail);
            project.setTitle(title);
            project.setProjectNo(projectNo);
            project.setProjectCategory(category);
            project.setStartDate(startDate);
            project.setEndDate(endDate);
            project.setActivityType(activityType);
            project.setBudget(budget);
            project.setStatus(status);
            project.setApprovalStatus(approvalStatus);
            project.setApprovalWorkflowStatus(workflowStatus);
            project.setObjectives("Objectives for " + title);
            project.setContactPersonName("Contact Person");
            project.setContactPersonRole("Manager");
            project.setContactPersonEmail("contact@example.com");
            project.setCreatedAt(LocalDateTime.now());
            project.setUpdatedAt(LocalDateTime.now());

            if (workflowStatus == ApprovalWorkflowStatus.APPROVED) {
                project.setApprovedAt(LocalDateTime.now());
                project.setApprovedBy(1L);
            }

            // Set themes
            Set<ProjectThemeAssignment> themes = new HashSet<>();
            ProjectThemeAssignment themeAss = new ProjectThemeAssignment();
            themeAss.setProject(project);
            themeAss.setProjectTheme(theme);
            themeAss.setAssignedAt(LocalDateTime.now());
            themes.add(themeAss);
            project.setThemes(themes);

            // Set locations
            Set<ProjectLocation> locations = new HashSet<>();
            for (LocationData locData : locationsData) {
                ProjectLocation loc = new ProjectLocation();
                loc.setProject(project);
                loc.setCounty(locData.county);
                loc.setSubCounty(locData.subCounty);
                loc.setMapsAddress(locData.mapsAddress);
                loc.setLatitude(locData.latitude);
                loc.setLongitude(locData.longitude);
                loc.setCreatedAt(LocalDateTime.now());
                locations.add(loc);
            }
            project.setLocations(locations);

            projectRepository.save(project);
            logger.info("Seeded project: {} with number: {}", title, projectNo);
        } else {
            logger.info("Project already exists with number: {}", projectNo);
        }
    }

    private static class LocationData {
        String county;
        String subCounty;
        String mapsAddress;
        double latitude;
        double longitude;

        LocationData(String county, String subCounty, String mapsAddress, double latitude, double longitude) {
            this.county = county;
            this.subCounty = subCounty;
            this.mapsAddress = mapsAddress;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}
