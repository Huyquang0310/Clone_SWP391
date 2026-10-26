package com.swp391.g1.model;

public class Department {
    private int id;
    private String code;
    private String name;
    private String type;
    private String location;
    private Enum.CommonStatus status;

    public Department() {
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Enum.CommonStatus getStatus() {
        return status;
    }

    public void setStatus(Enum.CommonStatus status) {
        this.status = status;
    }
}
