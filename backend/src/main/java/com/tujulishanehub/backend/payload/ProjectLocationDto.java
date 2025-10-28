package com.tujulishanehub.backend.payload;

public class ProjectLocationDto {
    private Long id;
    private String county;
    private String subCounty;
    private String mapsAddress;
    private Double latitude;
    private Double longitude;

    public ProjectLocationDto() {
    }

    public ProjectLocationDto(Long id, String county, String subCounty, String mapsAddress, Double latitude, Double longitude) {
        this.id = id;
        this.county = county;
        this.subCounty = subCounty;
        this.mapsAddress = mapsAddress;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getSubCounty() {
        return subCounty;
    }

    public void setSubCounty(String subCounty) {
        this.subCounty = subCounty;
    }

    public String getMapsAddress() {
        return mapsAddress;
    }

    public void setMapsAddress(String mapsAddress) {
        this.mapsAddress = mapsAddress;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
