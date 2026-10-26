package com.swp391.g1.dao.impl;

import com.swp391.g1.common.DBContext;
import com.swp391.g1.dao.IMessageDAO;
import com.swp391.g1.model.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAOImpl implements IMessageDAO {

    private Message mapRow(ResultSet rs) throws SQLException {
        Message m = new Message();
        m.setId(rs.getInt("id"));
        m.setStudentId(rs.getInt("student_id"));
        m.setStaffId(rs.getInt("staff_id"));
        m.setSenderType(rs.getString("sender_type"));
        m.setContent(rs.getString("content"));
        Timestamp ts = rs.getTimestamp("sent_at");
        if (ts != null) m.setSentAt(ts.toLocalDateTime());
        m.setRead(rs.getBoolean("is_read"));
        try { m.setStudentName(rs.getString("student_name")); } catch (SQLException ignored) {}
        try { m.setStudentCode(rs.getString("student_code")); } catch (SQLException ignored) {}
        try { m.setStaffName(rs.getString("staff_name")); } catch (SQLException ignored) {}
        try { m.setStaffCode(rs.getString("staff_code")); } catch (SQLException ignored) {}
        return m;
    }

    /** Get all messages in a conversation between student and staff */
    @Override
    public List<Message> getConversation(int studentId, int staffId) {
        List<Message> list = new ArrayList<>();
        String sql = "SELECT m.*, " +
                     "s.name AS student_name, s.code AS student_code, " +
                     "st.name AS staff_name, st.code AS staff_code " +
                     "FROM [dbo].[Message] m " +
                     "LEFT JOIN [dbo].[student] s ON m.student_id = s.id " +
                     "LEFT JOIN [dbo].[staff] st ON m.staff_id = st.id " +
                     "WHERE m.student_id = ? AND m.staff_id = ? " +
                     "ORDER BY m.sent_at ASC";
        try (Connection connection = DBContext.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, staffId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find conversation.", e);
        }
        return list;
    }

    /** Get all student conversations for the organizer dashboard (latest message per student) */
    @Override
    public List<Message> getLatestMessagePerStudent(int staffId) {
        List<Message> list = new ArrayList<>();
        String sql = "SELECT m.*, " +
                     "s.name AS student_name, s.code AS student_code, " +
                     "st.name AS staff_name, st.code AS staff_code " +
                     "FROM [dbo].[Message] m " +
                     "INNER JOIN (" +
                     "  SELECT student_id, MAX(sent_at) AS max_sent " +
                     "  FROM [dbo].[Message] WHERE staff_id = ? GROUP BY student_id" +
                     ") latest ON m.student_id = latest.student_id AND m.sent_at = latest.max_sent " +
                     "LEFT JOIN [dbo].[student] s ON m.student_id = s.id " +
                     "LEFT JOIN [dbo].[staff] st ON m.staff_id = st.id " +
                     "WHERE m.staff_id = ? " +
                     "ORDER BY m.sent_at DESC";
        try (Connection connection = DBContext.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, staffId);
            ps.setInt(2, staffId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find latest messages by student.", e);
        }
        return list;
    }

    /** Get all messages sent by this student (student inbox view) */
    @Override
    public List<Message> getStudentMessages(int studentId) {
        List<Message> list = new ArrayList<>();
        String sql = "SELECT m.*, " +
                     "s.name AS student_name, s.code AS student_code, " +
                     "st.name AS staff_name, st.code AS staff_code " +
                     "FROM [dbo].[Message] m " +
                     "LEFT JOIN [dbo].[student] s ON m.student_id = s.id " +
                     "LEFT JOIN [dbo].[staff] st ON m.staff_id = st.id " +
                     "WHERE m.student_id = ? " +
                     "ORDER BY m.sent_at DESC";
        try (Connection connection = DBContext.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find student messages.", e);
        }
        return list;
    }

    /** Send a message */
    @Override
    public boolean send(Message msg) {
        String sql = "INSERT INTO [dbo].[Message] (student_id, staff_id, sender_type, content, sent_at, is_read) " +
                     "VALUES (?, ?, ?, ?, CAST(SWITCHOFFSET(SYSDATETIMEOFFSET(), '+07:00') AS DATETIME2), 0)";
        try (Connection connection = DBContext.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, msg.getStudentId());
            ps.setInt(2, msg.getStaffId());
            ps.setString(3, msg.getSenderType());
            ps.setString(4, msg.getContent());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to send message.", e);
        }
    }

    /** Mark messages as read (for staff reading student messages) */
    @Override
    public void markAsRead(int studentId, int staffId) {
        String sql = "UPDATE [dbo].[Message] SET is_read = 1 " +
                     "WHERE student_id = ? AND staff_id = ? AND sender_type = 'STUDENT'";
        try (Connection connection = DBContext.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, staffId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to mark messages as read.", e);
        }
    }

    /** Count unread messages for a staff */
    @Override
    public int countUnread(int staffId) {
        String sql = "SELECT COUNT(*) FROM [dbo].[Message] WHERE staff_id = ? AND sender_type = 'STUDENT' AND is_read = 0";
        try (Connection connection = DBContext.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, staffId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to count unread messages.", e);
        }
        return 0;
    }
}
