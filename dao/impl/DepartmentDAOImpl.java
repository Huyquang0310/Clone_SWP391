package com.swp391.g1.dao.impl;

import com.swp391.g1.common.DBContext;
import com.swp391.g1.dao.IDepartmentDAO;
import com.swp391.g1.model.Department;
import com.swp391.g1.model.Enum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAOImpl implements IDepartmentDAO {

    private Department mapDepartment(ResultSet rs) throws SQLException {
        Department department = new Department();
        department.setId(rs.getInt("id"));
        department.setCode(rs.getString("code"));
        department.setName(rs.getString("name"));
        department.setType(rs.getString("type"));
        department.setLocation(rs.getString("location"));
        String status = rs.getString("status");
        department.setStatus(status == null ? null : Enum.CommonStatus.valueOf(status));
        return department;
    }

    @Override
    public List<Department> findAll() {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT * FROM department";

        try (Connection conn = DBContext.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                departments.add(mapDepartment(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find all departments.", e);
        }
        return departments;
    }

    @Override
    public Department findById(Integer id) {
        String sql = "SELECT * FROM department WHERE id = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapDepartment(rs) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find department by ID.", e);
        }
    }

    @Override
    public Integer insert(Department department) {
        String sql = "INSERT INTO department (code, name, type, location, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, department.getCode());
            ps.setString(2, department.getName());
            ps.setString(3, department.getType());
            ps.setString(4, department.getLocation());
            ps.setString(5, department.getStatus().name());

            if (ps.executeUpdate() == 0) {
                return null;
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to insert department.", e);
        }
    }

    @Override
    public boolean update(Department department) {
        if (department == null || department.getId() <= 0) {
            return false;
        }

        String sql = "UPDATE department SET code = ?, name = ?, type = ?, location = ?, status = ? WHERE id = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, department.getCode());
            ps.setString(2, department.getName());
            ps.setString(3, department.getType());
            ps.setString(4, department.getLocation());
            if (department.getStatus() == null) {
                ps.setNull(5, Types.VARCHAR);
            } else {
                ps.setString(5, department.getStatus().name());
            }
            ps.setInt(6, department.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update department.", e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }

        String sql = "DELETE FROM department WHERE id = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete department.", e);
        }
    }
}
