package com.swp391.g1.dao;

import com.swp391.g1.dto.response.ActivityStaffResponseDTO;

import java.util.List;
import java.util.Map;

public interface IActivityStaffDAO {
    List<ActivityStaffResponseDTO> findStaffsByActivityId(int activityId);

    /** staffId -> "code - name" of the staff's departments (via staff_assignment) within the activity's departments. */
    Map<Integer, String> findDepartmentLabelsByStaff(int activityId);

    boolean insert(int activityId, int staffId);

    boolean delete(int activityId, int staffId);

    boolean exists(int activityId, int staffId);

    boolean isStaffInDepartment(int staffId, int departmentId);

    boolean isStaffInActivityDepartments(int staffId, int activityId);
}
