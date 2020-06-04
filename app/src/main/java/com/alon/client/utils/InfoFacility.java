package com.alon.client.utils;

public class InfoFacility {
    private String description;
    private FacilityType type;
    private FacilityStatus status;
    private MuscaleGroup mus_group;

    public InfoFacility() {

    }

    public InfoFacility(String description, FacilityType type, FacilityStatus status, MuscaleGroup mus_group) {
        this.description = description;
        this.type = type;
        this.status = status;
        this.mus_group = mus_group;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FacilityType getType() {
        return type;
    }

    public void setType(FacilityType type) {
        this.type = type;
    }

    public FacilityStatus getStatus() {
        return status;
    }

    public void setStatus(FacilityStatus status) {
        this.status = status;
    }

    public MuscaleGroup getMus_group() {
        return mus_group;
    }

    public void setMus_group(MuscaleGroup mus_group) {
        this.mus_group = mus_group;
    }
}
