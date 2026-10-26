package com.swp391.g1.dao;
import com.swp391.g1.dto.response.ExtracurricularActivityResponseDTO;
import com.swp391.g1.model.ExtracurricularActivity;
import com.swp391.g1.model.Enum;
import java.util.List;

import java.time.LocalDateTime;

public interface IExtracurricularActivityDAO extends IGenericDAO<ExtracurricularActivity, Integer> {
    List<ExtracurricularActivity> findByCode(String code);
    List<ExtracurricularActivity> findByActivityStatus(Enum.ActivityStatus status);
    List<ExtracurricularActivity> findByApprovalStatus(Enum.ApprovalStatus status);

    public List<ExtracurricularActivityResponseDTO> findAllWithDetails();
    List<ExtracurricularActivityResponseDTO> findAllWithDetailsForStaff(int accountId, String role);
    boolean isInStaffActivityScope(int activityId, int accountId, String role);
    boolean update(ExtracurricularActivity activity, Enum.ApprovalStatus newApprovalStatus);

    boolean updateApprovalStatusIfUpcoming(
            int id,
            Enum.ApprovalStatus status,
            List<Enum.ApprovalStatus> allowedFrom,
            java.time.LocalDateTime now
    );

    boolean updateUpcoming(
            ExtracurricularActivity activity,
            Enum.ApprovalStatus expectedApprovalStatus,
            Enum.ApprovalStatus newApprovalStatus,
            java.time.LocalDateTime now
    );

    int countReservedApplications(int activityId);

    boolean setPublic(int activityId, boolean isPublic);

    boolean deleteIfNotOngoingOrCompleted(int id);

    // The supplied synchronization time is UTC and all status updates run in one transaction.
    void synchronizeActivityStatuses(java.time.LocalDateTime now);
}
