package com.swp391.g1.model;
import java.time.LocalDateTime;

public class ExtracurricularActivity {
    private int id; //[cite: 1]
    private int semesterId; //[cite: 1]
    private String code; //[cite: 1]
    private String name; //[cite: 1]
    private int responsibleDepartmentId; //[cite: 1]
    private int responsibleStaffId; //[cite: 1]
    private Integer partnerCompanyId; // Nullable[cite: 1]
    private Integer partnerStaffId; // Nullable[cite: 1]
    private Enum.ActivityNature nature;
    private boolean summaryRequired;
    private Integer maxParticipants;
    private String address; //[cite: 1]
    private String description; //[cite: 1]
    private Enum.ActivityStatus activityStatus; //[cite: 1]
    private Enum.ApprovalStatus approvalStatus; //[cite: 1]
    private LocalDateTime createdAt; //[cite: 1]
    private int activityTypeId; //[cite: 1]
    private LocalDateTime startTime; //[cite: 1]
    private LocalDateTime endTime;
    private boolean registrationFormRequired;
    private boolean isPublic = true;

    public boolean isPublic() { return isPublic; }

    public void setPublic(boolean isPublic) { this.isPublic = isPublic; }

    public boolean isRegistrationFormRequired() {
        return registrationFormRequired;
    }

    public void setRegistrationFormRequired(boolean registrationFormRequired) {
        this.registrationFormRequired = registrationFormRequired;
    }

    // Constructor with no parameter
    public ExtracurricularActivity() {
    }

    //Getter and Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResponsibleDepartmentId() {
        return responsibleDepartmentId;
    }

    public void setResponsibleDepartmentId(int responsibleDepartmentId) {
        this.responsibleDepartmentId = responsibleDepartmentId;
    }

    public int getResponsibleStaffId() {
        return responsibleStaffId;
    }

    public void setResponsibleStaffId(int responsibleStaffId) {
        this.responsibleStaffId = responsibleStaffId;
    }

    public Integer getPartnerCompanyId() {
        return partnerCompanyId;
    }

    public void setPartnerCompanyId(Integer partnerCompanyId) {
        this.partnerCompanyId = partnerCompanyId;
    }

    public Integer getPartnerStaffId() {
        return partnerStaffId;
    }

    public void setPartnerStaffId(Integer partnerStaffId) {
        this.partnerStaffId = partnerStaffId;
    }

    public Enum.ActivityNature getNature() {
        return nature;
    }

    public void setNature(Enum.ActivityNature nature) {
        this.nature = nature;
    }

    public boolean isSummaryRequired() {
        return summaryRequired;
    }

    public void setSummaryRequired(boolean summaryRequired) {
        this.summaryRequired = summaryRequired;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Enum.ActivityStatus getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(Enum.ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }

    public Enum.ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Enum.ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getActivityTypeId() {
        return activityTypeId;
    }

    public void setActivityTypeId(int activityTypeId) {
        this.activityTypeId = activityTypeId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
