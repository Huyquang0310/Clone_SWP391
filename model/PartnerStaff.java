package com.swp391.g1.model;
import java.time.LocalDate;

public class PartnerStaff {
    private int id; //[cite: 1]
    private int companyId; //[cite: 1]
    private String code; //[cite: 1]
    private String name; //[cite: 1]
    private LocalDate dob; //[cite: 1]
    private Boolean gender; // Nullable[cite: 1]
    private String position; //[cite: 1]
    private Enum.CommonStatus status; //[cite: 1]
    private Integer accountId;

    // Constructor with no parameter
    public PartnerStaff() {
    }

    //Getter and Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
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

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Enum.CommonStatus getStatus() {
        return status;
    }

    public void setStatus(Enum.CommonStatus status) {
        this.status = status;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }
}
