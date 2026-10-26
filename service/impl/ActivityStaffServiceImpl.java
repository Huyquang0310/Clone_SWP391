package com.swp391.g1.service.impl;

import com.swp391.g1.dao.IActivityDepartmentDAO;
import com.swp391.g1.dao.IActivityStaffDAO;
import com.swp391.g1.dao.IDepartmentDAO;
import com.swp391.g1.dao.IExtracurricularActivityDAO;
import com.swp391.g1.dao.IStaffDAO;
import com.swp391.g1.dao.impl.ActivityDepartmentDAOImpl;
import com.swp391.g1.dao.impl.ActivityStaffDAOImpl;
import com.swp391.g1.dao.impl.DepartmentDAOImpl;
import com.swp391.g1.dao.impl.ExtracurricularActivityDAOImpl;
import com.swp391.g1.dao.impl.StaffDAOImpl;
import com.swp391.g1.dto.CurrentUser;
import com.swp391.g1.dto.response.ActivityDepartmentResponseDTO;
import com.swp391.g1.dto.response.ActivityStaffResponseDTO;
import com.swp391.g1.model.Department;
import com.swp391.g1.model.Enum.CommonStatus;
import com.swp391.g1.model.ExtracurricularActivity;
import com.swp391.g1.model.Enum.ActivityStatus;
import com.swp391.g1.model.Staff;
import com.swp391.g1.service.AccessPolicy;
import com.swp391.g1.service.IActivityStaffService;
import com.swp391.g1.util.DateTimeUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ActivityStaffServiceImpl implements IActivityStaffService {

    private final IActivityStaffDAO activityStaffDAO;
    private final IExtracurricularActivityDAO activityDAO;
    private final IStaffDAO staffDAO;
    private final IActivityDepartmentDAO activityDepartmentDAO;
    private final IDepartmentDAO departmentDAO;

    public ActivityStaffServiceImpl() {
        this.activityStaffDAO = new ActivityStaffDAOImpl();
        this.activityDAO = new ExtracurricularActivityDAOImpl();
        this.staffDAO = new StaffDAOImpl();
        this.activityDepartmentDAO = new ActivityDepartmentDAOImpl();
        this.departmentDAO = new DepartmentDAOImpl();
    }

    @Override
    public List<ActivityStaffResponseDTO> getStaffsByActivityId(int activityId) {
        if (activityId <= 0) {
            return List.of();
        }
        return activityStaffDAO.findStaffsByActivityId(activityId);
    }

    @Override
    public List<Staff> getAvailableStaffsForActivity(int activityId) {
        if (activityId <= 0) {
            return List.of();
        }
        ExtracurricularActivity activity = activityDAO.findById(activityId);
        if (activity == null) {
            return List.of();
        }
        Map<Integer, Staff> deptStaffs = new LinkedHashMap<>();
        for (int deptId : getActiveActivityDepartmentIds(activity)) {
            for (Staff s : staffDAO.findByDepartmentId(deptId)) {
                deptStaffs.putIfAbsent(s.getId(), s);
            }
        }
        List<ActivityStaffResponseDTO> assigned = activityStaffDAO.findStaffsByActivityId(activityId);
        Set<Integer> assignedStaffIds = assigned.stream()
                .map(ActivityStaffResponseDTO::getStaffId)
                .collect(Collectors.toSet());

        return deptStaffs.values().stream()
                .filter(s -> s.getId() != activity.getResponsibleStaffId())
                .filter(s -> !assignedStaffIds.contains(s.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public Map<Integer, String> getDepartmentLabelsByStaff(int activityId) {
        return activityId <= 0 ? Map.of() : activityStaffDAO.findDepartmentLabelsByStaff(activityId);
    }

    @Override
    public boolean canManageStaffAssignments(int activityId, CurrentUser user) {
        if (activityId <= 0 || user == null) {
            return false;
        }
        ExtracurricularActivity activity = activityDAO.findById(activityId);
        if (activity == null) {
            return false;
        }
        if (AccessPolicy.canAssignStaffToActivity(user)
                || user.getId() == activity.getResponsibleStaffId()) {
            return true;
        }
        Staff currentStaff = staffDAO.findByAccountId(user.getId());
        return currentStaff != null
                && currentStaff.getId() == activity.getResponsibleStaffId();
    }

    @Override
    public boolean canAssignStaff(int activityId, CurrentUser user) {
        return canManageStaffAssignments(activityId, user)
                && isActivityBeforeStart(activityDAO.findById(activityId));
    }

    @Override
    public boolean assignStaff(int activityId, int staffId, CurrentUser user) throws Exception {
        if (activityId <= 0 || staffId <= 0) {
            throw new IllegalArgumentException("Thông tin phân công không hợp lệ.");
        }
        requireActivityBeforeStart(activityId);
        if (!canAssignStaff(activityId, user)) {
            throw new SecurityException("Bạn không có quyền phân công nhân sự vào hoạt động này.");
        }
        ExtracurricularActivity activity = activityDAO.findById(activityId);
        if (activity == null) {
            throw new IllegalArgumentException("Không tìm thấy hoạt động.");
        }
        Staff staff = staffDAO.findById(staffId);
        if (staff == null) {
            throw new IllegalArgumentException("Không tìm thấy nhân sự được chọn.");
        }
        if (staffId == activity.getResponsibleStaffId()) {
            throw new IllegalArgumentException("Nhân sự phụ trách chính không được thêm vào danh sách nhân sự của hoạt động.");
        }
        if (!"ACTIVE".equals(staff.getStatus())) {
            throw new IllegalArgumentException("Chỉ có thể phân công nhân sự đang hoạt động (ACTIVE).");
        }
        if (!activityStaffDAO.isStaffInActivityDepartments(staffId, activityId)) {
            throw new IllegalArgumentException("Nhân sự được phân công phải thuộc đơn vị phụ trách hoặc đơn vị đồng phụ trách (đang hoạt động) của hoạt động.");
        }
        if (activityStaffDAO.exists(activityId, staffId)) {
            throw new IllegalArgumentException("Nhân sự này đã được phân công vào hoạt động.");
        }
        return activityStaffDAO.insert(activityId, staffId);
    }

    @Override
    public boolean removeStaff(int activityId, int staffId, CurrentUser user) {
        if (activityId <= 0 || staffId <= 0) {
            return false;
        }
        requireActivityBeforeStart(activityId);
        if (!canAssignStaff(activityId, user)) {
            return false;
        }
        return activityStaffDAO.delete(activityId, staffId);
    }

    private boolean isActivityBeforeStart(ExtracurricularActivity activity) {
        return activity != null
                && activity.getActivityStatus() == ActivityStatus.UPCOMING
                && activity.getStartTime() != null
                && DateTimeUtil.now().isBefore(activity.getStartTime());
    }

    private void requireActivityBeforeStart(int activityId) {
        ExtracurricularActivity activity = activityDAO.findById(activityId);
        if (!isActivityBeforeStart(activity)) {
            throw new IllegalArgumentException(
                    "Chỉ được thay đổi phân công nhân sự trước khi hoạt động bắt đầu.");
        }
    }

    @Override
    public List<ActivityDepartmentResponseDTO> getDepartmentsByActivityId(int activityId) {
        if (activityId <= 0) {
            return List.of();
        }
        return activityDepartmentDAO.findByActivityId(activityId);
    }

    @Override
    public List<Department> getAvailableDepartmentsForActivity(int activityId) {
        ExtracurricularActivity activity = activityId <= 0 ? null : activityDAO.findById(activityId);
        if (activity == null) {
            return List.of();
        }
        Set<Integer> usedIds = activityDepartmentDAO.findByActivityId(activityId).stream()
                .map(ActivityDepartmentResponseDTO::getDepartmentId)
                .collect(Collectors.toSet());
        usedIds.add(activity.getResponsibleDepartmentId());
        return departmentDAO.findAll().stream()
                .filter(d -> d.getStatus() == CommonStatus.ACTIVE)
                .filter(d -> !usedIds.contains(d.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean addDepartment(int activityId, int departmentId, CurrentUser user) throws Exception {
        if (activityId <= 0 || departmentId <= 0) {
            throw new IllegalArgumentException("Thông tin đơn vị đồng phụ trách không hợp lệ.");
        }
        if (!canAssignStaff(activityId, user)) {
            throw new SecurityException("Bạn không có quyền quản lý đơn vị đồng phụ trách của hoạt động này.");
        }
        ExtracurricularActivity activity = activityDAO.findById(activityId);
        if (activity == null) {
            throw new IllegalArgumentException("Không tìm thấy hoạt động.");
        }
        Department department = departmentDAO.findById(departmentId);
        if (department == null) {
            throw new IllegalArgumentException("Không tìm thấy đơn vị được chọn.");
        }
        if (department.getStatus() != CommonStatus.ACTIVE) {
            throw new IllegalArgumentException("Chỉ có thể thêm đơn vị đang hoạt động (ACTIVE).");
        }
        if (departmentId == activity.getResponsibleDepartmentId()) {
            throw new IllegalArgumentException("Đơn vị phụ trách chính không cần thêm vào danh sách đồng phụ trách.");
        }
        if (activityDepartmentDAO.exists(activityId, departmentId)) {
            throw new IllegalArgumentException("Đơn vị này đã là đơn vị đồng phụ trách của hoạt động.");
        }
        return activityDepartmentDAO.insert(activityId, departmentId);
    }

    @Override
    public boolean removeDepartment(int activityId, int departmentId, CurrentUser user) {
        if (activityId <= 0 || departmentId <= 0 || !canAssignStaff(activityId, user)) {
            return false;
        }
        return activityDepartmentDAO.delete(activityId, departmentId);
    }

    private List<Integer> getActiveActivityDepartmentIds(ExtracurricularActivity activity) {
        List<Integer> ids = new ArrayList<>();
        Department primary = departmentDAO.findById(activity.getResponsibleDepartmentId());
        if (primary != null && primary.getStatus() == CommonStatus.ACTIVE) {
            ids.add(primary.getId());
        }
        for (ActivityDepartmentResponseDTO d : activityDepartmentDAO.findByActivityId(activity.getId())) {
            if ("ACTIVE".equals(d.getDepartmentStatus()) && !ids.contains(d.getDepartmentId())) {
                ids.add(d.getDepartmentId());
            }
        }
        return ids;
    }
}
