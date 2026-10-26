package com.swp391.g1.dao.impl;

import com.swp391.g1.common.DBContext;
import com.swp391.g1.dao.IExtracurricularActivityDAO;
import com.swp391.g1.dto.response.ExtracurricularActivityResponseDTO;
import com.swp391.g1.model.Enum;
import com.swp391.g1.model.ExtracurricularActivity;
import com.swp391.g1.util.DateTimeUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExtracurricularActivityDAOImpl implements IExtracurricularActivityDAO {
    private static final Logger LOGGER = Logger.getLogger(ExtracurricularActivityDAOImpl.class.getName());

    private ExtracurricularActivity mapActivity(ResultSet rs) throws SQLException {
        ExtracurricularActivity activity = new ExtracurricularActivity();
        activity.setId(rs.getInt("id"));
        activity.setSemesterId(rs.getInt("semester_id"));
        activity.setCode(rs.getString("code"));
        activity.setName(rs.getString("name"));
        activity.setResponsibleDepartmentId(rs.getInt("responsible_department_id"));
        activity.setResponsibleStaffId(rs.getInt("responsible_staff_id"));
        activity.setPartnerCompanyId((Integer) rs.getObject("partner_company_id"));
        activity.setPartnerStaffId((Integer) rs.getObject("partner_staff_id"));
        String nature = rs.getString("nature");
        activity.setNature(nature == null ? null : Enum.ActivityNature.valueOf(nature));
        activity.setSummaryRequired(rs.getBoolean("is_summary_required"));
        activity.setMaxParticipants((Integer) rs.getObject("max_participants"));
        activity.setAddress(rs.getString("address"));
        activity.setDescription(rs.getString("description"));

        String activityStatus = rs.getString("activity_status");
        activity.setActivityStatus(activityStatus == null ? null : Enum.ActivityStatus.valueOf(activityStatus));
        String approvalStatus = rs.getString("approval_status");
        activity.setApprovalStatus(approvalStatus == null ? null : Enum.ApprovalStatus.valueOf(approvalStatus));

        Timestamp createdAt = rs.getTimestamp("created_at");
        activity.setCreatedAt(createdAt == null ? null : createdAt.toLocalDateTime());
        activity.setActivityTypeId(rs.getInt("activity_type_id"));

        Timestamp startTime = rs.getTimestamp("start_time");
        activity.setStartTime(startTime == null
                ? null : DateTimeUtil.fromUtc(startTime.toLocalDateTime()));
        Timestamp endTime = rs.getTimestamp("end_time");
        activity.setEndTime(endTime == null
                ? null : DateTimeUtil.fromUtc(endTime.toLocalDateTime()));
        activity.setRegistrationFormRequired(rs.getBoolean("registration_form_required"));
        activity.setPublic(rs.getBoolean("is_public"));
        return activity;
    }

    @Override
    public List<ExtracurricularActivity> findByCode(String code) {
        List<ExtracurricularActivity> list = new ArrayList<>();
        String sql = "SELECT * FROM extracurricular_activity WHERE code = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapActivity(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find extracurricular activities by code.", e);
        }
        return list;
    }

    @Override
    public List<ExtracurricularActivity> findByActivityStatus(Enum.ActivityStatus status) {
        List<ExtracurricularActivity> list = new ArrayList<>();
        String sql = "SELECT * FROM extracurricular_activity WHERE activity_status = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapActivity(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find extracurricular activities by status.", e);
        }
        return list;
    }

    @Override
    public List<ExtracurricularActivity> findByApprovalStatus(Enum.ApprovalStatus status) {
        List<ExtracurricularActivity> list = new ArrayList<>();
        String sql = "SELECT * FROM extracurricular_activity WHERE approval_status = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapActivity(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find extracurricular activities by approval status.", e);
        }
        return list;
    }

    @Override
    public List<ExtracurricularActivity> findAll() {
        List<ExtracurricularActivity> list = new ArrayList<>();
        String sql = "SELECT * FROM extracurricular_activity";

        try (Connection conn = DBContext.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapActivity(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find all extracurricular activities.", e);
        }
        return list;
    }

    @Override
    public List<ExtracurricularActivityResponseDTO> findAllWithDetails() {
        return queryActivitiesWithDetails(null, null);
    }

    @Override
    public List<ExtracurricularActivityResponseDTO> findAllWithDetailsForStaff(int accountId, String role) {
        if (accountId <= 0) {
            return List.of();
        }
        String scope = staffScopePredicate(role);
        return scope == null ? List.of() : queryActivitiesWithDetails(scope, accountId);
    }

    @Override
    public boolean isInStaffActivityScope(int activityId, int accountId, String role) {
        if (activityId <= 0 || accountId <= 0) {
            return false;
        }
        String scope = staffScopePredicate(role);
        if (scope == null) {
            return false;
        }
        String sql = "SELECT 1 FROM extracurricular_activity a WHERE a.id = ? AND " + scope;
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, activityId);
            ps.setInt(2, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to check staff activity access.", e);
        }
    }

    private String staffScopePredicate(String role) {
        if ("STAFF".equals(role)) {
            return """
                    EXISTS (SELECT 1 FROM staff scoped_staff
                            WHERE scoped_staff.account_id = ?
                              AND scoped_staff.id = a.responsible_staff_id)
                    """;
        }
        if ("UNIT_DEPUTY_HEAD".equals(role) || "UNIT_HEAD".equals(role)
                || "UNIT_SECRETARY".equals(role)) {
            return """
                    EXISTS (SELECT 1
                            FROM staff scoped_staff
                            JOIN staff_assignment sa ON sa.staff_id = scoped_staff.id
                            WHERE scoped_staff.account_id = ?
                              AND sa.department_id = a.responsible_department_id)
                    """;
        }
        return null;
    }

    private List<ExtracurricularActivityResponseDTO> queryActivitiesWithDetails(String scope, Integer accountId) {
        List<ExtracurricularActivityResponseDTO> list = new ArrayList<>();
        String sql = """
    SELECT
        a.*,
        s.code AS semester_code, s.name AS semester_name,
        t.code AS activity_type_code, t.name AS activity_type_name,
        d.code AS dept_code, d.name AS dept_name,
        st.code AS staff_code, st.name AS staff_name,
        pc.code AS company_code, pc.name AS company_name,
        ps.code AS partner_staff_code, ps.name AS partner_staff_name
    FROM extracurricular_activity a
    LEFT JOIN semester s ON a.semester_id = s.id
    LEFT JOIN activity_type t ON a.activity_type_id = t.id
    LEFT JOIN department d ON a.responsible_department_id = d.id
    LEFT JOIN staff st ON a.responsible_staff_id = st.id
    LEFT JOIN partner_company pc ON a.partner_company_id = pc.id
    LEFT JOIN partner_staff ps ON a.partner_staff_id = ps.id
    """ + (scope == null ? "" : "WHERE " + scope) + """
    ORDER BY a.id DESC
    """;

        try (Connection conn = DBContext.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ExtracurricularActivityResponseDTO dto = new ExtracurricularActivityResponseDTO();
                dto.setId(rs.getInt("id"));
                dto.setCode(rs.getString("code"));
                dto.setName(rs.getString("name"));
                dto.setSemesterId(rs.getInt("semester_id"));
                dto.setActivityTypeId(rs.getInt("activity_type_id"));
                dto.setResponsibleDepartmentId(rs.getInt("responsible_department_id"));
                dto.setResponsibleStaffId(rs.getInt("responsible_staff_id"));
                dto.setPartnerCompanyId((Integer) rs.getObject("partner_company_id"));
                dto.setPartnerStaffId((Integer) rs.getObject("partner_staff_id"));
                String nature = rs.getString("nature");
                dto.setNature(nature == null ? null : Enum.ActivityNature.valueOf(nature));
                dto.setSummaryRequired(rs.getBoolean("is_summary_required"));
                dto.setMaxParticipants((Integer) rs.getObject("max_participants"));
                dto.setAddress(rs.getString("address"));
                dto.setDescription(rs.getString("description"));
                dto.setActivityStatus(Enum.ActivityStatus.valueOf(rs.getString("activity_status")));
                dto.setApprovalStatus(Enum.ApprovalStatus.valueOf(rs.getString("approval_status")));

                Timestamp startTime = rs.getTimestamp("start_time");
                dto.setStartTime(startTime == null
                        ? null : DateTimeUtil.fromUtc(startTime.toLocalDateTime()));
                Timestamp endTime = rs.getTimestamp("end_time");
                dto.setEndTime(endTime == null
                        ? null : DateTimeUtil.fromUtc(endTime.toLocalDateTime()));
                dto.setRegistrationFormRequired(rs.getBoolean("registration_form_required"));
                dto.setPublic(rs.getBoolean("is_public"));

                // Thông tin chi tiết JOIN từ các bảng liên quan
                dto.setSemesterCode(rs.getString("semester_code"));
                dto.setSemesterName(rs.getString("semester_name"));
                dto.setActivityTypeCode(rs.getString("activity_type_code"));
                dto.setActivityTypeName(rs.getString("activity_type_name"));
                dto.setResponsibleDepartmentCode(rs.getString("dept_code"));
                dto.setResponsibleDepartmentName(rs.getString("dept_name"));
                dto.setResponsibleStaffCode(rs.getString("staff_code"));
                dto.setResponsibleStaffName(rs.getString("staff_name"));
                dto.setPartnerCompanyCode(rs.getString("company_code"));
                dto.setPartnerCompanyName(rs.getString("company_name"));
                dto.setPartnerStaffCode(rs.getString("partner_staff_code"));
                dto.setPartnerStaffName(rs.getString("partner_staff_name"));

                list.add(dto);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to query extracurricular activity list.", e);
            throw new IllegalStateException("Failed to find all activity details.", e);
        }
        return list;
    }

    @Override
    public ExtracurricularActivity findById(Integer id) {
        String sql = "SELECT * FROM extracurricular_activity WHERE id = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapActivity(rs) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find extracurricular activity by ID.", e);
        }
    }

    @Override
    public int countReservedApplications(int activityId) {
        String sql = "SELECT COUNT(*) FROM dbo.activity_registration r "
                + "JOIN dbo.activity_registration_form f ON f.id=r.form_id "
                + "WHERE f.activity_id=? AND r.status IN ('SUBMITTED','APPROVED')";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, activityId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to count reserved activity registrations.", e);
        }
    }

    @Override
    public Integer insert(ExtracurricularActivity activity) {
        String sql = "INSERT INTO extracurricular_activity "
                + "(semester_id, code, name, responsible_department_id, responsible_staff_id, "
                + "partner_company_id, partner_staff_id, nature, is_summary_required, address, description, "
                + "activity_status, approval_status, activity_type_id, start_time, end_time, registration_form_required, max_participants, is_public) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindActivity(ps, activity);
            if (ps.executeUpdate() == 0) {
                return null;
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : null;
            }
        } catch (SQLException e) {
            if (isDuplicateCodeError(e)) {
                throw new IllegalArgumentException("Extracurricular activity with the same code already exists.", e);
            }
            throw new IllegalStateException("Failed to insert extracurricular activity.", e);
        }
    }

    @Override
    public boolean update(ExtracurricularActivity activity) {
        return update(activity, null);
    }

    @Override
    public boolean update(ExtracurricularActivity activity, Enum.ApprovalStatus newApprovalStatus) {
        if (activity == null || activity.getId() <= 0) {
            return false;
        }

        String sql = "UPDATE extracurricular_activity SET "
                + "semester_id = ?, code = ?, name = ?, responsible_department_id = ?, responsible_staff_id = ?, "
                + "partner_company_id = ?, partner_staff_id = ?, nature = ?, is_summary_required = ?, "
                + "address = ?, description = ?, activity_type_id = ?, start_time = ?, end_time = ?, "
                + "registration_form_required = ?, max_participants = ?, is_public = ?"
                + (newApprovalStatus == null ? "" : ", approval_status = ?") + " WHERE id = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, activity.getSemesterId());
            ps.setString(2, activity.getCode());
            ps.setString(3, activity.getName());
            ps.setInt(4, activity.getResponsibleDepartmentId());
            ps.setInt(5, activity.getResponsibleStaffId());
            if (activity.getPartnerCompanyId() == null) {
                ps.setNull(6, Types.INTEGER);
            } else {
                ps.setInt(6, activity.getPartnerCompanyId());
            }
            if (activity.getPartnerStaffId() == null) {
                ps.setNull(7, Types.INTEGER);
            } else {
                ps.setInt(7, activity.getPartnerStaffId());
            }
            ps.setString(8, activity.getNature() == null ? null : activity.getNature().name());
            ps.setBoolean(9, activity.isSummaryRequired());
            ps.setString(10, activity.getAddress());
            ps.setString(11, activity.getDescription());
            ps.setInt(12, activity.getActivityTypeId());
            ps.setTimestamp(13, activity.getStartTime() == null
                    ? null : Timestamp.valueOf(DateTimeUtil.toUtc(activity.getStartTime())));
            ps.setTimestamp(14, activity.getEndTime() == null
                    ? null : Timestamp.valueOf(DateTimeUtil.toUtc(activity.getEndTime())));
            ps.setBoolean(15, activity.isRegistrationFormRequired());
            setMaxParticipants(ps, 16, activity);
            ps.setBoolean(17, activity.isPublic());
            int next = 18;
            if (newApprovalStatus != null) {
                ps.setString(next++, newApprovalStatus.name());
            }
            ps.setInt(next, activity.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            if (isDuplicateCodeError(e)) {
                throw new IllegalArgumentException("Mã hoạt động đã tồn tại. Vui lòng nhập mã khác.", e);
            }
            throw new IllegalStateException("Failed to update extracurricular activity: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateUpcoming(
            ExtracurricularActivity activity,
            Enum.ApprovalStatus expectedApprovalStatus,
            Enum.ApprovalStatus newApprovalStatus,
            LocalDateTime now) {
        if (activity == null || activity.getId() <= 0
                || expectedApprovalStatus == null || now == null) {
            return false;
        }

        String sql = "UPDATE a WITH (UPDLOCK,HOLDLOCK) SET "
                + "semester_id = ?, code = ?, name = ?, responsible_department_id = ?, responsible_staff_id = ?, "
                + "partner_company_id = ?, partner_staff_id = ?, nature = ?, is_summary_required = ?, "
                + "address = ?, description = ?, activity_type_id = ?, start_time = ?, end_time = ?, "
                + "registration_form_required = ?, max_participants = ?, is_public = ?"
                + (newApprovalStatus == null ? "" : ", approval_status = ?")
                + " FROM dbo.extracurricular_activity a "
                + " WHERE a.id = ? AND a.activity_status = 'UPCOMING' "
                + "AND a.start_time IS NOT NULL AND a.start_time > ? "
                + "AND a.approval_status = ? "
                + "AND (? IS NULL OR ? >= (SELECT COUNT(*) FROM dbo.activity_registration r WITH (UPDLOCK,HOLDLOCK) "
                + "JOIN dbo.activity_registration_form f ON f.id=r.form_id "
                + "WHERE f.activity_id=a.id "
                + "AND r.status IN ('SUBMITTED','APPROVED')) "
                + "OR (? IS NOT NULL AND a.max_participants = ?))";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, activity.getSemesterId());
            ps.setString(2, activity.getCode());
            ps.setString(3, activity.getName());
            ps.setInt(4, activity.getResponsibleDepartmentId());
            ps.setInt(5, activity.getResponsibleStaffId());
            if (activity.getPartnerCompanyId() == null) {
                ps.setNull(6, Types.INTEGER);
            } else {
                ps.setInt(6, activity.getPartnerCompanyId());
            }
            if (activity.getPartnerStaffId() == null) {
                ps.setNull(7, Types.INTEGER);
            } else {
                ps.setInt(7, activity.getPartnerStaffId());
            }
            ps.setString(8, activity.getNature() == null ? null : activity.getNature().name());
            ps.setBoolean(9, activity.isSummaryRequired());
            ps.setString(10, activity.getAddress());
            ps.setString(11, activity.getDescription());
            ps.setInt(12, activity.getActivityTypeId());
            ps.setTimestamp(13, activity.getStartTime() == null
                    ? null : Timestamp.valueOf(DateTimeUtil.toUtc(activity.getStartTime())));
            ps.setTimestamp(14, activity.getEndTime() == null
                    ? null : Timestamp.valueOf(DateTimeUtil.toUtc(activity.getEndTime())));
            ps.setBoolean(15, activity.isRegistrationFormRequired());
            setMaxParticipants(ps, 16, activity);
            ps.setBoolean(17, activity.isPublic());

            int next = 18;
            if (newApprovalStatus != null) {
                ps.setString(next++, newApprovalStatus.name());
            }
            ps.setInt(next++, activity.getId());
            ps.setTimestamp(next++, Timestamp.valueOf(now));
            ps.setString(next++, expectedApprovalStatus.name());
            setMaxParticipants(ps, next++, activity);
            setMaxParticipants(ps, next++, activity);
            setMaxParticipants(ps, next++, activity);
            setMaxParticipants(ps, next, activity);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            if (isDuplicateCodeError(e)) {
                throw new IllegalArgumentException("Mã hoạt động đã tồn tại. Vui lòng nhập mã khác.", e);
            }
            throw new IllegalStateException("Failed to update upcoming extracurricular activity.", e);
        }
    }

    private void setMaxParticipants(PreparedStatement ps, int index, ExtracurricularActivity activity) throws SQLException {
        if (activity.getMaxParticipants() == null) {
            ps.setNull(index, Types.INTEGER);
        } else {
            ps.setInt(index, activity.getMaxParticipants());
        }
    }

    private boolean isDuplicateCodeError(SQLException exception) {
        for (SQLException current = exception; current != null; current = current.getNextException()) {
            if (current.getErrorCode() == 2601 || current.getErrorCode() == 2627) {
                return true;
            }
        }
        return false;
    }

    private void bindActivity(PreparedStatement ps, ExtracurricularActivity activity) throws SQLException {
        ps.setInt(1, activity.getSemesterId());
        ps.setString(2, activity.getCode());
        ps.setString(3, activity.getName());
        ps.setInt(4, activity.getResponsibleDepartmentId());
        ps.setInt(5, activity.getResponsibleStaffId());
        if (activity.getPartnerCompanyId() == null) {
            ps.setNull(6, Types.INTEGER);
        } else {
            ps.setInt(6, activity.getPartnerCompanyId());
        }
        if (activity.getPartnerStaffId() == null) {
            ps.setNull(7, Types.INTEGER);
        } else {
            ps.setInt(7, activity.getPartnerStaffId());
        }
        ps.setString(8, activity.getNature() == null ? null : activity.getNature().name());
        ps.setBoolean(9, activity.isSummaryRequired());
        ps.setString(10, activity.getAddress());
        ps.setString(11, activity.getDescription());
        ps.setString(12, activity.getActivityStatus() == null ? null : activity.getActivityStatus().name());
        ps.setString(13, activity.getApprovalStatus() == null ? null : activity.getApprovalStatus().name());
        ps.setInt(14, activity.getActivityTypeId());
        ps.setTimestamp(15, activity.getStartTime() == null
                ? null : Timestamp.valueOf(DateTimeUtil.toUtc(activity.getStartTime())));
        ps.setTimestamp(16, activity.getEndTime() == null
                ? null : Timestamp.valueOf(DateTimeUtil.toUtc(activity.getEndTime())));
        ps.setBoolean(17, activity.isRegistrationFormRequired());
        setMaxParticipants(ps, 18, activity);
        ps.setBoolean(19, activity.isPublic());
    }

    @Override
    public boolean delete(Integer id) {
        if (id == null) {
            return false;
        }

        String existsSql = "SELECT 1 FROM extracurricular_activity WHERE id = ?";
        // Remove the partner-company link before its invitation because it references that invitation.
        String[] dependentDeletes = {
                "DELETE FROM activity_staff WHERE activity_id = ?",
                "DELETE FROM activity_department WHERE activity_id = ?",
                "DELETE FROM activity_partner_staff WHERE activity_id = ?",
                "DELETE FROM activity_partner_company WHERE activity_id = ?",
                "DELETE FROM activity_collaboration_invitation WHERE activity_id = ?"
        };

        try (Connection conn = DBContext.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(existsSql)) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            return false;
                        }
                    }
                }
                for (String sql : dependentDeletes) {
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setInt(1, id);
                        ps.executeUpdate();
                    }
                }
                try (PreparedStatement ps =
                             conn.prepareStatement("DELETE FROM extracurricular_activity WHERE id = ?")) {
                    ps.setInt(1, id);
                    if (ps.executeUpdate() == 0) {
                        conn.rollback();
                        return false;
                    }
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackException) {
                    e.addSuppressed(rollbackException);
                }
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete extracurricular activity.", e);
        }
    }

    @Override
    public boolean updateApprovalStatusIfUpcoming(
            int id,
            Enum.ApprovalStatus status,
            List<Enum.ApprovalStatus> allowedFrom,
            LocalDateTime now) {

        if (id <= 0 || status == null
                || allowedFrom == null || allowedFrom.isEmpty()
                || now == null) {
            return false;
        }

        StringBuilder sql = new StringBuilder(
                "UPDATE extracurricular_activity "
                        + "SET approval_status = ? "
                        + "WHERE id = ? "
                        + "AND activity_status = 'UPCOMING' "
                        + "AND start_time IS NOT NULL "
                        + "AND start_time > ? "
                        + "AND approval_status IN (");

        for (int i = 0; i < allowedFrom.size(); i++) {
            if (i > 0) {
                sql.append(",");
            }
            sql.append("?");
        }

        sql.append(")");

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            ps.setString(1, status.name());
            ps.setInt(2, id);
            ps.setTimestamp(3, Timestamp.valueOf(now));

            int index = 4;
            for (Enum.ApprovalStatus from : allowedFrom) {
                ps.setString(index++, from.name());
            }

            // Chỉ thành công nếu câu SQL cập nhật được ít nhất một dòng.
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to update approval status: " + e.getMessage(), e);
        }
    }

    /**
     * Đồng bộ trạng thái hoạt động bằng thời gian UTC.
     *
     * Xử lý cả trường hợp Tomcat tắt trong thời gian diễn ra hoạt động.
     */
    @Override
    public void synchronizeActivityStatuses(
            java.time.LocalDateTime now) {

        if (now == null) {
            throw new IllegalArgumentException(
                    "The synchronization time must not be null.");
        }

        // Transition newly rejected activities and their linked partner records atomically.
        String rejectUnapprovedSql = """
                DECLARE @RejectedActivities TABLE (id INT PRIMARY KEY);

                UPDATE extracurricular_activity
                SET approval_status = 'REJECTED',
                    activity_status = 'CANCELLED'
                OUTPUT inserted.id INTO @RejectedActivities (id)
                WHERE activity_status = 'UPCOMING'
                  AND (approval_status IS NULL OR approval_status <> 'APPROVED')
                  AND start_time IS NOT NULL
                  AND start_time <= ?;

                UPDATE i
                SET approval_status = 'CANCELLED'
                FROM activity_collaboration_invitation i
                JOIN @RejectedActivities r ON r.id = i.activity_id;

                UPDATE apc
                SET collaboration_status = 'INACTIVE'
                FROM activity_partner_company apc
                JOIN @RejectedActivities r ON r.id = apc.activity_id;

                UPDATE aps
                SET collaboration_status = 'INACTIVE'
                FROM activity_partner_staff aps
                JOIN @RejectedActivities r ON r.id = aps.activity_id;
                """;

        // 2. Đã duyệt nhưng đã qua cả giờ kết thúc: chuyển thẳng từ UPCOMING sang COMPLETED.
        String completeMissedWindowSql =
                "UPDATE extracurricular_activity " +
                        "SET activity_status = 'COMPLETED' " +
                        "WHERE activity_status = 'UPCOMING' " +
                        "AND approval_status = 'APPROVED' " +
                        "AND end_time IS NOT NULL AND end_time <= ?";

        // 3. Đã duyệt, đã bắt đầu nhưng chưa kết thúc.
        String startApprovedSql =
                "UPDATE extracurricular_activity " +
                        "SET activity_status = 'ONGOING' " +
                        "WHERE activity_status = 'UPCOMING' " +
                        "AND approval_status = 'APPROVED' " +
                        "AND start_time IS NOT NULL AND start_time <= ? " +
                        "AND end_time IS NOT NULL AND end_time > ?";

        // 4. Đang diễn ra và đã đến giờ kết thúc.
        String completeOngoingSql =
                "UPDATE extracurricular_activity " +
                        "SET activity_status = 'COMPLETED' " +
                        "WHERE activity_status = 'ONGOING' " +
                        "AND approval_status = 'APPROVED' " +
                        "AND end_time IS NOT NULL AND end_time <= ?";

        String cancelInactiveInvitationsSql =
                "UPDATE activity_collaboration_invitation " +
                        "SET approval_status = 'CANCELLED' " +
                        "WHERE approval_status = 'PENDING' " +
                        "AND activity_id IN (" +
                        "SELECT id FROM extracurricular_activity " +
                        "WHERE activity_status IN ('ONGOING', 'COMPLETED', 'CANCELLED'))";

        try (Connection conn = DBContext.getConnection()) {

            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try {
                // Bước 1: Từ chối hoạt động chưa được duyệt và đồng bộ dữ liệu liên quan.
                try (PreparedStatement ps =
                             conn.prepareStatement(rejectUnapprovedSql)) {

                    ps.setTimestamp(1, Timestamp.valueOf(now));
                    ps.execute();
                }

                // Bước 2: Bắt kịp hoạt động đã kết thúc khi Tomcat tắt.
                try (PreparedStatement ps =
                             conn.prepareStatement(completeMissedWindowSql)) {

                    ps.setTimestamp(1, Timestamp.valueOf(now));
                    ps.executeUpdate();
                }

                // Bước 3: Chuyển hoạt động đã duyệt sang ONGOING.
                try (PreparedStatement ps =
                             conn.prepareStatement(startApprovedSql)) {

                    ps.setTimestamp(1, Timestamp.valueOf(now));
                    ps.setTimestamp(2, Timestamp.valueOf(now));
                    ps.executeUpdate();
                }

                // Bước 4: Hoàn thành hoạt động đang diễn ra đã hết giờ.
                try (PreparedStatement ps =
                             conn.prepareStatement(completeOngoingSql)) {

                    ps.setTimestamp(1, Timestamp.valueOf(now));
                    ps.executeUpdate();
                }

                // Hủy lời mời còn chờ khi hoạt động không còn ở trạng thái UPCOMING.
                try (PreparedStatement ps =
                             conn.prepareStatement(cancelInactiveInvitationsSql)) {
                    ps.executeUpdate();
                }

                // Lưu tất cả thay đổi.
                conn.commit();

            } catch (SQLException e) {

                // Có lỗi thì hoàn tác toàn bộ thay đổi trong transaction.
                try {
                    conn.rollback();
                } catch (SQLException rollbackException) {
                    e.addSuppressed(rollbackException);
                }

                throw e;

            } finally {
                try {
                    conn.setAutoCommit(originalAutoCommit);
                } catch (SQLException ignored) {
                    // Connection sắp được đóng.
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to synchronize extracurricular activity statuses.",
                    e);
        }
    }

    @Override
    public boolean deleteIfNotOngoingOrCompleted(int id) {
        if (id <= 0) {
            return false;
        }

        String[] dependentDeletes = {
                "DELETE FROM activity_staff WHERE activity_id = ?",
                "DELETE FROM activity_department WHERE activity_id = ?",
                "DELETE FROM activity_partner_staff WHERE activity_id = ?",
                "DELETE FROM activity_partner_company WHERE activity_id = ?",
                "DELETE FROM activity_collaboration_invitation WHERE activity_id = ?"
        };
        String deleteActivitySql = """
                DELETE FROM extracurricular_activity
                WHERE id = ?
                  AND (
                      activity_status = 'CANCELLED'
                      OR (activity_status = 'UPCOMING'
                          AND start_time IS NOT NULL
                          AND start_time > SYSUTCDATETIME())
                  )
                """;

        try (Connection conn = DBContext.getConnection()) {
            conn.setAutoCommit(false);
            try {
                for (String sql : dependentDeletes) {
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setInt(1, id);
                        ps.executeUpdate();
                    }
                }
                try (PreparedStatement ps = conn.prepareStatement(deleteActivitySql)) {
                    ps.setInt(1, id);
                    if (ps.executeUpdate() == 0) {
                        conn.rollback();
                        return false;
                    }
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackException) {
                    e.addSuppressed(rollbackException);
                }
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to delete activity that has not started or was cancelled.", e);
        }
    }

    @Override
    public boolean setPublic(int activityId, boolean isPublic) {
        String sql = "UPDATE dbo.extracurricular_activity SET is_public=? WHERE id=?";
        try (Connection c = DBContext.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setBoolean(1, isPublic);
            p.setInt(2, activityId);
            return p.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Không thể cập nhật trạng thái hiển thị hoạt động.", e);
        }
    }

}
