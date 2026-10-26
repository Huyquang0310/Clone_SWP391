package com.swp391.g1.service.impl;

import com.swp391.g1.dao.IActivityCollaborationInvitationDAO;
import com.swp391.g1.dao.IExtracurricularActivityDAO;
import com.swp391.g1.dao.IActivityPartnerCompanyDAO;
import com.swp391.g1.dao.IActivityPartnerStaffDAO;
import com.swp391.g1.dao.IPartnerStaffDAO;
import com.swp391.g1.dao.impl.ActivityCollaborationInvitationDAOImpl;
import com.swp391.g1.dao.impl.ActivityPartnerCompanyDAOImpl;
import com.swp391.g1.dao.impl.ActivityPartnerStaffDAOImpl;
import com.swp391.g1.dao.impl.ExtracurricularActivityDAOImpl;
import com.swp391.g1.dao.impl.PartnerStaffDAOImpl;
import com.swp391.g1.dto.CurrentUser;
import com.swp391.g1.dto.response.ActivityPartnerCompanyDTO;
import com.swp391.g1.dto.response.ActivityPartnerStaffResponseDTO;
import com.swp391.g1.model.Enum;
import com.swp391.g1.model.Enum.ActivityStatus;
import com.swp391.g1.model.ExtracurricularActivity;
import com.swp391.g1.model.PartnerStaff;
import com.swp391.g1.service.AccessPolicy;
import com.swp391.g1.service.IActivityPartnerStaffService;
import com.swp391.g1.util.DateTimeUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ActivityPartnerStaffServiceImpl implements IActivityPartnerStaffService {

    private final IActivityPartnerStaffDAO activityPartnerStaffDAO;
    private final IActivityCollaborationInvitationDAO invitationDAO;
    private final IExtracurricularActivityDAO activityDAO;
    private final IActivityPartnerCompanyDAO partnerCompanyDAO;
    private final IPartnerStaffDAO partnerStaffDAO;

    public ActivityPartnerStaffServiceImpl() {
        this.activityPartnerStaffDAO = new ActivityPartnerStaffDAOImpl();
        this.invitationDAO = new ActivityCollaborationInvitationDAOImpl();
        this.activityDAO = new ExtracurricularActivityDAOImpl();
        this.partnerCompanyDAO = new ActivityPartnerCompanyDAOImpl();
        this.partnerStaffDAO = new PartnerStaffDAOImpl();
    }

    @Override
    public List<ActivityPartnerStaffResponseDTO> getPartnerStaffsByActivityId(int activityId, CurrentUser user) {
        if (activityId <= 0) {
            return List.of();
        }
        List<ActivityPartnerStaffResponseDTO> all = activityPartnerStaffDAO.findByActivityId(activityId);
        if (user == null || !"PARTNER_STAFF".equalsIgnoreCase(user.getType())) {
            return all;
        }
        // Partner staff only see the assigned staff of their own company.
        PartnerStaff me = partnerStaffDAO.findByAccountId(user.getId());
        if (me == null) {
            return List.of();
        }
        return all.stream().filter(s -> s.getCompanyId() == me.getCompanyId()).collect(Collectors.toList());
    }

    /**
     * Companies of the activity (accepted invitations) that the user may manage staff for:
     * the invited main staff, or a leader/secretary, of that company.
     */
    private Set<Integer> manageableCompanyIds(int activityId, CurrentUser user) {
        if (!isActivityBeforeStart(activityId)) {
            return Set.of();
        }
        return authorizedCompanyIds(activityId, user);
    }

    private Set<Integer> authorizedCompanyIds(int activityId, CurrentUser user) {
        if (activityId <= 0 || user == null || !"PARTNER_STAFF".equalsIgnoreCase(user.getType())) {
            return Set.of();
        }
        PartnerStaff me = partnerStaffDAO.findByAccountId(user.getId());
        if (me == null) {
            return Set.of();
        }
        boolean leader = AccessPolicy.canAssignPartnerStaffToActivity(user);
        return partnerCompanyDAO.findByActivityId(activityId).stream()
                .filter(ActivityPartnerCompanyDTO::isActive)
                .filter(i -> i.getPartnerCompanyId() == me.getCompanyId())
                .filter(i -> leader || i.getPartnerStaffId() == me.getId())
                .map(i -> i.getPartnerCompanyId())
                .collect(Collectors.toSet());
    }

    private Set<Integer> mainStaffIds(int activityId) {
        return partnerCompanyDAO.findByActivityId(activityId).stream()
                .filter(ActivityPartnerCompanyDTO::isActive)
                .map(i -> i.getPartnerStaffId())
                .collect(Collectors.toSet());
    }

    @Override
    public List<PartnerStaff> getAvailablePartnerStaffsForActivity(int activityId, CurrentUser user) {
        Set<Integer> companyIds = manageableCompanyIds(activityId, user);
        if (companyIds.isEmpty()) {
            return List.of();
        }
        Set<Integer> excluded = new HashSet<>(activityPartnerStaffDAO.findByActivityId(activityId).stream()
                .map(ActivityPartnerStaffResponseDTO::getPartnerStaffId)
                .collect(Collectors.toSet()));
        excluded.addAll(mainStaffIds(activityId));

        return companyIds.stream()
                .flatMap(companyId -> partnerStaffDAO.findByCompany(companyId).stream())
                .filter(s -> s.getStatus() == Enum.CommonStatus.ACTIVE)
                .filter(s -> !excluded.contains(s.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean canManagePartnerStaff(int activityId, CurrentUser user) {
        return !authorizedCompanyIds(activityId, user).isEmpty();
    }

    @Override
    public boolean canAssignPartnerStaff(int activityId, CurrentUser user) {
        return !manageableCompanyIds(activityId, user).isEmpty();
    }

    @Override
    public boolean assignPartnerStaff(int activityId, int partnerStaffId, CurrentUser user) {
        if (activityId <= 0 || partnerStaffId <= 0) {
            throw new IllegalArgumentException("Thông tin phân công không hợp lệ.");
        }
        requireActivityBeforeStart(activityId);
        Set<Integer> companyIds = manageableCompanyIds(activityId, user);
        if (companyIds.isEmpty()) {
            throw new SecurityException("Bạn không có quyền phân công nhân sự đối tác vào hoạt động này.");
        }
        PartnerStaff staff = partnerStaffDAO.findById(partnerStaffId);
        if (staff == null) {
            throw new IllegalArgumentException("Không tìm thấy nhân sự đối tác được chọn.");
        }
        if (staff.getStatus() != Enum.CommonStatus.ACTIVE) {
            throw new IllegalArgumentException("Nhân sự đối tác được chọn không còn hoạt động.");
        }
        if (mainStaffIds(activityId).contains(partnerStaffId)) {
            throw new IllegalArgumentException(
                    "Nhân sự đối tác phụ trách chính không được thêm vào danh sách nhân sự đối tác của hoạt động.");
        }
        if (!companyIds.contains(staff.getCompanyId())) {
            throw new IllegalArgumentException("Nhân sự đối tác phải thuộc công ty đối tác của bạn trong hoạt động.");
        }
        if (activityPartnerStaffDAO.exists(activityId, partnerStaffId)) {
            throw new IllegalArgumentException("Nhân sự đối tác này đã được phân công vào hoạt động.");
        }
        return activityPartnerStaffDAO.insert(activityId, partnerStaffId);
    }

    @Override
    public boolean removePartnerStaff(int activityId, int partnerStaffId, CurrentUser user) {
        if (activityId <= 0 || partnerStaffId <= 0) {
            return false;
        }
        requireActivityBeforeStart(activityId);
        PartnerStaff staff = partnerStaffDAO.findById(partnerStaffId);
        if (staff == null || !manageableCompanyIds(activityId, user).contains(staff.getCompanyId())) {
            return false;
        }
        return activityPartnerStaffDAO.delete(activityId, partnerStaffId);
    }

    private boolean isActivityBeforeStart(int activityId) {
        ExtracurricularActivity activity = activityDAO.findById(activityId);
        return activity != null
                && activity.getActivityStatus() == ActivityStatus.UPCOMING
                && activity.getStartTime() != null
                && DateTimeUtil.now().isBefore(activity.getStartTime());
    }

    private void requireActivityBeforeStart(int activityId) {
        if (!isActivityBeforeStart(activityId)) {
            throw new IllegalArgumentException(
                    "Chỉ được thay đổi phân công nhân sự đối tác trước khi hoạt động bắt đầu.");
        }
    }
}
