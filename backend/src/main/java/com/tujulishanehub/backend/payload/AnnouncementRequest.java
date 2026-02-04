package com.tujulishanehub.backend.payload;

import com.tujulishanehub.backend.models.AnnouncementStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AnnouncementRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Content is required")
    private String content;
    
    @NotNull(message = "Project ID is required")
    private Long projectId;
    
    private LocalDate deadline;
    
    private AnnouncementStatus status = AnnouncementStatus.ACTIVE;
}
