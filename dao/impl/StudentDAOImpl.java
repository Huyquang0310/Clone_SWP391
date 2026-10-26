package com.swp391.g1.dao.impl;

import com.swp391.g1.common.DBContext;
import com.swp391.g1.dao.IStudentDAO;
import com.swp391.g1.model.Student;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StudentDAOImpl implements IStudentDAO {

    private static final String SELECT_BASE = """
            SELECT s.id, s.main_class_id, s.account_id, mc.code AS mainClassCode,
                   s.code, s.name, s.dob, s.gender, s.email,
                   s.phone_number, s.status
            FROM student s
            LEFT JOIN main_class mc ON mc.id = s.main_class_id
            """;

    private Student mapStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        int mainClassId = rs.getInt("main_class_id");
        student.setMainClassId(rs.wasNull() ? 0 : mainClassId);
        student.setAccountId(rs.getInt("account_id"));
        student.setMainClassCode(rs.getString("mainClassCode"));
        student.setCode(rs.getString("code"));
        student.setName(rs.getString("name"));

        Date dob = rs.getDate("dob");
        student.setDob(dob == null ? null : dob.toString());

        boolean gender = rs.getBoolean("gender");
        student.setGender(rs.wasNull() ? null : Boolean.toString(gender));

        student.setEmail(rs.getString("email"));
        student.setPhoneNumber(rs.getString("phone_number"));
        student.setStatus(rs.getString("status"));
        return student;
    }

    @Override
    public List<Student> findAll() {
        List<Student> list = new ArrayList<>();
        String sql = SELECT_BASE + " ORDER BY s.code";

        try (Connection conn = DBContext.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapStudent(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find all students.", e);
        }
        return list;
    }

    @Override
    public Student findById(Integer id) {
        String sql = SELECT_BASE + " WHERE s.id = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapStudent(rs) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find student by ID.", e);
        }
    }

    @Override
    public Student findByAccountId(int accountId) {
        if (accountId <= 0) {
            return null;
        }
        String sql = SELECT_BASE + " WHERE s.account_id = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapStudent(rs) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find student by account ID.", e);
        }
    }

    @Override
    public Integer insert(Student student) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public boolean update(Student student) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public boolean delete(Integer id) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}