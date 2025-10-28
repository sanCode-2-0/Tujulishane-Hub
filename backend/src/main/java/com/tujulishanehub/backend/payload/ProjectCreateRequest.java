package com.tujulishanehub.backend.payload;

import com.tujulishanehub.backend.models.ProjectCategory;
import com.tujulishanehub.backend.models.ProjectTheme;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
public class ProjectCreateRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Partner is required")
    private String partner;

    @NotEmpty(message = "At least one theme is required")
    private List<String> themes; // List of theme codes (e.g., "GBV", "AYPSRH")

    @NotNull(message = "Project category is required")
    private ProjectCategory projectCategory;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    @NotBlank(message = "Activity type is required")
    private String activityType;

    @NotEmpty(message = "At least one location is required")
    private List<LocationRequest> locations;

    @NotBlank(message = "Contact person name is required")
    private String contactPersonName;

    @NotBlank(message = "Contact person role is required")
    private String contactPersonRole;

    private String contactPersonEmail;

    @NotBlank(message = "Objectives are required")
    private String objectives;

    @NotNull(message = "Budget is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Budget must be greater than 0")
    private BigDecimal budget;

    @Data
    public static class LocationRequest {
        @NotBlank(message = "County is required")
        private String county;

        private String subCounty;

        private String mapsAddress;

        private Double latitude;

        private Double longitude;

        // Getters and setters
        public String getCounty() { return county; }
        public void setCounty(String county) { this.county = county; }

        public String getSubCounty() { return subCounty; }
        public void setSubCounty(String subCounty) { this.subCounty = subCounty; }

        public String getMapsAddress() { return mapsAddress; }
        public void setMapsAddress(String mapsAddress) { this.mapsAddress = mapsAddress; }

        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }

        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }
    }

    // Getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPartner() { return partner; }
    public void setPartner(String partner) { this.partner = partner; }

    public List<String> getThemes() { return themes; }
    public void setThemes(List<String> themes) { this.themes = themes; }

    public ProjectCategory getProjectCategory() { return projectCategory; }
    public void setProjectCategory(ProjectCategory projectCategory) { this.projectCategory = projectCategory; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }

    public List<LocationRequest> getLocations() { return locations; }
    public void setLocations(List<LocationRequest> locations) { this.locations = locations; }

    public String getContactPersonName() { return contactPersonName; }
    public void setContactPersonName(String contactPersonName) { this.contactPersonName = contactPersonName; }

    public String getContactPersonRole() { return contactPersonRole; }
    public void setContactPersonRole(String contactPersonRole) { this.contactPersonRole = contactPersonRole; }

    public String getContactPersonEmail() { return contactPersonEmail; }
    public void setContactPersonEmail(String contactPersonEmail) { this.contactPersonEmail = contactPersonEmail; }

    public String getObjectives() { return objectives; }
    public void setObjectives(String objectives) { this.objectives = objectives; }

    public BigDecimal getBudget() { return budget; }
    public void setBudget(BigDecimal budget) { this.budget = budget; }
}