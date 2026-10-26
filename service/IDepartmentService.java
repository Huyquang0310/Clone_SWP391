package com.swp391.g1.service;

import com.swp391.g1.model.Department;

import java.util.List;

public interface IDepartmentService {
    List<Department> getAllDepartments();

    List<Department> getActiveDepartments();

    Department getDepartmentById(int id);

    boolean createDepartment(Department department) throws Exception;

    boolean updateDepartment(Department department) throws Exception;

    boolean deleteDepartment(int id);
}
