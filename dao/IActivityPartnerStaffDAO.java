package com.swp391.g1.dao;

import com.swp391.g1.dto.response.ActivityPartnerStaffResponseDTO;

import java.util.List;

public interface IActivityPartnerStaffDAO {
    List<ActivityPartnerStaffResponseDTO> findByActivityId(int activityId);

    boolean insert(int activityId, int partnerStaffId);

    boolean delete(int activityId, int partnerStaffId);

    boolean exists(int activityId, int partnerStaffId);
}
