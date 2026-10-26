package com.swp391.g1.dao;

import com.swp391.g1.dto.response.ActivityPartnerCompanyDTO;

import java.util.List;

public interface IActivityPartnerCompanyDAO {
    /** All partner rows of the activity, ACTIVE and INACTIVE. */
    List<ActivityPartnerCompanyDTO> findByActivityId(int activityId);

    ActivityPartnerCompanyDTO findById(int id);
}
