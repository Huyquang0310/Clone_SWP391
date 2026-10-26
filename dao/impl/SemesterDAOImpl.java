package com.swp391.g1.dao.impl;

import com.swp391.g1.common.DBContext;
import com.swp391.g1.dao.ISemesterDAO;
import com.swp391.g1.model.Enum;
import com.swp391.g1.model.Semester;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class SemesterDAOImpl implements ISemesterDAO {

    private Semester mapSemester(ResultSet rs) throws SQLException {
        Semester semester = new Semester();
        semester.setId(rs.getInt("id"));
        semester.setCode(rs.getString("code"));
        semester.setName(rs.getString("name"));
        semester.setDescription(rs.getString("description"));
        Timestamp timeStart = rs.getTimestamp("time_start");
        semester.setTimeStart(timeStart == null ? null : timeStart.toLocalDateTime());
        Timestamp timeEnd = rs.getTimestamp("time_end");
        semester.setTimeEnd(timeEnd == null ? null : timeEnd.toLocalDateTime());
        String status = rs.getString("status");
        semester.setStatus(status == null ? null : Enum.CommonStatus.valueOf(status));
        return semester;
    }

    @Override
    public List<Semester> findAll() {
        List<Semester> semesters = new ArrayList<>();
        String sql = "SELECT * FROM semester";

        try (Connection conn = DBContext.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                semesters.add(mapSemester(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find all semesters.", e);
        }
        return semesters;
    }

    @Override
    public Semester findById(Integer id) {
        String sql = "SELECT * FROM semester WHERE id = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapSemester(rs) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find semester by ID.", e);
        }
    }

    @Override
    public Integer insert(Semester semester) {
        String sql = "INSERT INTO semester (code, name, description, time_start, time_end, status) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, semester.getCode());
            ps.setString(2, semester.getName());
            ps.setString(3, semester.getDescription());
            setTimestamp(ps, 4, semester.getTimeStart());
            setTimestamp(ps, 5, semester.getTimeEnd());
            ps.setString(6, semester.getStatus().name());

            if (ps.executeUpdate() == 0) {
                return null;
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to insert semester.", e);
        }
    }

    @Override
    public boolean update(Semester semester) {
        if (semester == null || semester.getId() <= 0) {
            return false;
        }

        String sql = "UPDATE semester SET code = ?, name = ?, description = ?, "
                + "time_start = ?, time_end = ?, status = ? WHERE id = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, semester.getCode());
            ps.setString(2, semester.getName());
            ps.setString(3, semester.getDescription());
            setTimestamp(ps, 4, semester.getTimeStart());
            setTimestamp(ps, 5, semester.getTimeEnd());
            ps.setString(6, semester.getStatus().name());
            ps.setInt(7, semester.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update semester.", e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }

        String sql = "DELETE FROM semester WHERE id = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete semester.", e);
        }
    }

    private void setTimestamp(PreparedStatement ps, int parameterIndex, java.time.LocalDateTime value)
            throws SQLException {
        if (value == null) {
            ps.setNull(parameterIndex, Types.TIMESTAMP);
        } else {
            ps.setTimestamp(parameterIndex, Timestamp.valueOf(value));
        }
    }
}
