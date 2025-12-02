package com.tujulishanehub.backend.services;

import com.tujulishanehub.backend.models.GeneralAnnouncement;
import com.tujulishanehub.backend.models.User;
import com.tujulishanehub.backend.repositories.GeneralAnnouncementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GeneralAnnouncementService {

    private static final Logger logger = LoggerFactory.getLogger(GeneralAnnouncementService.class);

    @Autowired
    private GeneralAnnouncementRepository generalAnnouncementRepository;

    @Autowired
    private UserService userService;

    /**
     * Create a new general announcement
     */
    public GeneralAnnouncement createAnnouncement(String title, String body, String userEmail) {
        logger.info("Creating general announcement by user: {}", userEmail);

        User user = userService.getUserByEmail(userEmail);

        GeneralAnnouncement announcement = new GeneralAnnouncement();
        announcement.setTitle(title);
        announcement.setBody(body);
        announcement.setCreatedBy(user);

        GeneralAnnouncement saved = generalAnnouncementRepository.save(announcement);
        logger.info("General announcement created successfully with ID: {}", saved.getId());
        return saved;
    }

    /**
     * Get all general announcements
     */
    public List<GeneralAnnouncement> getAllAnnouncements() {
        logger.info("Retrieving all general announcements");
        return generalAnnouncementRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Get announcement by ID
     */
    public Optional<GeneralAnnouncement> getAnnouncementById(Long id) {
        return generalAnnouncementRepository.findById(id);
    }
}