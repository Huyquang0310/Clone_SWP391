package com.swp391.g1.model;

import java.time.LocalDateTime;

public class Answer {
    private int id;
    private int questionId;
    private int staffId;
    private String content;
    private String approvalStatus; // DRAFT, PUBLISHED
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;

    // For JOIN display
    private String staffName;
    private String staffCode;

    public Answer() {}

    public Answer(int id, int questionId, int staffId, String content,
                  String approvalStatus, LocalDateTime createdAt, LocalDateTime publishedAt) {
        this.id = id;
        this.questionId = questionId;
        this.staffId = staffId;
        this.content = content;
        this.approvalStatus = approvalStatus;
        this.createdAt = createdAt;
        this.publishedAt = publishedAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getQuestionId() { return questionId; }
    public void setQuestionId(int questionId) { this.questionId = questionId; }

    public int getStaffId() { return staffId; }
    public void setStaffId(int staffId) { this.staffId = staffId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }

    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }

    public String getStaffCode() { return staffCode; }
    public void setStaffCode(String staffCode) { this.staffCode = staffCode; }
}
