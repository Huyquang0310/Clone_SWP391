package com.swp391.g1.dao;
import com.swp391.g1.dto.response.PartnerStaffResponseDTO;
import com.swp391.g1.model.PartnerCompany;
import com.swp391.g1.model.PartnerStaff;
import com.swp391.g1.model.Enum;
import java.util.List;

public interface IPartnerStaffDAO extends IGenericDAO<PartnerStaff, Integer> {
    List<PartnerStaff> findByCode(String code);
    List<PartnerStaff> findByCommonStatus(Enum.CommonStatus status);
    List<PartnerStaff> findByCompany(int companyId);
    PartnerStaff findByAccountId(int accountId);
    List<PartnerStaffResponseDTO> findAllWithDetails();
}
