package com.swp391.g1.model;

import java.time.LocalDateTime;

public class Question {
    private int id;
    private String title;
    private String content;
    private boolean isAnonymous;
    private String status; // PENDING, ANSWERED
    private int studentId;
    private LocalDateTime createdAt;

    // For JOIN display
    private String studentName;
    private String studentCode;

    public Question() {}

    public Question(int id, String title, String content, boolean isAnonymous,
                    String status, int studentId, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.isAnonymous = isAnonymous;
        this.status = status;
        this.studentId = studentId;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public boolean isAnonymous() { return isAnonymous; }
    public void setAnonymous(boolean anonymous) { isAnonymous = anonymous; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentCode() { return studentCode; }
    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }
}
