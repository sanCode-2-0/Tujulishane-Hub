package com.tujulishanehub.backend.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GeneralAnnouncementRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Body is required")
    private String body;
}