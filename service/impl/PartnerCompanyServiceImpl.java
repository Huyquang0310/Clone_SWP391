package com.swp391.g1.service.impl;

import com.swp391.g1.dao.IPartnerCompanyDAO;
import com.swp391.g1.dao.impl.PartnerCompanyDAOImpl;
import com.swp391.g1.model.Enum.CommonStatus;
import com.swp391.g1.model.PartnerCompany;
import com.swp391.g1.service.IPartnerCompanyService;

import java.util.List;
import java.util.stream.Collectors;

public class PartnerCompanyServiceImpl implements IPartnerCompanyService {

    private final IPartnerCompanyDAO partnerCompanyDAO;

    public PartnerCompanyServiceImpl() {
        this.partnerCompanyDAO = new PartnerCompanyDAOImpl();
    }

    @Override
    public List<PartnerCompany> getAllPartnerCompanies() {
        return partnerCompanyDAO.findAll();
    }

    @Override
    public List<PartnerCompany> getActivePartnerCompanies() {
        return partnerCompanyDAO.findAll().stream()
                .filter(company -> company.getStatus() == CommonStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    @Override
    public PartnerCompany getPartnerCompanyById(int id) {
        if (id <= 0) {
            return null;
        }
        return partnerCompanyDAO.findById(id);
    }

    @Override
    public boolean createPartnerCompany(PartnerCompany partnerCompany) throws Exception {
        // 1. Validate nghiệp vụ
        validatePartnerCompany(partnerCompany, false);

        // 2. Mặc định trạng thái ACTIVE khi tạo mới
        partnerCompany.setStatus(CommonStatus.ACTIVE);

        // 3. Gọi DAO lưu vào CSDL
        Integer generatedId = partnerCompanyDAO.insert(partnerCompany);
        return generatedId != null && generatedId > 0;
    }

    @Override
    public boolean updatePartnerCompany(PartnerCompany partnerCompany) throws Exception {
        // 1. Validate nghiệp vụ (bao gồm kiểm tra ID)
        validatePartnerCompany(partnerCompany, true);

        // 2. Gọi DAO cập nhật
        return partnerCompanyDAO.update(partnerCompany);
    }

    @Override
    public boolean deletePartnerCompany(int id) {
        if (id <= 0) {
            return false;
        }
        return partnerCompanyDAO.delete(id);
    }

    // =========================================================================
    // HELPER METHOD: VALIDATION NGHIỆP VỤ
    // =========================================================================

    private void validatePartnerCompany(PartnerCompany entity, boolean isUpdate) throws Exception {
        if (entity == null) {
            throw new IllegalArgumentException("Dữ liệu công ty đối tác không hợp lệ.");
        }

        if (isUpdate && entity.getId() <= 0) {
            throw new IllegalArgumentException("ID công ty đối tác không hợp lệ khi cập nhật.");
        }

        if (isEmpty(entity.getCode())) {
            throw new IllegalArgumentException("Mã công ty đối tác không được để trống.");
        }

        if (isEmpty(entity.getName())) {
            throw new IllegalArgumentException("Tên công ty đối tác không được để trống.");
        }

        // Validate định dạng Email (nếu người dùng có nhập)
        if (!isEmpty(entity.getEmail()) && !entity.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Địa chỉ email công ty không đúng định dạng.");
        }

        // Validate Số điện thoại (chỉ chứa 9 - 11 chữ số)
        if (!isEmpty(entity.getPhoneNumber()) && !entity.getPhoneNumber().matches("^\\d{9,11}$")) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ (phải chứa từ 9 đến 11 chữ số).");
        }
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}