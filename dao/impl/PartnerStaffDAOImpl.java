package com.swp391.g1.dao.impl;

import com.swp391.g1.common.DBContext;
import com.swp391.g1.dao.IPartnerStaffDAO;
import com.swp391.g1.dto.response.PartnerStaffResponseDTO;
import com.swp391.g1.model.Enum;
import com.swp391.g1.model.PartnerStaff;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartnerStaffDAOImpl implements IPartnerStaffDAO {

    private PartnerStaff mapPartnerStaff(ResultSet rs) throws SQLException {
        PartnerStaff partnerStaff = new PartnerStaff();
        partnerStaff.setId(rs.getInt("id"));
        partnerStaff.setCode(rs.getString("code"));
        partnerStaff.setName(rs.getString("name"));
        partnerStaff.setCompanyId(rs.getInt("company_id"));
        Date dob = rs.getDate("dob");
        partnerStaff.setDob(dob == null ? null : dob.toLocalDate());
        partnerStaff.setGender(rs.getObject("gender") == null ? null : rs.getBoolean("gender"));
        partnerStaff.setPosition(rs.getString("position"));
        String status = rs.getString("status");
        partnerStaff.setStatus(status == null ? null : Enum.CommonStatus.valueOf(status));
        Object accountId = rs.getObject("account_id");
        partnerStaff.setAccountId(accountId == null ? null : ((Number) accountId).intValue());
        return partnerStaff;
    }

    @Override
    public List<PartnerStaff> findByCode(String code) {
        List<PartnerStaff> list = new ArrayList<>();
        String sql = "SELECT * FROM partner_staff WHERE code = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,code);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPartnerStaff(rs));
                }
            }
            if(list.size() > 1){
                System.out.println("Warning: Multiple partner staff found with the same code: " + code);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find partner staff by code.", e);
        }
        return list;
    }

    @Override
    public List<PartnerStaff> findByCommonStatus(Enum.CommonStatus status) {
        List<PartnerStaff> list = new ArrayList<>();
        String sql = "SELECT * FROM partner_staff WHERE status = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPartnerStaff(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find partner staff by status.", e);
        }
        return list;
    }

    @Override
    public List<PartnerStaff> findByCompany(int companyId) {
        List<PartnerStaff> list = new ArrayList<>();
        String sql = "SELECT * FROM partner_staff WHERE company_id = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, companyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPartnerStaff(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find partner staff by company.", e);
        }
        return list;
    }

    @Override
    public PartnerStaff findByAccountId(int accountId) {
        String sql = "SELECT * FROM partner_staff WHERE account_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapPartnerStaff(rs) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find partner staff by account.", e);
        }
    }

    @Override
    public List<PartnerStaffResponseDTO> findAllWithDetails() {
        List<PartnerStaffResponseDTO> list = new ArrayList<>();

        // Câu SQL JOIN lấy dữ liệu nhân viên và thông tin công ty đối tác
        String sql = "SELECT ps.*, "
                + "pc.code AS company_code, "
                + "pc.name AS company_name "
                + "FROM partner_staff ps "
                + "LEFT JOIN partner_company pc ON ps.company_id = pc.id "
                + "ORDER BY ps.id DESC";

        try (Connection conn = DBContext.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                PartnerStaffResponseDTO dto = new PartnerStaffResponseDTO();

                // 1. Mapping các thuộc tính cơ bản
                dto.setId(rs.getInt("id"));
                dto.setCode(rs.getString("code"));
                dto.setName(rs.getString("name"));
                dto.setCompanyId(rs.getInt("company_id"));

                // 2. Mapping java.sql.Date -> java.time.LocalDate (dob)
                Date dobSql = rs.getDate("dob");
                dto.setDob(dobSql != null ? dobSql.toLocalDate() : null);

                // 3. Mapping Boolean (gender - tránh lỗi NullPointerException nếu DB null)
                if (rs.getObject("gender") != null) {
                    dto.setGender(rs.getBoolean("gender"));
                } else {
                    dto.setGender(null);
                }

                dto.setPosition(rs.getString("position"));
                dto.setStatus(rs.getString("status"));

                // 4. Mapping các trường JOIN từ partner_company (Sửa tên setter cho khớp DTO)
                dto.setCompanyCode(rs.getString("company_code"));
                dto.setCompanyName(rs.getString("company_name"));

                // 5. Mapping username (nếu trong DB có JOIN với bảng Account/User)
                try {
                    dto.setUsername(rs.getString("username"));
                } catch (SQLException e) {
                    // Bỏ qua nếu câu SQL chưa SELECT cột username
                    dto.setUsername(null);
                }

                list.add(dto);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find partner staff list with details.", e);
        }
        return list;
    }

    @Override
    public List<PartnerStaff> findAll() {
        List<PartnerStaff> list = new ArrayList<>();
        String sql = "SELECT * FROM partner_staff";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapPartnerStaff(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find all partner staff.", e);
        }
        return list;
    }

    @Override
    public PartnerStaff findById(Integer id) {
        PartnerStaff partnerStaff = null;
        String sql = "SELECT * FROM partner_staff WHERE id = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    partnerStaff = mapPartnerStaff(rs);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find partner staff by ID.", e);
        }
        return partnerStaff;
    }

    @Override
    public Integer insert(PartnerStaff partnerStaff) {
        String sql = "INSERT INTO partner_staff "
                + "(company_id, code, name, dob, gender, position, status, account_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, partnerStaff.getCompanyId());
            ps.setString(2, partnerStaff.getCode());
            ps.setString(3, partnerStaff.getName());
            ps.setDate(4, partnerStaff.getDob() == null ? null : Date.valueOf(partnerStaff.getDob()));
            if (partnerStaff.getGender() == null) {
                ps.setNull(5, Types.BIT);
            } else {
                ps.setBoolean(5, partnerStaff.getGender());
            }
            ps.setString(6, partnerStaff.getPosition());
            ps.setString(7, partnerStaff.getStatus().name());
            if (partnerStaff.getAccountId() == null) {
                ps.setNull(8, Types.INTEGER);
            } else {
                ps.setInt(8, partnerStaff.getAccountId());
            }

            if (ps.executeUpdate() == 0) {
                return null;
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to insert partner company.", e);
        }
    }

    @Override
    public boolean update(PartnerStaff entity) {
        String sql = "UPDATE partner_staff SET company_id = ?, code = ?, name = ?, "
                + "dob = ?, gender = ?, position = ?, status = ? WHERE id = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, entity.getCompanyId());
            ps.setString(2, entity.getCode());
            ps.setString(3, entity.getName());
            ps.setDate(4, entity.getDob() == null ? null : Date.valueOf(entity.getDob()));
            if (entity.getGender() == null) {
                ps.setNull(5, Types.BIT);
            } else {
                ps.setBoolean(5, entity.getGender());
            }
            ps.setString(6, entity.getPosition());
            ps.setString(7, entity.getStatus().name());
            ps.setInt(8, entity.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update partner staff.", e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM partner_staff WHERE id = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete partner staff.", e);
        }
    }
}
