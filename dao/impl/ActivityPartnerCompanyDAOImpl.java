package com.swp391.g1.dao.impl;

import com.swp391.g1.common.DBContext;
import com.swp391.g1.dao.IActivityPartnerCompanyDAO;
import com.swp391.g1.dto.response.ActivityPartnerCompanyDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ActivityPartnerCompanyDAOImpl implements IActivityPartnerCompanyDAO {

    private static final String SELECT_DETAILS = """
            SELECT apc.id, apc.activity_id, apc.partner_company_id, apc.partner_staff_id, apc.created_at, apc.collaboration_status,
                   pc.code AS company_code, pc.name AS company_name,
                   ps.code AS staff_code, ps.name AS staff_name, ps.position AS staff_position
            FROM activity_partner_company apc
            JOIN partner_company pc ON apc.partner_company_id = pc.id
            JOIN partner_staff ps ON apc.partner_staff_id = ps.id
            """;

    private ActivityPartnerCompanyDTO map(ResultSet rs) throws SQLException {
        ActivityPartnerCompanyDTO dto = new ActivityPartnerCompanyDTO();
        dto.setId(rs.getInt("id"));
        dto.setActivityId(rs.getInt("activity_id"));
        dto.setPartnerCompanyId(rs.getInt("partner_company_id"));
        dto.setPartnerCompanyCode(rs.getString("company_code"));
        dto.setPartnerCompanyName(rs.getString("company_name"));
        dto.setPartnerStaffId(rs.getInt("partner_staff_id"));
        dto.setCollaborationStatus(rs.getString("collaboration_status"));
        dto.setPartnerStaffCode(rs.getString("staff_code"));
        dto.setPartnerStaffName(rs.getString("staff_name"));
        dto.setPartnerStaffPosition(rs.getString("staff_position"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        dto.setCreatedAt(createdAt == null ? null : createdAt.toLocalDateTime());
        return dto;
    }

    @Override
    public List<ActivityPartnerCompanyDTO> findByActivityId(int activityId) {
        List<ActivityPartnerCompanyDTO> list = new ArrayList<>();
        String sql = SELECT_DETAILS + " WHERE apc.activity_id = ? ORDER BY apc.created_at, apc.id";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, activityId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find partner companies of activity " + activityId, e);
        }
        return list;
    }

    @Override
    public ActivityPartnerCompanyDTO findById(int id) {
        String sql = SELECT_DETAILS + " WHERE apc.id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find activity partner company " + id, e);
        }
    }
}
