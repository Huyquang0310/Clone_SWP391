package com.swp391.g1.dao.impl;

import com.swp391.g1.common.DBContext;
import com.swp391.g1.dao.IAnswerDAO;
import com.swp391.g1.model.Answer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnswerDAOImpl implements IAnswerDAO {

    private Answer mapRow(ResultSet rs) throws SQLException {
        Answer a = new Answer();
        a.setId(rs.getInt("id"));
        a.setQuestionId(rs.getInt("question_id"));
        a.setStaffId(rs.getInt("staff_id"));
        a.setContent(rs.getString("content"));
        a.setApprovalStatus(rs.getString("approval_status"));
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) a.setCreatedAt(created.toLocalDateTime());
        Timestamp published = rs.getTimestamp("published_at");
        if (published != null) a.setPublishedAt(published.toLocalDateTime());
        try { a.setStaffName(rs.getString("staff_name")); } catch (SQLException ignored) {}
        try { a.setStaffCode(rs.getString("staff_code")); } catch (SQLException ignored) {}
        return a;
    }

    /** Get all answers for a specific question */
    @Override
    public List<Answer> getByQuestionId(int questionId) {
        List<Answer> list = new ArrayList<>();
        String sql = "SELECT a.*, st.name AS staff_name, st.code AS staff_code " +
                     "FROM [dbo].[Answer] a " +
                     "LEFT JOIN [dbo].[staff] st ON a.staff_id = st.id " +
                     "WHERE a.question_id = ? " +
                     "ORDER BY a.created_at ASC";
        try (Connection connection = DBContext.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find answers by question ID.", e);
        }
        return list;
    }

    /** Get only PUBLISHED answers for a question (public Q&A view) */
    @Override
    public List<Answer> getPublishedByQuestionId(int questionId) {
        List<Answer> list = new ArrayList<>();
        String sql = "SELECT a.*, st.name AS staff_name, st.code AS staff_code " +
                     "FROM [dbo].[Answer] a " +
                     "LEFT JOIN [dbo].[staff] st ON a.staff_id = st.id " +
                     "WHERE a.question_id = ? AND a.approval_status = 'PUBLISHED' " +
                     "ORDER BY a.created_at ASC";
        try (Connection connection = DBContext.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find published answers by question ID.", e);
        }
        return list;
    }

    /** Insert a new answer (default DRAFT status) */
    @Override
    public boolean insert(Answer answer) {
        String sql = "INSERT INTO [dbo].[Answer] (question_id, staff_id, content, approval_status, created_at) " +
                     "VALUES (?, ?, ?, 'DRAFT', CAST(SWITCHOFFSET(SYSDATETIMEOFFSET(), '+07:00') AS DATETIME2))";
        try (Connection connection = DBContext.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, answer.getQuestionId());
            ps.setInt(2, answer.getStaffId());
            ps.setString(3, answer.getContent());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to insert answer.", e);
        }
    }

    /** Approve/publish an answer */
    @Override
    public boolean publish(int answerId) {
        String sql = "UPDATE [dbo].[Answer] SET approval_status = 'PUBLISHED', published_at = CAST(SWITCHOFFSET(SYSDATETIMEOFFSET(), '+07:00') AS DATETIME2) WHERE id = ?";
        try (Connection connection = DBContext.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, answerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to publish answer.", e);
        }
    }

    /** Delete an answer */
    @Override
    public boolean delete(int answerId) {
        String sql = "DELETE FROM [dbo].[Answer] WHERE id = ?";
        try (Connection connection = DBContext.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, answerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete answer.", e);
        }
    }

    /** Get answer by id */
    @Override
    public Answer getById(int id) {
        String sql = "SELECT a.*, st.name AS staff_name, st.code AS staff_code " +
                     "FROM [dbo].[Answer] a " +
                     "LEFT JOIN [dbo].[staff] st ON a.staff_id = st.id " +
                     "WHERE a.id = ?";
        try (Connection connection = DBContext.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find answer by ID.", e);
        }
        return null;
    }
}
