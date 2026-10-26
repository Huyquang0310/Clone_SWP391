package com.swp391.g1.service.impl;

import com.swp391.g1.dao.IActivityCollaborationInvitationDAO;
import com.swp391.g1.dao.IActivityPartnerCompanyDAO;
import com.swp391.g1.dao.IExtracurricularActivityDAO;
import com.swp391.g1.dao.IPartnerCompanyDAO;
import com.swp391.g1.dao.IPartnerStaffDAO;
import com.swp391.g1.dao.impl.ActivityCollaborationInvitationDAOImpl;
import com.swp391.g1.dao.impl.ActivityPartnerCompanyDAOImpl;
import com.swp391.g1.dao.impl.ExtracurricularActivityDAOImpl;
import com.swp391.g1.dao.impl.PartnerCompanyDAOImpl;
import com.swp391.g1.dao.impl.PartnerStaffDAOImpl;
import com.swp391.g1.dto.CurrentUser;
import com.swp391.g1.dto.response.ActivityCollaborationInvitationDTO;
import com.swp391.g1.dto.response.ActivityPartnerCompanyDTO;
import com.swp391.g1.model.Enum.CommonStatus;
import com.swp391.g1.model.ExtracurricularActivity;
import com.swp391.g1.model.PartnerCompany;
import com.swp391.g1.model.PartnerStaff;
import com.swp391.g1.service.AccessPolicy;
import com.swp391.g1.service.IActivityCollaborationInvitationService;
import com.swp391.g1.util.DateTimeUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ActivityCollaborationInvitationServiceImpl implements IActivityCollaborationInvitationService {

    private static final int MAX_MESSAGE_LENGTH = 500;

    private final IActivityCollaborationInvitationDAO invitationDAO = new ActivityCollaborationInvitationDAOImpl();
    private final IActivityPartnerCompanyDAO partnerCompanyLinkDAO = new ActivityPartnerCompanyDAOImpl();
    private final IExtracurricularActivityDAO activityDAO = new ExtracurricularActivityDAOImpl();
    private final IPartnerCompanyDAO partnerCompanyDAO = new PartnerCompanyDAOImpl();
    private final IPartnerStaffDAO partnerStaffDAO = new PartnerStaffDAOImpl();

    @Override
    public List<ActivityCollaborationInvitationDTO> getInvitations(int activityId, CurrentUser user) {
        if (activityId <= 0) {
            return List.of();
        }
        invitationDAO.cancelPendingForInactiveActivities();
        List<ActivityCollaborationInvitationDTO> invitations = invitationDAO.findByActivityId(activityId);
        PartnerStaff me = isPartnerStaffUser(user) ? partnerStaffDAO.findByAccountId(user.getId()) : null;
        Set<Integer> activeCompanyIds = activeCompanyIds(activityId);
        for (ActivityCollaborationInvitationDTO invitation : invitations) {
            invitation.setCanRespond(canRespond(invitation, user, me));
            invitation.setCanRevoke(canRevoke(invitation, user, me, activeCompanyIds));
        }
        return invitations;
    }

    @Override
    public List<ActivityCollaborationInvitationDTO> getInvitationsForUser(CurrentUser user) {
        if (user == null) {
            return List.of();
        }
        PartnerStaff me = isPartnerStaffUser(user) ? partnerStaffDAO.findByAccountId(user.getId()) : null;
        if (isPartnerStaffUser(user) && me == null) {
            return List.of();
        }
        invitationDAO.cancelPendingForInactiveActivities();
        List<ActivityCollaborationInvitationDTO> invitations =
                invitationDAO.findByPartnerCompanyId(me == null ? null : me.getCompanyId());
        java.util.Map<Integer, Set<Integer>> activeByActivity = new java.util.HashMap<>();
        for (ActivityCollaborationInvitationDTO invitation : invitations) {
            Set<Integer> activeCompanyIds = activeByActivity.computeIfAbsent(
                    invitation.getActivityId(), this::activeCompanyIds);
            invitation.setCanRespond(canRespond(invitation, user, me));
            invitation.setCanRevoke(canRevoke(invitation, user, me, activeCompanyIds));
        }
        return invitations;
    }

    @Override
    public java.util.Set<Integer> getVisibleActivityIds(CurrentUser user) {
        if (!isPartnerStaffUser(user)) {
            return null;
        }
        PartnerStaff me = partnerStaffDAO.findByAccountId(user.getId());
        return me == null ? Set.of() : invitationDAO.findVisibleActivityIds(me.getCompanyId());
    }

    @Override
    public List<ActivityPartnerCompanyDTO> getApprovedPartners(int activityId) {
        return activityId <= 0 ? List.of() : partnerCompanyLinkDAO.findByActivityId(activityId);
    }

    @Override
    public void invite(int activityId, String partnerCompanyId, String partnerStaffId, String message,
                       CurrentUser user) {
        if (user == null || !AccessPolicy.canManageActivities(user)) {
            throw new SecurityException("Bạn không có quyền mời đối tác tham gia hoạt động.");
        }

        requireUpcomingActivity(activityId);

        com.swp391.g1.model.ExtracurricularActivity activity =
                activityId <= 0 ? null : activityDAO.findById(activityId);
        if (activity == null) {
            throw new IllegalArgumentException("Không tìm thấy hoạt động.");
        }
        if (activity.getApprovalStatus() != com.swp391.g1.model.Enum.ApprovalStatus.APPROVED) {
            throw new IllegalArgumentException("Chỉ có thể tạo lời mời cho hoạt động đã được duyệt.");
        }
        int companyId = parseId(partnerCompanyId, "Vui lòng chọn công ty đối tác.");
        int staffId = parseId(partnerStaffId, "Vui lòng chọn nhân sự đối tác phụ trách chính.");
        String note = requireMessage(message);

        PartnerCompany company = partnerCompanyDAO.findById(companyId);
        if (company == null || company.getStatus() != CommonStatus.ACTIVE) {
            throw new IllegalArgumentException("Công ty đối tác không tồn tại hoặc không còn hoạt động.");
        }
        PartnerStaff staff = partnerStaffDAO.findById(staffId);
        if (staff == null || staff.getStatus() != CommonStatus.ACTIVE || staff.getCompanyId() != companyId) {
            throw new IllegalArgumentException(
                    "Nhân sự đối tác phải đang hoạt động và thuộc công ty đối tác đã chọn.");
        }
        if (invitationDAO.existsOpenInvitation(activityId, companyId)) {
            throw new IllegalArgumentException(
                    "Công ty đối tác này đã được mời hoặc đang hợp tác trong hoạt động.");
        }
        boolean inserted = invitationDAO.insert(
                activityId,
                companyId,
                staffId,
                String.valueOf(user.getId()),
                IActivityCollaborationInvitationDAO.TYPE_COLLABORATION,
                note);
        if (!inserted) {
            throw new IllegalArgumentException(
                    "Không thể gửi lời mời. Hoạt động có thể đã bắt đầu "
                            + "hoặc không còn ở trạng thái UPCOMING.");
        }
    }

    @Override
    public void requestTermination(int activityId, int activityPartnerCompanyId, String message, CurrentUser user) {
        if (user == null || !AccessPolicy.canManageActivities(user)) {
            throw new SecurityException("Bạn không có quyền gửi lời mời ngừng hợp tác.");
        }
        requireUpcomingActivity(activityId);
        String note = requireMessage(message);
        ActivityPartnerCompanyDTO link = activityPartnerCompanyId <= 0
                ? null : partnerCompanyLinkDAO.findById(activityPartnerCompanyId);
        if (link == null || link.getActivityId() != activityId) {
            throw new IllegalArgumentException("Không tìm thấy đối tác của hoạt động.");
        }
        if (!link.isActive()) {
            throw new IllegalArgumentException("Đối tác này hiện không trong trạng thái hợp tác.");
        }
        if (invitationDAO.existsPendingInvitation(activityId, link.getPartnerCompanyId())) {
            throw new IllegalArgumentException("Đối tác này đang có lời mời chờ phản hồi.");
        }

        boolean inserted = invitationDAO.insert(
                activityId,
                link.getPartnerCompanyId(),
                link.getPartnerStaffId(),
                String.valueOf(user.getId()),
                IActivityCollaborationInvitationDAO.TYPE_TERMINATION,
                note);
        if (!inserted) {
            throw new IllegalArgumentException(
                    "Không thể gửi yêu cầu ngừng hợp tác. Hoạt động có thể đã bắt đầu "
                            + "hoặc không còn ở trạng thái UPCOMING."
            );
        }
    }

    @Override
    public void respond(int invitationId, boolean accept, CurrentUser user) {
        ActivityCollaborationInvitationDTO invitation = requirePending(invitationId);

        // Kiểm tra hoạt động còn cho phép phản hồi lời mời không.
        requireUpcomingActivity(invitation.getActivityId());

        PartnerStaff me = isPartnerStaffUser(user)
                ? partnerStaffDAO.findByAccountId(user.getId())
                : null;

        if (!canRespond(invitation, user, me)) {
            throw new SecurityException(
                    "Bạn không có quyền phản hồi lời mời này."
            );
        }

        boolean updated = accept
                ? invitationDAO.accept(invitationId)
                : invitationDAO.reject(invitationId);
        if (!updated) {
            throw new IllegalArgumentException(
                    "Không thể phản hồi lời mời vì lời mời đã được xử lý hoặc hoạt động đã bắt đầu.");
        }
    }

    @Override
    public void cancel(int invitationId, CurrentUser user) {
        if (user == null || !AccessPolicy.canManageActivities(user)) {
            throw new SecurityException("Bạn không có quyền hủy lời mời này.");
        }
        requirePending(invitationId);
        if (!invitationDAO.cancel(invitationId)) {
            throw new IllegalArgumentException(
                    "Không thể hủy lời mời vì lời mời đã được xử lý hoặc hoạt động đã bắt đầu.");
        }
    }

    @Override
    public void revoke(int invitationId, CurrentUser user) {
        ActivityCollaborationInvitationDTO invitation = invitationId <= 0 ? null : invitationDAO.findById(invitationId);
        if (invitation == null) {
            throw new IllegalArgumentException("Không tìm thấy lời mời.");
        }
        PartnerStaff me = isPartnerStaffUser(user) ? partnerStaffDAO.findByAccountId(user.getId()) : null;
        Set<Integer> activeCompanyIds = activeCompanyIds(invitation.getActivityId());
        if (!canRevoke(invitation, user, me, activeCompanyIds)) {
            throw new SecurityException("Bạn không thể hủy phản hồi của lời mời này.");
        }
        boolean approved = IActivityCollaborationInvitationDAO.APPROVED.equalsIgnoreCase(invitation.getApprovalStatus());
        if (invitationDAO.existsPendingInvitation(invitation.getActivityId(), invitation.getPartnerCompanyId())) {
            throw new IllegalArgumentException("Công ty đối tác này đang có lời mời khác chờ phản hồi trong hoạt động.");
        }
        if (!approved && !isTermination(invitation)
                && activeCompanyIds.contains(invitation.getPartnerCompanyId())) {
            throw new IllegalArgumentException("Công ty đối tác này đang hợp tác trong hoạt động.");
        }
        boolean updated = approved ? invitationDAO.revokeApproval(invitationId) : invitationDAO.revokeRejection(invitationId);
        if (!updated) {
            throw new IllegalArgumentException(
                    "Không thể hủy phản hồi vì lời mời đã thay đổi hoặc hoạt động đã bắt đầu.");
        }
    }

    private Set<Integer> activeCompanyIds(int activityId) {
        return partnerCompanyLinkDAO.findByActivityId(activityId).stream()
                .filter(ActivityPartnerCompanyDTO::isActive)
                .map(ActivityPartnerCompanyDTO::getPartnerCompanyId)
                .collect(Collectors.toSet());
    }

    private ActivityCollaborationInvitationDTO requirePending(int invitationId) {
        invitationDAO.cancelPendingForInactiveActivities();
        ActivityCollaborationInvitationDTO invitation = invitationId <= 0 ? null : invitationDAO.findById(invitationId);
        if (invitation == null) {
            throw new IllegalArgumentException("Không tìm thấy lời mời.");
        }
        if (!IActivityCollaborationInvitationDAO.PENDING.equalsIgnoreCase(invitation.getApprovalStatus())) {
            throw new IllegalArgumentException("Lời mời đã được xử lý trước đó.");
        }
        return invitation;
    }

    private boolean isTermination(ActivityCollaborationInvitationDTO invitation) {
        return IActivityCollaborationInvitationDAO.TYPE_TERMINATION.equalsIgnoreCase(invitation.getInvitationType());
    }

    private boolean isPartnerStaffUser(CurrentUser user) {
        return user != null && "PARTNER_STAFF".equalsIgnoreCase(user.getType());
    }

    /** The invited main staff, or a leader/secretary of the invited company. */
    private boolean isResponder(ActivityCollaborationInvitationDTO invitation, CurrentUser user, PartnerStaff me) {
        if (me == null) {
            return false;
        }
        if (invitation.getPartnerStaffId() != null && invitation.getPartnerStaffId() == me.getId()) {
            return true;
        }
        return AccessPolicy.canAssignPartnerStaffToActivity(user)
                && me.getCompanyId() == invitation.getPartnerCompanyId();
    }

    private boolean canRespond(ActivityCollaborationInvitationDTO invitation, CurrentUser user, PartnerStaff me) {
        return IActivityCollaborationInvitationDAO.PENDING.equalsIgnoreCase(invitation.getApprovalStatus())
                && isResponder(invitation, user, me);
    }

    /**
     * Responders may undo an accepted collaboration invitation (while the collaboration is ACTIVE)
     * or a rejected invitation (a rejected termination only while the collaboration is still ACTIVE).
     * An accepted termination cannot be undone.
     */
    private boolean canRevoke(ActivityCollaborationInvitationDTO invitation, CurrentUser user, PartnerStaff me,
                              Set<Integer> activeCompanyIds) {
        boolean termination = isTermination(invitation);
        boolean approved = !termination
                && IActivityCollaborationInvitationDAO.APPROVED.equalsIgnoreCase(invitation.getApprovalStatus())
                && "ACTIVE".equalsIgnoreCase(invitation.getCollaborationStatus());
        boolean rejected = IActivityCollaborationInvitationDAO.REJECTED.equalsIgnoreCase(invitation.getApprovalStatus())
                && (!termination || activeCompanyIds.contains(invitation.getPartnerCompanyId()));
        return (approved || rejected) && isResponder(invitation, user, me);
    }

    private String requireMessage(String message) {
        String note = message == null ? "" : message.trim();
        if (note.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập lời nhắn gửi đối tác.");
        }
        if (note.length() > MAX_MESSAGE_LENGTH) {
            throw new IllegalArgumentException("Lời nhắn không được vượt quá " + MAX_MESSAGE_LENGTH + " ký tự.");
        }
        return note;
    }

    private int parseId(String value, String errorMessage) {
        try {
            int id = Integer.parseInt(value == null ? "" : value.trim());
            if (id <= 0) {
                throw new IllegalArgumentException(errorMessage);
            }
            return id;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(errorMessage, e);
        }
    }

    /**
     * Chỉ cho phép thao tác lời mời khi hoạt động còn UPCOMING
     * và chưa đến thời điểm bắt đầu.
     */
    private void requireUpcomingActivity(int activityId) {
        if (activityId <= 0) {
            throw new IllegalArgumentException("Không tìm thấy hoạt động.");
        }

        ExtracurricularActivity activity = activityDAO.findById(activityId);

        if (activity == null) {
            throw new IllegalArgumentException("Không tìm thấy hoạt động.");
        }

        if (activity.getActivityStatus() == null
                || !"UPCOMING".equalsIgnoreCase(
                activity.getActivityStatus().toString())) {
            throw new IllegalArgumentException(
                    "Chỉ được thao tác lời mời khi hoạt động đang ở trạng thái UPCOMING."
            );
        }

        LocalDateTime startTime = activity.getStartTime();
        LocalDateTime now = DateTimeUtil.now();

        if (startTime == null || !now.isBefore(startTime)) {
            throw new IllegalArgumentException(
                    "Không thể thao tác lời mời sau khi hoạt động bắt đầu."
            );
        }
    }
}
