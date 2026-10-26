package com.swp391.g1.dao;

import com.swp391.g1.model.Staff;

import java.util.List;

public interface IStaffDAO extends IGenericDAO<Staff, Integer> {
    Staff findByAccountId(int accountId);

    Staff findFirst();

    List<Staff> findByDepartmentId(int departmentId);
}
