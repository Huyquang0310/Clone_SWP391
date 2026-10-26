package com.swp391.g1.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RegistrationForm {
    private int id;
    private int activityId;
    private String activityName;
    private String title;
    private String description;
    private LocalDateTime openAt;
    private LocalDateTime closeAt;
    private String status;
    private int registrationCount;
    private int reservedSeatCount;
    private Integer maxParticipants;
    private List<RegistrationFormField> fields = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getOpenAt() {
        return openAt;
    }

    public void setOpenAt(LocalDateTime openAt) {
        this.openAt = openAt;
    }

    public LocalDateTime getCloseAt() {
        return closeAt;
    }

    public void setCloseAt(LocalDateTime closeAt) {
        this.closeAt = closeAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRegistrationCount() {
        return registrationCount;
    }

    public void setRegistrationCount(int registrationCount) {
        this.registrationCount = registrationCount;
    }

    public int getReservedSeatCount() {
        return reservedSeatCount;
    }

    public void setReservedSeatCount(int reservedSeatCount) {
        this.reservedSeatCount = reservedSeatCount;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public List<RegistrationFormField> getFields() {
        return fields;
    }

    public void setFields(List<RegistrationFormField> fields) {
        this.fields = fields == null ? new ArrayList<>() : fields;
    }

}
