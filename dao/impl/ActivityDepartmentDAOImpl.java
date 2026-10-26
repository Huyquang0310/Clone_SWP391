package com.swp391.g1.dao.impl;

import com.swp391.g1.common.DBContext;
import com.swp391.g1.dao.IActivityDepartmentDAO;
import com.swp391.g1.dto.response.ActivityDepartmentResponseDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ActivityDepartmentDAOImpl implements IActivityDepartmentDAO {

    @Override
    public List<ActivityDepartmentResponseDTO> findByActivityId(int activityId) {
        List<ActivityDepartmentResponseDTO> list = new ArrayList<>();
        String sql = """
                SELECT ad.activity_id, ad.department_id, ad.created_at,
                       d.code AS department_code, d.name AS department_name, d.status AS department_status
                FROM activity_department ad
                JOIN department d ON d.id = ad.department_id
                WHERE ad.activity_id = ?
                ORDER BY d.name ASC
                """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, activityId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ActivityDepartmentResponseDTO dto = new ActivityDepartmentResponseDTO();
                    dto.setActivityId(rs.getInt("activity_id"));
                    dto.setDepartmentId(rs.getInt("department_id"));
                    dto.setDepartmentCode(rs.getString("department_code"));
                    dto.setDepartmentName(rs.getString("department_name"));
                    dto.setDepartmentStatus(rs.getString("department_status"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    dto.setCreatedAt(ts == null ? null : ts.toLocalDateTime());
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find co-responsible departments for activity " + activityId, e);
        }
        return list;
    }

    @Override
    public boolean insert(int activityId, int departmentId) {
        String sql = """
                INSERT INTO activity_department (activity_id, department_id)
                SELECT ?, ?
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
            ps.setInt(2, departmentId);
            ps.setInt(3, activityId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to add co-responsible department to activity.", e);
        }
    }

    @Override
    public boolean delete(int activityId, int departmentId) {
        String deleteDepartmentSql = """
                DELETE ad
                FROM activity_department ad
                INNER JOIN extracurricular_activity ea WITH (UPDLOCK, HOLDLOCK)
                    ON ea.id = ad.activity_id
                WHERE ad.activity_id = ?
                  AND ad.department_id = ?
                  AND ea.activity_status = 'UPCOMING'
                  AND ea.start_time > SYSUTCDATETIME()
                """;
        try (Connection conn = DBContext.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(deleteDepartmentSql)) {
                    ps.setInt(1, activityId);
                    ps.setInt(2, departmentId);
                    if (ps.executeUpdate() == 0) {
                        conn.rollback();
                        return false;
                    }
                }
                deleteStaffOutsideDepartments(conn, activityId);
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
            throw new IllegalStateException("Failed to remove co-responsible department from activity.", e);
        }
    }

    @Override
    public boolean exists(int activityId, int departmentId) {
        String sql = "SELECT 1 FROM activity_department WHERE activity_id = ? AND department_id = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, activityId);
            ps.setInt(2, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to check co-responsible department.", e);
        }
    }

    private int deleteStaffOutsideDepartments(Connection conn, int activityId) throws SQLException {
        String sql = """
                DELETE FROM activity_staff
                WHERE activity_id = ?
                  AND NOT EXISTS (
                      SELECT 1
                      FROM staff_assignment sa
                      WHERE sa.staff_id = activity_staff.staff_id
                        AND (sa.department_id = (SELECT responsible_department_id
                                                 FROM extracurricular_activity WHERE id = ?)
                             OR sa.department_id IN (SELECT department_id FROM activity_department
                                                     WHERE activity_id = ?))
                  )
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, activityId);
            ps.setInt(2, activityId);
            ps.setInt(3, activityId);
            return ps.executeUpdate();
        }
    }
}
