package com.swp391.g1.service;

import com.swp391.g1.model.PartnerCompany;
import java.util.List;

public interface IPartnerCompanyService {

    // Lấy toàn bộ danh sách công ty đối tác
    List<PartnerCompany> getAllPartnerCompanies();

    // Lấy danh sách công ty đang hoạt động (ACTIVE) để nạp vào Select box khi tạo Hoạt động
    List<PartnerCompany> getActivePartnerCompanies();

    // Tìm công ty theo ID
    PartnerCompany getPartnerCompanyById(int id);

    // Tạo mới công ty đối tác
    boolean createPartnerCompany(PartnerCompany partnerCompany) throws Exception;

    // Cập nhật thông tin công ty đối tác
    boolean updatePartnerCompany(PartnerCompany partnerCompany) throws Exception;

    // Xóa công ty đối tác
    boolean deletePartnerCompany(int id);
}