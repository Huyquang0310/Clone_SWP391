package com.swp391.g1.service;

import com.swp391.g1.dto.CurrentUser;
import com.swp391.g1.dto.response.ActivityPartnerStaffResponseDTO;
import com.swp391.g1.model.PartnerStaff;

import java.util.List;

public interface IActivityPartnerStaffService {
    List<ActivityPartnerStaffResponseDTO> getPartnerStaffsByActivityId(int activityId, CurrentUser user);

    List<PartnerStaff> getAvailablePartnerStaffsForActivity(int activityId, CurrentUser user);

    boolean canManagePartnerStaff(int activityId, CurrentUser user);

    boolean canAssignPartnerStaff(int activityId, CurrentUser user);

    boolean assignPartnerStaff(int activityId, int partnerStaffId, CurrentUser user);

    boolean removePartnerStaff(int activityId, int partnerStaffId, CurrentUser user);
}
