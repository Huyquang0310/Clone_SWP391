package com.swp391.g1.model;

public class PartnerCompany {
    private int id; //[cite: 1]
    private String code; //[cite: 1]
    private String shortName; //[cite: 1]
    private String name; //[cite: 1]
    private String website; //[cite: 1]
    private String email; //[cite: 1]
    private String phoneNumber; //[cite: 1]
    private String address; //[cite: 1]
    private String description; //[cite: 1]
    private Enum.CommonStatus status;

    //Constructor with no parameter
    public PartnerCompany() {
    }

    //Getter and Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public Enum.CommonStatus getStatus() {
        return status;
    }

    public void setStatus(Enum.CommonStatus status) {
        this.status = status;
    }
}
