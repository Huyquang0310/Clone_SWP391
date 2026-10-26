package com.swp391.g1.dao;

import com.swp391.g1.dto.response.ActivityCollaborationInvitationDTO;

import java.util.List;

public interface IActivityCollaborationInvitationDAO {
    String PENDING = "PENDING";
    String APPROVED = "APPROVED";
    String REJECTED = "REJECTED";
    String CANCELLED = "CANCELLED";

    String TYPE_COLLABORATION = "COLLABORATION";
    String TYPE_TERMINATION = "TERMINATION";

    List<ActivityCollaborationInvitationDTO> findByActivityId(int activityId);

    /** Invitations of the given company, or of every company when the id is null. */
    List<ActivityCollaborationInvitationDTO> findByPartnerCompanyId(Integer partnerCompanyId);

    /** PENDING -> CANCELLED for every invitation whose activity is no longer UPCOMING. */
    int cancelPendingForInactiveActivities();

    /** Activities a partner company may view: pending/approved collaboration invitations or an ACTIVE collaboration. */
    java.util.Set<Integer> findVisibleActivityIds(int partnerCompanyId);

    ActivityCollaborationInvitationDTO findById(int id);

    /** True when the company has a pending invitation (any type) in the activity. */
    boolean existsPendingInvitation(int activityId, int partnerCompanyId);

    /** True when the company has a pending invitation or an ACTIVE collaboration in the activity. */
    boolean existsOpenInvitation(int activityId, int partnerCompanyId);

    /**
     * Creates a PENDING invitation. A COLLABORATION invitation also creates the INACTIVE partner row
     * (or reuses the existing row of the company); a TERMINATION invitation leaves the partner row untouched.
     */
    boolean insert(int activityId, int partnerCompanyId, int partnerStaffId, String createdBy,
                   String invitationType, String message);

    /**
     * PENDING -> APPROVED. COLLABORATION: collaboration ACTIVE.
     * TERMINATION: collaboration INACTIVE and the partner's assigned staff are removed.
     */
    boolean accept(int id);

    /** PENDING -> REJECTED. COLLABORATION: collaboration INACTIVE. TERMINATION: collaboration unchanged. */
    boolean reject(int id);

    /** PENDING -> CANCELLED. COLLABORATION: collaboration INACTIVE. TERMINATION: unchanged. */
    boolean cancel(int id);

    /** COLLABORATION only: APPROVED -> PENDING, collaboration INACTIVE, assigned staff of the partner removed. */
    boolean revokeApproval(int id);

    /** REJECTED -> PENDING. COLLABORATION: collaboration INACTIVE. TERMINATION: unchanged. */
    boolean revokeRejection(int id);
}
