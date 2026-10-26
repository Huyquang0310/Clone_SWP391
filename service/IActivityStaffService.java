package com.swp391.g1.service;

import com.swp391.g1.dto.CurrentUser;
import com.swp391.g1.dto.response.ActivityStaffResponseDTO;
import com.swp391.g1.dto.response.ActivityDepartmentResponseDTO;
import com.swp391.g1.model.Department;
import com.swp391.g1.model.Staff;

import java.util.List;
import java.util.Map;

public interface IActivityStaffService {
    List<ActivityStaffResponseDTO> getStaffsByActivityId(int activityId);

    List<Staff> getAvailableStaffsForActivity(int activityId);

    boolean canManageStaffAssignments(int activityId, CurrentUser user);

    boolean canAssignStaff(int activityId, CurrentUser user);

    boolean assignStaff(int activityId, int staffId, CurrentUser user) throws Exception;

    boolean removeStaff(int activityId, int staffId, CurrentUser user);

    Map<Integer, String> getDepartmentLabelsByStaff(int activityId);

    List<ActivityDepartmentResponseDTO> getDepartmentsByActivityId(int activityId);

    List<Department> getAvailableDepartmentsForActivity(int activityId);

    boolean addDepartment(int activityId, int departmentId, CurrentUser user) throws Exception;

    boolean removeDepartment(int activityId, int departmentId, CurrentUser user);
}
