package com.swp391.g1.service;

import com.swp391.g1.dto.CurrentUser;
import com.swp391.g1.dto.request.ExtracurricularActivityRequestDTO;
import com.swp391.g1.dto.response.ExtracurricularActivityResponseDTO;

import java.util.List;

public interface IExtracurricularActivityService {
    List<ExtracurricularActivityResponseDTO> getAllActivities();
    List<ExtracurricularActivityResponseDTO> getActivitiesVisibleTo(CurrentUser user);
    ExtracurricularActivityResponseDTO getActivityById(int id);
    boolean canAccessActivity(int id, CurrentUser user);
    boolean createActivity(ExtracurricularActivityRequestDTO requestDTO) throws Exception;
    boolean updateActivity(ExtracurricularActivityRequestDTO requestDTO) throws Exception;
    boolean deleteActivity(int id);
    boolean submitForApproval(int id);
    boolean approveActivity(int id);
    boolean rejectActivity(int id);
    boolean revokeApproval(int id);
    boolean revokeRejection(int id);

    void synchronizeActivityStatuses();
}
