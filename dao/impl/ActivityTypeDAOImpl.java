package com.swp391.g1.dao.impl;

import com.swp391.g1.common.DBContext;
import com.swp391.g1.dao.IActivityTypeDAO;
import com.swp391.g1.model.ActivityType;
import com.swp391.g1.model.Enum;

import java.sql.*;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ActivityTypeDAOImpl implements IActivityTypeDAO {

    private ActivityType mapActivityType(ResultSet rs) throws SQLException {
        ActivityType activityType = new ActivityType();
        activityType.setId(rs.getInt("id"));
        activityType.setCode(rs.getString("code"));
        activityType.setName(rs.getString("name"));
        activityType.setDescription(rs.getString("description"));
        String status = rs.getString("status");
        activityType.setStatus(status == null ? null : Enum.CommonStatus.valueOf(status));
        return activityType;
    }

    @Override
    public List<ActivityType> findByCode(String code) {
        List<ActivityType> list = new ArrayList<>();
        String sql = "SELECT * FROM activity_type WHERE code = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapActivityType(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find activity types by code.", e);
        }
        return list;
    }

    @Override
    public List<ActivityType> findByCommonStatus(Enum.CommonStatus status) {
        List<ActivityType> list = new ArrayList<>();
        String sql = "SELECT * FROM activity_type WHERE status = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapActivityType(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find activity types by status.", e);
        }
        return list;
    }

    @Override
    public List<ActivityType> findAll() {
        List<ActivityType> list = new ArrayList<>();
        String sql = "SELECT * FROM activity_type";

        try (Connection conn = DBContext.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapActivityType(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find all activity types.", e);
        }
        return list;
    }

    @Override
    public ActivityType findById(Integer id) {
        String sql = "SELECT * FROM activity_type WHERE id = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapActivityType(rs) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find activity type by ID.", e);
        }
    }

    @Override
    public Integer insert(ActivityType activityType) {
        String sql = "INSERT INTO activity_type "
                + "(code, name, description, status) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, activityType.getCode());
            ps.setString(2, activityType.getName());
            ps.setString(3, activityType.getDescription());
            if (activityType.getStatus() == null) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, activityType.getStatus().name());
            }

            if (ps.executeUpdate() == 0) {
                return null;
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to insert activity type.", e);
        }
    }

    @Override
    public boolean update(ActivityType activityType) {
        if (activityType == null || activityType.getId() <= 0) {
            return false;
        }

        String sql = "UPDATE activity_type "
                + "SET code = ?, name = ?,                 description = ?, status = ? "
                                + "WHERE id = ?";

                        try (Connection conn = DBContext.getConnection();
                             PreparedStatement ps = conn.prepareStatement(sql)) {
                            ps.setString(1, activityType.getCode());
                            ps.setString(2, activityType.getName());
                            ps.setString(3, activityType.getDescription());
                            if (activityType.getStatus() == null) {
                                ps.setNull(4, Types.VARCHAR);
                            } else {
                                ps.setString(4, activityType.getStatus().name());
                            }
                            ps.setInt(5, activityType.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update activity type.", e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        if (id == null) {
            return false;
        }

        String sql = "DELETE FROM activity_type WHERE id = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete activity type.", e);
        }
    }
}
