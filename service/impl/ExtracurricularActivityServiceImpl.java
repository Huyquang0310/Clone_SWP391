package com.swp391.g1.service.impl;

import com.swp391.g1.dao.impl.ExtracurricularActivityDAOImpl;
import com.swp391.g1.dao.IExtracurricularActivityDAO;
import com.swp391.g1.dao.IActivityStaffDAO;
import com.swp391.g1.dao.impl.ActivityStaffDAOImpl;
import com.swp391.g1.dto.CurrentUser;
import com.swp391.g1.dto.request.ExtracurricularActivityRequestDTO;
import com.swp391.g1.dto.response.ExtracurricularActivityResponseDTO;
import com.swp391.g1.model.Enum.ActivityNature;
import com.swp391.g1.model.Enum.ActivityStatus;
import com.swp391.g1.model.Enum.ApprovalStatus;
import com.swp391.g1.model.ExtracurricularActivity;
import com.swp391.g1.dao.ISemesterDAO;
import com.swp391.g1.dao.impl.SemesterDAOImpl;
import com.swp391.g1.model.Semester;
import com.swp391.g1.service.AccessPolicy;
import com.swp391.g1.service.IExtracurricularActivityService;
import com.swp391.g1.util.DateTimeUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ExtracurricularActivityServiceImpl implements IExtracurricularActivityService {

    private final IExtracurricularActivityDAO activityDAO;
    private final IActivityStaffDAO activityStaffDAO;
    private final ISemesterDAO semesterDAO = new SemesterDAOImpl();

    private static final String LOCKED_MESSAGE =
            "Không thể sửa hoặc xóa hoạt động đang diễn ra hoặc đã hoàn thành.";

    private boolean isLocked(ExtracurricularActivity activity) {
        return activity != null && (activity.getActivityStatus() == ActivityStatus.ONGOING
                || activity.getActivityStatus() == ActivityStatus.COMPLETED);
    }

    public ExtracurricularActivityServiceImpl() {
        this.activityDAO = new ExtracurricularActivityDAOImpl();
        this.activityStaffDAO = new ActivityStaffDAOImpl();
    }

    @Override
    public List<ExtracurricularActivityResponseDTO> getAllActivities() {
        return activityDAO.findAllWithDetails();
    }

    @Override
    public List<ExtracurricularActivityResponseDTO> getActivitiesVisibleTo(CurrentUser user) {
        if (user == null) {
            return List.of();
        }
        if (!AccessPolicy.hasType(user, "STAFF")) {
            return getAllActivities();
        }
        if (!AccessPolicy.canUseStaffActivityScope(user) || user.getId() <= 0) {
            return List.of();
        }
        return activityDAO.findAllWithDetailsForStaff(user.getId(), user.getRole());
    }

    @Override
    public ExtracurricularActivityResponseDTO getActivityById(int id) {
        if (id <= 0) {
            return null;
        }
        // Find DTO by ID from the list of all activities with details
        return activityDAO.findAllWithDetails().stream()
                .filter(a -> a.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean canAccessActivity(int id, CurrentUser user) {
        if (id <= 0 || user == null) {
            return false;
        }
        if (AccessPolicy.hasType(user, "ADMIN")) {
            return activityDAO.findById(id) != null;
        }
        return AccessPolicy.canUseStaffActivityScope(user)
                && user.getId() > 0
                && activityDAO.isInStaffActivityScope(id, user.getId(), user.getRole());
    }

    @Override
    public boolean createActivity(ExtracurricularActivityRequestDTO dto) throws Exception {
        // Validate input data (no need to check ID for creation)
        validateRequestDTO(dto, false);
        // Map from RequestDTO to Entity
        ExtracurricularActivity entity = mapToEntity(dto);
        // Set default values for ApprovalStatus and ActivityStatus
        entity.setApprovalStatus(ApprovalStatus.DRAFT);
        entity.setActivityStatus(ActivityStatus.UPCOMING);
        // Call DAO to insert the new activity
        Integer generatedId = activityDAO.insert(entity);
        return generatedId != null && generatedId > 0;
    }

    @Override
    public boolean updateActivity(ExtracurricularActivityRequestDTO dto) throws Exception {

        // Bước 1: Validate dữ liệu đầu vào (giữ nguyên phần validate).
        validateRequestDTO(dto, true);

        // Bước 2: Chuyển RequestDTO thành Entity.
        ExtracurricularActivity entity = mapToEntity(dto);
        entity.setId(Integer.parseInt(dto.getId().trim()));

        // Bước 3: Lấy hoạt động hiện tại từ database.
        ExtracurricularActivity existing = activityDAO.findById(entity.getId());
        if (isLocked(existing)) {
            throw new IllegalArgumentException(LOCKED_MESSAGE);
        }
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy hoạt động cần cập nhật.");
        }
        if (existing != null) {
            // Partner information is managed separately in the "Đối tác hoạt động" step.
            entity.setPartnerCompanyId(existing.getPartnerCompanyId());
            entity.setPartnerStaffId(existing.getPartnerStaffId());
        }

        // Bước 4: Chỉ cho phép sửa khi hoạt động vẫn UPCOMING.
        requireUpcomingActivity(
                entity.getId(),
                "Không thể sửa hoạt động khi hoạt động không còn ở trạng thái UPCOMING."
        );

        // Bước 5: Giữ nguyên thông tin đối tác vì thông tin này được quản lý riêng.
        entity.setPartnerCompanyId(existing.getPartnerCompanyId());
        entity.setPartnerStaffId(existing.getPartnerStaffId());

        if (!Objects.equals(existing.getMaxParticipants(), entity.getMaxParticipants())
                && entity.getMaxParticipants() != null
                && entity.getMaxParticipants() < activityDAO.countReservedApplications(entity.getId())) {
            throw new IllegalArgumentException(
                    "Không thể giảm số lượng tối đa thấp hơn số đơn đang chờ xử lý và đã duyệt đang giữ chỗ.");
        }

        // Bước 6: Nếu nội dung không thay đổi thì không cần cập nhật.
        if (!hasContentChanged(existing, entity)) {
            return true;
        }

        // Bước 7: Kiểm tra hoạt động đã được duyệt trước khi sửa hay chưa.
        boolean wasApproved =
                existing.getApprovalStatus() == ApprovalStatus.APPROVED;

        // Bước 8: Cập nhật có điều kiện trong database.
        // Nếu đã được duyệt, chuyển approvalStatus về DRAFT.
        // Nếu chưa được duyệt, giữ nguyên approvalStatus hiện tại.
        boolean updated = activityDAO.updateUpcoming(
                entity,
                existing.getApprovalStatus(),
                wasApproved ? ApprovalStatus.DRAFT : null,
                DateTimeUtil.nowUtc()
        );

        // Bước 9: Nếu cập nhật thất bại, thông báo lỗi.
        if (!updated) {
            if (!Objects.equals(existing.getMaxParticipants(), entity.getMaxParticipants())
                    && entity.getMaxParticipants() != null
                    && entity.getMaxParticipants() < activityDAO.countReservedApplications(entity.getId())) {
                throw new IllegalArgumentException(
                        "Không thể giảm số lượng tối đa thấp hơn số đơn đang chờ xử lý và đã duyệt đang giữ chỗ.");
            }
            throw new IllegalArgumentException(
                    "Không thể sửa hoạt động vì hoạt động đã bắt đầu "
                            + "hoặc trạng thái duyệt đã thay đổi. Vui lòng tải lại trang."
            );
        }

        return true;
    }

    private boolean hasContentChanged(ExtracurricularActivity a, ExtracurricularActivity b) {
        return a.getSemesterId() != b.getSemesterId()
                || a.getActivityTypeId() != b.getActivityTypeId()
                || a.getResponsibleDepartmentId() != b.getResponsibleDepartmentId()
                || a.getResponsibleStaffId() != b.getResponsibleStaffId()
                || a.isRegistrationFormRequired() != b.isRegistrationFormRequired()
                || !sameText(a.getCode(), b.getCode())
                || !sameText(a.getName(), b.getName())
                || !sameText(a.getAddress(), b.getAddress())
                || !sameText(a.getDescription(), b.getDescription())
                || a.getNature() != b.getNature()
                || a.isSummaryRequired() != b.isSummaryRequired()
                || !Objects.equals(a.getMaxParticipants(), b.getMaxParticipants())
                || !sameMinute(a.getStartTime(), b.getStartTime())
                || !sameMinute(a.getEndTime(), b.getEndTime());
    }

    private boolean sameText(String x, String y) {
        return Objects.equals(x == null ? null : x.trim(), y == null ? null : y.trim());
    }

    private boolean sameMinute(LocalDateTime x, LocalDateTime y) {
        return Objects.equals(
                x == null ? null : x.truncatedTo(ChronoUnit.MINUTES),
                y == null ? null : y.truncatedTo(ChronoUnit.MINUTES));
    }

    @Override
    public boolean deleteActivity(int id) {
        if (id <= 0) {
            return false;
        }

        boolean deleted = activityDAO.deleteIfNotOngoingOrCompleted(id);

        if (!deleted) {
            throw new IllegalArgumentException(
                    "Không thể xóa hoạt động đang diễn ra hoặc đã hoàn thành."
            );
        }

        return true;
    }

    private void validateRequestDTO(ExtracurricularActivityRequestDTO dto, boolean isUpdate) throws Exception {
        if (dto == null) {
            throw new IllegalArgumentException("Dữ liệu yêu cầu không hợp lệ: ExtracurricularActivityRequestDTO rỗng.");
        }

        if (isUpdate && (dto.getId() == null || dto.getId().trim().isEmpty())) {
            throw new IllegalArgumentException("ID hoạt động không hợp lệ khi cập nhật.");
        }

        // Validate các trường bắt buộc (Not Null)
        if (isEmpty(dto.getCode())) {
            throw new IllegalArgumentException("Mã hoạt động không được để trống.");
        }
        String code = dto.getCode().trim();
        int excludedActivityId = isUpdate ? Integer.parseInt(dto.getId()) : 0;
        boolean duplicateCode = activityDAO.findByCode(code).stream()
                .anyMatch(activity -> activity.getId() != excludedActivityId);
        if (duplicateCode) {
            throw new IllegalArgumentException("Đã tồn tại hoạt động ngoại khóa với mã này.");
        }
        if (isEmpty(dto.getName())) {
            throw new IllegalArgumentException("Tên hoạt động không được để trống.");
        }
        if (isEmpty(dto.getSemesterId())) {
            throw new IllegalArgumentException("Vui lòng chọn học kỳ.");
        }
        if (isEmpty(dto.getActivityTypeId())) {
            throw new IllegalArgumentException("Vui lòng chọn loại hoạt động.");
        }
        if (isEmpty(dto.getResponsibleDepartmentId())) {
            throw new IllegalArgumentException("Vui lòng chọn phòng ban phụ trách.");
        }
        if (isEmpty(dto.getResponsibleStaffId())) {
            throw new IllegalArgumentException("Vui lòng chọn nhân viên phụ trách.");
        }
        int responsibleDepartmentId = parseRequiredId(
                dto.getResponsibleDepartmentId(), "Đơn vị phụ trách không hợp lệ.");
        int responsibleStaffId = parseRequiredId(
                dto.getResponsibleStaffId(), "Nhân viên phụ trách không hợp lệ.");
        if (!activityStaffDAO.isStaffInDepartment(responsibleStaffId, responsibleDepartmentId)) {
            throw new IllegalArgumentException("Nhân viên phụ trách phải thuộc đơn vị phụ trách đã chọn.");
        }

        if (isEmpty(dto.getNature())) {
            throw new IllegalArgumentException("Vui lòng chọn tính chất hoạt động.");
        }
        try {
            ActivityNature.valueOf(dto.getNature().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tính chất hoạt động không hợp lệ.");
        }

        if (!isEmpty(dto.getMaxParticipants())) {
            try {
                if (Integer.parseInt(dto.getMaxParticipants().trim()) <= 0) {
                    throw new IllegalArgumentException("Số lượng tham gia tối đa phải lớn hơn 0.");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Số lượng tham gia tối đa không hợp lệ.");
            }
        }

        // Validate khoảng thời gian: bắt buộc nhập, StartTime phải trước EndTime
        if (isEmpty(dto.getStartTime())) {
            throw new IllegalArgumentException("Thời gian bắt đầu không được để trống.");
        }
        if (isEmpty(dto.getEndTime())) {
            throw new IllegalArgumentException("Thời gian kết thúc không được để trống.");
        }
        try {
            LocalDateTime start = LocalDateTime.parse(dto.getStartTime().trim());
            LocalDateTime end = LocalDateTime.parse(dto.getEndTime().trim());

            if (!start.isAfter(DateTimeUtil.now())) {
                throw new IllegalArgumentException(
                        "Thời gian bắt đầu hoạt động phải ở tương lai.");
            }
            if (!start.isBefore(end)) {
                throw new IllegalArgumentException("Thời gian bắt đầu phải trước thời gian kết thúc.");
            }
            Semester semester = semesterDAO.findById(parseRequiredId(dto.getSemesterId(), "Học kỳ không hợp lệ."));
            if (semester == null) {
                throw new IllegalArgumentException("Học kỳ không tồn tại.");
            }
            if (semester.getTimeStart() != null && start.isBefore(semester.getTimeStart())) {
                throw new IllegalArgumentException(
                        "Thời gian bắt đầu hoạt động phải sau thời gian bắt đầu của học kỳ.");
            }
            if (semester.getTimeEnd() != null && start.isAfter(semester.getTimeEnd())) {
                throw new IllegalArgumentException(
                        "Thời gian bắt đầu hoạt động phải trước thời gian kết thúc của học kỳ.");
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Định dạng ngày/giờ không hợp lệ.");
        }
    }

    private int parseRequiredId(String value, String errorMessage) {
        try {
            int id = Integer.parseInt(value.trim());
            if (id > 0) {
                return id;
            }
        } catch (NumberFormatException ignored) {
            // Report invalid form IDs as validation errors.
        }
        throw new IllegalArgumentException(errorMessage);
    }

    private ExtracurricularActivity mapToEntity(ExtracurricularActivityRequestDTO dto) {
        ExtracurricularActivity entity = new ExtracurricularActivity();

        entity.setCode(dto.getCode().trim());
        entity.setName(dto.getName().trim());
        entity.setAddress(dto.getAddress() != null ? dto.getAddress().trim() : null);
        entity.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : null);

        // Convert String -> int
        entity.setSemesterId(Integer.parseInt(dto.getSemesterId()));
        entity.setActivityTypeId(Integer.parseInt(dto.getActivityTypeId()));
        entity.setResponsibleDepartmentId(Integer.parseInt(dto.getResponsibleDepartmentId()));
        entity.setResponsibleStaffId(Integer.parseInt(dto.getResponsibleStaffId()));

        entity.setNature(ActivityNature.valueOf(dto.getNature().trim()));
        entity.setSummaryRequired("true".equalsIgnoreCase(dto.getSummaryRequired()));
        entity.setMaxParticipants(isEmpty(dto.getMaxParticipants()) ? null : Integer.valueOf(dto.getMaxParticipants().trim()));

        // Convert Thời gian (<input type="datetime-local"> trả về định dạng yyyy-MM-ddTHH:mm)[cite: 1]
        if (!isEmpty(dto.getStartTime())) {
            entity.setStartTime(LocalDateTime.parse(dto.getStartTime().trim()));
        }
        if (!isEmpty(dto.getEndTime())) {
            entity.setEndTime(LocalDateTime.parse(dto.getEndTime().trim()));
        }
        entity.setRegistrationFormRequired("true".equalsIgnoreCase(dto.getRegistrationFormRequired()));
        entity.setPublic(!entity.isRegistrationFormRequired());

        return entity;
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    @Override
    public boolean submitForApproval(int id) {
        return changeApprovalStatus(id, ApprovalStatus.PENDING,
                "Only draft or rejected activities can be submitted for approval.",
                ApprovalStatus.DRAFT, ApprovalStatus.REJECTED);
    }

    @Override
    public boolean approveActivity(int id) {
        return changeApprovalStatus(id, ApprovalStatus.APPROVED,
                "Only activities pending approval can be approved.",
                ApprovalStatus.PENDING);
    }

    @Override
    public boolean rejectActivity(int id) {
        return changeApprovalStatus(id, ApprovalStatus.REJECTED,
                "Only activities pending approval can be rejected.",
                ApprovalStatus.PENDING);
    }

    @Override
    public boolean revokeApproval(int id) {
        if (id <= 0) {
            return false;
        }
        ExtracurricularActivity activity = activityDAO.findById(id);
        if (activity == null) {
            return false;
        }
        if (activity.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new IllegalArgumentException("Chỉ có thể hủy duyệt hoạt động đã được duyệt.");
        }
        if (activity.getActivityStatus() != ActivityStatus.UPCOMING) {
            throw new IllegalArgumentException(
                    "Không thể hủy duyệt vì hoạt động đã diễn ra, đã kết thúc hoặc đã bị hủy.");
        }

        boolean updated = activityDAO.updateApprovalStatusIfUpcoming(
                id,
                ApprovalStatus.PENDING,
                List.of(ApprovalStatus.APPROVED),
                DateTimeUtil.nowUtc()
        );

        if (!updated) {
            throw new IllegalArgumentException(
                    "Không thể hủy duyệt: hoạt động đã bắt đầu "
                            + "hoặc trạng thái duyệt đã thay đổi."
            );
        }

        return true;
    }

    @Override
    public boolean revokeRejection(int id) {
        return changeApprovalStatus(id, ApprovalStatus.PENDING,
                "Chỉ có thể hủy từ chối hoạt động đang ở trạng thái bị từ chối.",
                ApprovalStatus.REJECTED);
    }

    private boolean changeApprovalStatus(
            int id,
            ApprovalStatus target,
            String errorMessage,
            ApprovalStatus... allowedFrom) {

        if (id <= 0) {
            return false;
        }

        // Bước 1: Lấy hoạt động từ database.
        ExtracurricularActivity activity = activityDAO.findById(id);

        if (activity == null) {
            return false;
        }

        // Bước 2: Kiểm tra trạng thái duyệt hiện tại có hợp lệ không.
        if (!Arrays.asList(allowedFrom).contains(activity.getApprovalStatus())) {
            throw new IllegalArgumentException(errorMessage);
        }

        // Bước 3: Chỉ cho phép đổi trạng thái duyệt khi hoạt động UPCOMING.
        if (activity.getActivityStatus() != ActivityStatus.UPCOMING) {
            throw new IllegalArgumentException(
                    "Không thể thay đổi trạng thái duyệt vì hoạt động "
                            + "không còn ở trạng thái UPCOMING.");
        }

        // Bước 4: Cập nhật có điều kiện trực tiếp trong database.
        boolean updated = activityDAO.updateApprovalStatusIfUpcoming(
                id,
                target,
                Arrays.asList(allowedFrom),
                DateTimeUtil.nowUtc()
        );

        // Bước 5: Nếu database không cập nhật được, báo lỗi.
        if (!updated) {
            throw new IllegalArgumentException(
                    "Không thể thay đổi trạng thái duyệt: hoạt động đã bắt đầu "
                            + "hoặc trạng thái đã thay đổi. Vui lòng tải lại trang."
            );
        }

        return true;
    }

    /**
     * Đồng bộ trạng thái hoạt động theo thời gian UTC.
     */
    @Override
    public void synchronizeActivityStatuses() {

        LocalDateTime now = DateTimeUtil.nowUtc();

        // Gọi DAO cập nhật trạng thái trong database.
        activityDAO.synchronizeActivityStatuses(now);
    }

    /**
     * Loại bỏ mọi hành động thay đổi khi hoạt động đó không còn diễn ra nữa.
     */
    private ExtracurricularActivity requireUpcomingActivity(
            int id, String errorMessage) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID hoạt động không hợp lệ.");
        }

        ExtracurricularActivity activity = activityDAO.findById(id);

        if (activity == null) {
            throw new IllegalArgumentException("Không tìm thấy hoạt động.");
        }

        if (activity.getActivityStatus() != ActivityStatus.UPCOMING) {
            throw new IllegalArgumentException(errorMessage);
        }

        if (activity.getStartTime() == null
                || !DateTimeUtil.now().isBefore(activity.getStartTime())) {
            throw new IllegalArgumentException(errorMessage);
        }

        return activity;
    }


}
