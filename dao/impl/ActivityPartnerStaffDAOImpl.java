package com.swp391.g1.dao.impl;

import com.swp391.g1.common.DBContext;
import com.swp391.g1.dao.IActivityPartnerStaffDAO;
import com.swp391.g1.dto.response.ActivityPartnerStaffResponseDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ActivityPartnerStaffDAOImpl implements IActivityPartnerStaffDAO {

    @Override
    public List<ActivityPartnerStaffResponseDTO> findByActivityId(int activityId) {
        List<ActivityPartnerStaffResponseDTO> list = new ArrayList<>();
        String sql = """
                SELECT aps.activity_id, aps.partner_staff_id, aps.assigned_on,
                       ps.code, ps.name, ps.status, ps.position, ps.company_id, pc.name AS company_name
                FROM activity_partner_staff aps
                JOIN partner_staff ps ON aps.partner_staff_id = ps.id
                LEFT JOIN partner_company pc ON ps.company_id = pc.id
                WHERE aps.activity_id = ?
                ORDER BY aps.assigned_on DESC
                """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, activityId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ActivityPartnerStaffResponseDTO dto = new ActivityPartnerStaffResponseDTO();
                    dto.setActivityId(rs.getInt("activity_id"));
                    dto.setPartnerStaffId(rs.getInt("partner_staff_id"));
                    Timestamp ts = rs.getTimestamp("assigned_on");
                    dto.setAssignedOn(ts == null ? null : ts.toLocalDateTime());
                    dto.setPartnerStaffCode(rs.getString("code"));
                    dto.setPartnerStaffName(rs.getString("name"));
                    dto.setStatus(rs.getString("status"));
                    dto.setPosition(rs.getString("position"));
                    dto.setCompanyId(rs.getInt("company_id"));
                    dto.setCompanyName(rs.getString("company_name"));
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find assigned partner staffs for activity " + activityId, e);
        }
        return list;
    }

    @Override
    public boolean insert(int activityId, int partnerStaffId) {
        String sql = """
                INSERT INTO activity_partner_staff
                    (activity_id, partner_staff_id, assigned_on, collaboration_status)
                SELECT ?, ?, CAST(SWITCHOFFSET(SYSDATETIMEOFFSET(), '+07:00') AS DATETIME2), 'ACTIVE'
                WHERE EXISTS (
                    SELECT 1
                    FROM extracurricular_activity
                    WHERE id = ?
                      AND activity_status = 'UPCOMING'
                      AND start_time > SYSUTCDATETIME()
                )
                """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, activityId);
            ps.setInt(2, partnerStaffId);
            ps.setInt(3, activityId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to assign partner staff to activity: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int activityId, int partnerStaffId) {
        String sql = """
                DELETE aps
                FROM activity_partner_staff aps
                INNER JOIN extracurricular_activity ea ON ea.id = aps.activity_id
                WHERE aps.activity_id = ?
                  AND aps.partner_staff_id = ?
                  AND ea.activity_status = 'UPCOMING'
                  AND ea.start_time > SYSUTCDATETIME()
                """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, activityId);
            ps.setInt(2, partnerStaffId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to remove partner staff from activity.", e);
        }
    }

    @Override
    public boolean exists(int activityId, int partnerStaffId) {
        String sql = "SELECT 1 FROM activity_partner_staff WHERE activity_id = ? AND partner_staff_id = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, activityId);
            ps.setInt(2, partnerStaffId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to check partner staff assignment.", e);
        }
    }
}
