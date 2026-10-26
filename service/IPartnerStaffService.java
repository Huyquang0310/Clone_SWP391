package com.swp391.g1.service;

import com.swp391.g1.dto.request.PartnerStaffRequestDTO;
import com.swp391.g1.dto.response.PartnerStaffResponseDTO;

import java.util.List;

public interface IPartnerStaffService {

    // Lấy tất cả nhân viên đối tác kèm thông tin công ty (dùng DTO JOIN)
    List<PartnerStaffResponseDTO> getAllPartnerStaffs();

    // Lấy danh sách nhân viên theo ID công ty (Phục vụ lọc dynamic/AJAX khi chọn Công ty trên Form)
    List<PartnerStaffResponseDTO> getStaffsByCompanyId(int companyId);

    // Lấy chi tiết nhân viên theo ID
    PartnerStaffResponseDTO getPartnerStaffById(int id);

    // Tạo mới nhân viên đối tác
    boolean createPartnerStaff(PartnerStaffRequestDTO requestDTO) throws Exception;

    // Cập nhật nhân viên đối tác
    boolean updatePartnerStaff(PartnerStaffRequestDTO requestDTO) throws Exception;

    // Xóa nhân viên đối tác
    boolean deletePartnerStaff(int id);
}