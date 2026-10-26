package com.swp391.g1.dao;

import com.swp391.g1.dto.response.ActivityDepartmentResponseDTO;

import java.util.List;

public interface IActivityDepartmentDAO {
    List<ActivityDepartmentResponseDTO> findByActivityId(int activityId);

    boolean insert(int activityId, int departmentId);

    boolean delete(int activityId, int departmentId);

    boolean exists(int activityId, int departmentId);

}
