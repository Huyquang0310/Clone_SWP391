package com.swp391.g1.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RegistrationApplication {
    private int id;
    private int formId;
    private String formTitle;
    private String activityName;
    private int studentId;
    private String studentCode;
    private String studentName;
    private String classCode;
    private String email;
    private String note;
    private String status;
    private LocalDateTime submittedAt;
    private LocalDateTime pendingSince;
    private List<RegistrationFormField> answers = new ArrayList<>();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFormTitle() { return formTitle; }
    public void setFormTitle(String formTitle) { this.formTitle = formTitle; }
    public String getActivityName() { return activityName; }
    public void setActivityName(String activityName) { this.activityName = activityName; }

    public int getFormId() { return formId; }
    public void setFormId(int formId) { this.formId = formId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentCode() { return studentCode; }
    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getClassCode() { return classCode; }
    public void setClassCode(String classCode) { this.classCode = classCode; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public LocalDateTime getPendingSince() { return pendingSince; }
    public void setPendingSince(LocalDateTime pendingSince) { this.pendingSince = pendingSince; }

    public List<RegistrationFormField> getAnswers() { return answers; }
    public void setAnswers(List<RegistrationFormField> answers) {
        this.answers = answers == null ? new ArrayList<>() : answers;
    }
}
