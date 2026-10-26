package com.swp391.g1.dao.impl;

import com.swp391.g1.common.DBContext;
import com.swp391.g1.dao.IStaffDAO;
import com.swp391.g1.model.Staff;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class StaffDAOImpl implements IStaffDAO {

    private Staff mapStaff(ResultSet rs) throws SQLException {
        Staff staff = new Staff();
        staff.setId(rs.getInt("id"));
        staff.setCode(rs.getString("code"));
        staff.setName(rs.getString("name"));
        Date dob = rs.getDate("dob");
        staff.setDob(dob == null ? null : dob.toString());
        boolean gender = rs.getBoolean("gender");
        staff.setGender(rs.wasNull() ? null : Boolean.toString(gender));
        staff.setStatus(rs.getString("status"));
        int accountId = rs.getInt("account_id");
        staff.setAccountId(rs.wasNull() ? 0 : accountId);
        return staff;
    }

    @Override
    public List<Staff> findAll() {
        List<Staff> staffs = new ArrayList<>();
        String sql = "SELECT * FROM staff";

        try (Connection conn = DBContext.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                staffs.add(mapStaff(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find all staff.", e);
        }
        return staffs;
    }

    @Override
    public Staff findById(Integer id) {
        String sql = "SELECT * FROM staff WHERE id = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapStaff(rs) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find staff by ID.", e);
        }
    }

    @Override
    public Staff findByAccountId(int accountId) {
        if (accountId <= 0) {
            return null;
        }
        String sql = "SELECT * FROM staff WHERE account_id = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapStaff(rs) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find staff by account ID.", e);
        }
    }

    @Override
    public Staff findFirst() {
        String sql = "SELECT TOP 1 * FROM staff ORDER BY id";

        try (Connection conn = DBContext.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? mapStaff(rs) : null;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find a staff record.", e);
        }
    }

    @Override
    public List<Staff> findByDepartmentId(int departmentId) {
        List<Staff> staffs = new ArrayList<>();
        String sql = """
                SELECT DISTINCT s.*
                FROM staff s
                JOIN staff_assignment sa ON s.id = sa.staff_id
                WHERE sa.department_id = ? AND s.status = 'ACTIVE'
                ORDER BY s.name ASC
                """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    staffs.add(mapStaff(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find staff by department ID.", e);
        }
        return staffs;
    }

    @Override
    public Integer insert(Staff staff) {
        String sql = "INSERT INTO staff (code, name, dob, gender, status, account_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, staff.getCode());
            ps.setString(2, staff.getName());
            setDate(ps, 3, staff.getDob());
            setGender(ps, 4, staff.getGender());
            ps.setString(5, staff.getStatus());
            setAccountId(ps, 6, staff.getAccountId());

            if (ps.executeUpdate() == 0) {
                return null;
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to insert staff.", e);
        }
    }

    @Override
    public boolean update(Staff staff) {
        if (staff == null || staff.getId() <= 0) {
            return false;
        }

        String sql = "UPDATE staff SET code = ?, name = ?, dob = ?, gender = ?, status = ?, account_id = ? "
                + "WHERE id = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, staff.getCode());
            ps.setString(2, staff.getName());
            setDate(ps, 3, staff.getDob());
            setGender(ps, 4, staff.getGender());
            ps.setString(5, staff.getStatus());
            setAccountId(ps, 6, staff.getAccountId());
            ps.setInt(7, staff.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update staff.", e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }

        String sql = "DELETE FROM staff WHERE id = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete staff.", e);
        }
    }

    private void setDate(PreparedStatement ps, int parameterIndex, String dob) throws SQLException {
        if (dob == null || dob.trim().isEmpty()) {
            ps.setNull(parameterIndex, Types.DATE);
        } else {
            ps.setDate(parameterIndex, Date.valueOf(dob));
        }
    }

    private void setGender(PreparedStatement ps, int parameterIndex, String gender) throws SQLException {
        if (gender == null || gender.trim().isEmpty()) {
            ps.setNull(parameterIndex, Types.BIT);
        } else if ("true".equalsIgnoreCase(gender) || "1".equals(gender)) {
            ps.setBoolean(parameterIndex, true);
        } else if ("false".equalsIgnoreCase(gender) || "0".equals(gender)) {
            ps.setBoolean(parameterIndex, false);
        } else {
            throw new IllegalArgumentException("Staff gender must be true, false, 1, or 0.");
        }
    }

    private void setAccountId(PreparedStatement ps, int parameterIndex, int accountId) throws SQLException {
        if (accountId <= 0) {
            ps.setNull(parameterIndex, Types.INTEGER);
        } else {
            ps.setInt(parameterIndex, accountId);
        }
    }
}
