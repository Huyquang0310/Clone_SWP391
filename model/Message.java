package com.swp391.g1.model;

import java.time.LocalDateTime;

public class Message {
    private int id;
    private int studentId;
    private int staffId;
    private String senderType; // STUDENT, STAFF
    private String content;
    private LocalDateTime sentAt;
    private boolean isRead;

    // For JOIN display
    private String studentName;
    private String studentCode;
    private String staffName;
    private String staffCode;

    public Message() {}

    public Message(int id, int studentId, int staffId, String senderType,
                   String content, LocalDateTime sentAt, boolean isRead) {
        this.id = id;
        this.studentId = studentId;
        this.staffId = staffId;
        this.senderType = senderType;
        this.content = content;
        this.sentAt = sentAt;
        this.isRead = isRead;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getStaffId() { return staffId; }
    public void setStaffId(int staffId) { this.staffId = staffId; }

    public String getSenderType() { return senderType; }
    public void setSenderType(String senderType) { this.senderType = senderType; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentCode() { return studentCode; }
    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }

    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }

    public String getStaffCode() { return staffCode; }
    public void setStaffCode(String staffCode) { this.staffCode = staffCode; }
}
