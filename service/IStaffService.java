package com.swp391.g1.service;

import com.swp391.g1.model.Staff;

import java.util.List;

public interface IStaffService {
    List<Staff> getAllStaffs();

    List<Staff> getActiveStaffs();

    Staff getStaffById(int id);

    Staff getStaffByAccountId(int accountId);

    Staff getFirstStaff();

    List<Staff> getStaffsByDepartmentId(int departmentId);

    boolean createStaff(Staff staff) throws Exception;

    boolean updateStaff(Staff staff) throws Exception;

    boolean deleteStaff(int id);
}
