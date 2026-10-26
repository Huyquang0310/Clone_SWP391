package com.swp391.g1.dao.impl;

import com.swp391.g1.common.DBContext;
import com.swp391.g1.dao.IActivityCollaborationInvitationDAO;
import com.swp391.g1.dto.response.ActivityCollaborationInvitationDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ActivityCollaborationInvitationDAOImpl implements IActivityCollaborationInvitationDAO {

    private static final String SELECT_DETAILS = """
            SELECT i.id, i.activity_id, i.partner_company_id, i.partner_staff_id, i.approval_status,
                   i.invitation_type, i.message, i.created_by, i.created_at,
                   pc.code AS company_code, pc.name AS company_name,
                   ps.code AS staff_code, ps.name AS staff_name, ps.position AS staff_position,
                   acc.username AS created_by_name,
                   apc.collaboration_status AS collaboration_status,
                   ea.code AS activity_code, ea.name AS activity_name, ea.approval_status AS activity_approval_status,
                   ea.activity_status AS activity_status, ea.start_time AS activity_start_time,
                   ea.end_time AS activity_end_time,
                   atype.name AS activity_type_name
            FROM activity_collaboration_invitation i
            JOIN partner_company pc ON i.partner_company_id = pc.id
            LEFT JOIN partner_staff ps ON i.partner_staff_id = ps.id
            LEFT JOIN account acc ON acc.id = TRY_CAST(i.created_by AS INT)
            LEFT JOIN activity_partner_company apc ON apc.invitation_id = i.id
            JOIN extracurricular_activity ea ON ea.id = i.activity_id
            LEFT JOIN activity_type atype ON atype.id = ea.activity_type_id
            """;

    private ActivityCollaborationInvitationDTO map(ResultSet rs) throws SQLException {
        ActivityCollaborationInvitationDTO dto = new ActivityCollaborationInvitationDTO();
        dto.setId(rs.getInt("id"));
        dto.setActivityId(rs.getInt("activity_id"));
        dto.setPartnerCompanyId(rs.getInt("partner_company_id"));
        dto.setPartnerCompanyCode(rs.getString("company_code"));
        dto.setPartnerCompanyName(rs.getString("company_name"));
        dto.setPartnerStaffId((Integer) rs.getObject("partner_staff_id"));
        dto.setPartnerStaffCode(rs.getString("staff_code"));
        dto.setPartnerStaffName(rs.getString("staff_name"));
        dto.setPartnerStaffPosition(rs.getString("staff_position"));
        dto.setApprovalStatus(rs.getString("approval_status"));
        dto.setInvitationType(rs.getString("invitation_type"));
        dto.setMessage(rs.getString("message"));
        dto.setCollaborationStatus(rs.getString("collaboration_status"));
        dto.setActivityCode(rs.getString("activity_code"));
        dto.setActivityName(rs.getString("activity_name"));
        dto.setActivityTypeName(rs.getString("activity_type_name"));
        dto.setActivityApprovalStatus(rs.getString("activity_approval_status"));
        dto.setActivityStatus(rs.getString("activity_status"));
        Timestamp start = rs.getTimestamp("activity_start_time");
        dto.setActivityStartTime(start == null ? null : start.toLocalDateTime());
        Timestamp end = rs.getTimestamp("activity_end_time");
        dto.setActivityEndTime(end == null ? null : end.toLocalDateTime());
        dto.setCreatedBy(rs.getString("created_by"));
        dto.setCreatedByName(rs.getString("created_by_name"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        dto.setCreatedAt(createdAt == null ? null : createdAt.toLocalDateTime());
        return dto;
    }

    @Override
    public List<ActivityCollaborationInvitationDTO> findByActivityId(int activityId) {
        List<ActivityCollaborationInvitationDTO> list = new ArrayList<>();
        String sql = SELECT_DETAILS + " WHERE i.activity_id = ? ORDER BY i.created_at DESC, i.id DESC";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, activityId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find collaboration invitations of activity " + activityId, e);
        }
        return list;
    }

    @Override
    public List<ActivityCollaborationInvitationDTO> findByPartnerCompanyId(Integer partnerCompanyId) {
        List<ActivityCollaborationInvitationDTO> list = new ArrayList<>();
        String sql = SELECT_DETAILS + (partnerCompanyId == null ? "" : " WHERE i.partner_company_id = ?")
                + " ORDER BY i.created_at DESC, i.id DESC";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (partnerCompanyId != null) {
                ps.setInt(1, partnerCompanyId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find collaboration invitations.", e);
        }
        return list;
    }

    @Override
    public java.util.Set<Integer> findVisibleActivityIds(int partnerCompanyId) {
        String sql = "SELECT activity_id FROM activity_collaboration_invitation "
                + "WHERE partner_company_id = ? AND invitation_type = ? AND approval_status IN (?, ?) "
                + "UNION SELECT activity_id FROM activity_partner_company "
                + "WHERE partner_company_id = ? AND collaboration_status = 'ACTIVE'";
        java.util.Set<Integer> ids = new java.util.HashSet<>();
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, partnerCompanyId);
            ps.setString(2, TYPE_COLLABORATION);
            ps.setString(3, PENDING);
            ps.setString(4, APPROVED);
            ps.setInt(5, partnerCompanyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find visible activities of partner company.", e);
        }
        return ids;
    }

    @Override
    public int cancelPendingForInactiveActivities() {
        String sql = "UPDATE activity_collaboration_invitation SET approval_status = ? "
                + "WHERE approval_status = ? AND activity_id IN "
                + "(SELECT id FROM extracurricular_activity "
                + "WHERE activity_status IN ('ONGOING', 'COMPLETED', 'CANCELLED'))";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, CANCELLED);
            ps.setString(2, PENDING);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to cancel pending invitations for inactive activities.", e);
        }
    }

    @Override
    public ActivityCollaborationInvitationDTO findById(int id) {
        String sql = SELECT_DETAILS + " WHERE i.id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find collaboration invitation " + id, e);
        }
    }

    @Override
    public boolean existsPendingInvitation(int activityId, int partnerCompanyId) {
        String sql = "SELECT 1 FROM activity_collaboration_invitation "
                + "WHERE activity_id = ? AND partner_company_id = ? AND approval_status = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, activityId);
            ps.setInt(2, partnerCompanyId);
            ps.setString(3, PENDING);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to check pending collaboration invitation.", e);
        }
    }

    @Override
    public boolean existsOpenInvitation(int activityId, int partnerCompanyId) {
        if (existsPendingInvitation(activityId, partnerCompanyId)) {
            return true;
        }
        String sql = "SELECT 1 FROM activity_partner_company "
                + "WHERE activity_id = ? AND partner_company_id = ? AND collaboration_status = 'ACTIVE'";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, activityId);
            ps.setInt(2, partnerCompanyId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to check existing collaboration.", e);
        }
    }

    @Override
    public boolean insert(int activityId, int partnerCompanyId, int partnerStaffId, String createdBy,
                          String invitationType, String message) {
        String insertSql = """
                INSERT INTO activity_collaboration_invitation
                    (activity_id, partner_company_id, partner_staff_id, approval_status,
                     invitation_type, message, created_by)
                SELECT ?, ?, ?, ?, ?, ?, ?
                WHERE EXISTS (
                    SELECT 1
                    FROM extracurricular_activity
                    WHERE id = ?
                      AND activity_status = 'UPCOMING'
                      AND start_time > SYSUTCDATETIME()
                )
                """;
        String reuseSql = "UPDATE activity_partner_company SET partner_staff_id = ?, invitation_id = ?, "
                + "collaboration_status = 'INACTIVE' WHERE activity_id = ? AND partner_company_id = ?";
        String linkSql = "INSERT INTO activity_partner_company "
                + "(activity_id, partner_company_id, partner_staff_id, invitation_id, collaboration_status) "
                + "VALUES (?, ?, ?, ?, 'INACTIVE')";
        try (Connection conn = DBContext.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int invitationId;
                try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, activityId);
                    ps.setInt(2, partnerCompanyId);
                    ps.setInt(3, partnerStaffId);
                    ps.setString(4, PENDING);
                    ps.setString(5, invitationType);
                    ps.setString(6, message);
                    ps.setString(7, createdBy);
                    ps.setInt(8, activityId);
                    if (ps.executeUpdate() == 0) {
                        conn.rollback();
                        return false;
                    }
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) {
                            conn.rollback();
                            return false;
                        }
                        invitationId = keys.getInt(1);
                    }
                }
                if (TYPE_COLLABORATION.equals(invitationType)) {
                    int reused;
                    try (PreparedStatement ps = conn.prepareStatement(reuseSql)) {
                        ps.setInt(1, partnerStaffId);
                        ps.setInt(2, invitationId);
                        ps.setInt(3, activityId);
                        ps.setInt(4, partnerCompanyId);
                        reused = ps.executeUpdate();
                    }
                    if (reused == 0) {
                        try (PreparedStatement ps = conn.prepareStatement(linkSql)) {
                            ps.setInt(1, activityId);
                            ps.setInt(2, partnerCompanyId);
                            ps.setInt(3, partnerStaffId);
                            ps.setInt(4, invitationId);
                            ps.executeUpdate();
                        }
                    }
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to create collaboration invitation: "
                            + e.getMessage(), e);
        }
    }

    @Override
    public boolean accept(int id) {
        return changeStatus(id, PENDING, APPROVED);
    }

    @Override
    public boolean reject(int id) {
        return changeStatus(id, PENDING, REJECTED);
    }

    @Override
    public boolean cancel(int id) {
        return changeStatus(id, PENDING, CANCELLED);
    }

    @Override
    public boolean revokeApproval(int id) {
        return changeStatus(id, APPROVED, PENDING);
    }

    @Override
    public boolean revokeRejection(int id) {
        return changeStatus(id, REJECTED, PENDING);
    }

    /**
     * Moves the invitation between statuses and applies its effect on the partner row in one transaction.
     * COLLABORATION invitations drive the collaboration status of their own partner row;
     * a TERMINATION invitation only changes the collaboration when it is approved.
     */
    private boolean changeStatus(int id, String fromStatus, String toStatus) {
        String updateInvitation = """
                UPDATE i
                SET approval_status = ?
                FROM activity_collaboration_invitation i
                JOIN extracurricular_activity a ON a.id = i.activity_id
                WHERE i.id = ?
                  AND i.approval_status = ?
                  AND a.activity_status = 'UPCOMING'
                  AND a.start_time > SYSUTCDATETIME()
                """;
        String selectType = "SELECT invitation_type FROM activity_collaboration_invitation WHERE id = ?";
        String updateOwnLink = "UPDATE activity_partner_company SET collaboration_status = ? WHERE invitation_id = ?";
        String createLink = "INSERT INTO activity_partner_company "
                + "(activity_id, partner_company_id, partner_staff_id, invitation_id, collaboration_status) "
                + "SELECT activity_id, partner_company_id, partner_staff_id, id, ? "
                + "FROM activity_collaboration_invitation WHERE id = ? AND partner_staff_id IS NOT NULL";
        String deactivateCompanyLink = """
                UPDATE apc SET collaboration_status = 'INACTIVE'
                FROM activity_partner_company apc
                JOIN activity_collaboration_invitation i ON i.activity_id = apc.activity_id
                     AND i.partner_company_id = apc.partner_company_id
                WHERE i.id = ?
                """;
        String deleteStaff = """
                DELETE aps FROM activity_partner_staff aps
                JOIN partner_staff ps ON aps.partner_staff_id = ps.id
                JOIN activity_collaboration_invitation i ON i.activity_id = aps.activity_id
                     AND i.partner_company_id = ps.company_id
                WHERE i.id = ?
                """;
        try (Connection conn = DBContext.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(updateInvitation)) {
                    ps.setString(1, toStatus);
                    ps.setInt(2, id);
                    ps.setString(3, fromStatus);
                    if (ps.executeUpdate() == 0) {
                        conn.rollback();
                        return false;
                    }
                }
                String type;
                try (PreparedStatement ps = conn.prepareStatement(selectType)) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        type = rs.next() ? rs.getString(1) : TYPE_COLLABORATION;
                    }
                }

                if (TYPE_TERMINATION.equals(type)) {
                    if (APPROVED.equals(toStatus)) {
                        executeById(conn, deleteStaff, id);
                        executeById(conn, deactivateCompanyLink, id);
                    }
                } else {
                    String collaborationStatus = APPROVED.equals(toStatus) ? "ACTIVE" : "INACTIVE";
                    if (APPROVED.equals(fromStatus)) {
                        executeById(conn, deleteStaff, id);
                    }
                    int linked;
                    try (PreparedStatement ps = conn.prepareStatement(updateOwnLink)) {
                        ps.setString(1, collaborationStatus);
                        ps.setInt(2, id);
                        linked = ps.executeUpdate();
                    }
                    if (linked == 0) {
                        try (PreparedStatement ps = conn.prepareStatement(createLink)) {
                            ps.setString(1, collaborationStatus);
                            ps.setInt(2, id);
                            if (ps.executeUpdate() == 0) {
                                conn.rollback();
                                return false;
                            }
                        }
                    }
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to update collaboration invitation: "
                            + e.getMessage(), e);
        }
    }

    private void executeById(Connection conn, String sql, int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
