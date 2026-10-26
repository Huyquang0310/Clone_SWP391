package com.swp391.g1.service;

import com.swp391.g1.dto.CurrentUser;
import com.swp391.g1.dto.response.ActivityCollaborationInvitationDTO;
import com.swp391.g1.dto.response.ActivityPartnerCompanyDTO;

import java.util.List;

public interface IActivityCollaborationInvitationService {
    /** All invitations of the activity; {@code canRespond} is evaluated for the given user. */
    List<ActivityCollaborationInvitationDTO> getInvitations(int activityId, CurrentUser user);

    /** Invitations visible to the user: their company's for partner staff, all for other users. */
    List<ActivityCollaborationInvitationDTO> getInvitationsForUser(CurrentUser user);

    /**
     * Ids of the activities the user may view, or null when the user is not restricted.
     * Partner staff only see activities their company is invited to (pending/approved) or actively collaborates in.
     */
    java.util.Set<Integer> getVisibleActivityIds(CurrentUser user);

    /** Accepted invitations, i.e. the partners that take part in the activity. */
    List<ActivityPartnerCompanyDTO> getApprovedPartners(int activityId);

    void invite(int activityId, String partnerCompanyId, String partnerStaffId, String message, CurrentUser user);

    /** Sends a termination invitation; the collaboration only ends once the partner accepts it. */
    void requestTermination(int activityId, int activityPartnerCompanyId, String message, CurrentUser user);

    void respond(int invitationId, boolean accept, CurrentUser user);

    void cancel(int invitationId, CurrentUser user);

    /** Undoes an accepted collaboration invitation or a rejected invitation: it returns to PENDING. */
    void revoke(int invitationId, CurrentUser user);
}
