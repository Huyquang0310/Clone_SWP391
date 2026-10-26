package com.swp391.g1.service.impl;

import com.swp391.g1.dao.IPartnerStaffDAO;
import com.swp391.g1.dao.impl.PartnerStaffDAOImpl;
import com.swp391.g1.dto.request.PartnerStaffRequestDTO;
import com.swp391.g1.dto.response.PartnerStaffResponseDTO;
import com.swp391.g1.model.Enum.CommonStatus;
import com.swp391.g1.model.PartnerStaff;
import com.swp391.g1.service.IPartnerStaffService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.stream.Collectors;

public class PartnerStaffServiceImpl implements IPartnerStaffService {

    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);
    private final IPartnerStaffDAO partnerStaffDAO;

    public PartnerStaffServiceImpl() {
        this.partnerStaffDAO = new PartnerStaffDAOImpl();
    }

    @Override
    public List<PartnerStaffResponseDTO> getAllPartnerStaffs() {
        return partnerStaffDAO.findAllWithDetails();
    }

    @Override
    public List<PartnerStaffResponseDTO> getStaffsByCompanyId(int companyId) {
        if (companyId <= 0) {
            return List.of();
        }
        return partnerStaffDAO.findAllWithDetails().stream()
                .filter(staff -> staff.getCompanyId() == companyId)
                .collect(Collectors.toList());
    }

    @Override
    public PartnerStaffResponseDTO getPartnerStaffById(int id) {
        if (id <= 0) {
            return null;
        }
        return partnerStaffDAO.findAllWithDetails().stream()
                .filter(staff -> staff.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean createPartnerStaff(PartnerStaffRequestDTO dto) throws Exception {
        // 1. Validate dữ liệu đầu vào
        validateRequestDTO(dto, false);

        // 2. Map sang Entity
        PartnerStaff entity = mapToEntity(dto);
        entity.setStatus(CommonStatus.ACTIVE);

        // 3. Gọi DAO insert
        Integer generatedId = partnerStaffDAO.insert(entity);
        return generatedId != null && generatedId > 0;
    }

    @Override
    public boolean updatePartnerStaff(PartnerStaffRequestDTO dto) throws Exception {
        // 1. Validate dữ liệu đầu vào (bao gồm kiểm tra ID)
        validateRequestDTO(dto, true);

        // 2. Map sang Entity
        PartnerStaff entity = mapToEntity(dto);
        entity.setId(dto.getId());
        if (!isEmpty(dto.getStatus())) {
            try {
                entity.setStatus(CommonStatus.valueOf(dto.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                entity.setStatus(CommonStatus.ACTIVE);
            }
        } else {
            entity.setStatus(CommonStatus.ACTIVE);
        }

        // 3. Gọi DAO update
        return partnerStaffDAO.update(entity);
    }

    @Override
    public boolean deletePartnerStaff(int id) {
        if (id <= 0) {
            return false;
        }
        return partnerStaffDAO.delete(id);
    }

    // =========================================================================
    // HELPER METHODS: VALIDATION & MAPPING
    // =========================================================================

    private void validateRequestDTO(PartnerStaffRequestDTO dto, boolean isUpdate) throws Exception {
        if (dto == null) {
            throw new IllegalArgumentException("Dữ liệu gửi lên không hợp lệ.");
        }

        if (isUpdate && dto.getId() <= 0) {
            throw new IllegalArgumentException("ID nhân viên không hợp lệ khi cập nhật.");
        }

        if (isEmpty(dto.getCompanyId())) {
            throw new IllegalArgumentException("Vui lòng chọn công ty đối tác.");
        }

        try {
            int companyId = Integer.parseInt(dto.getCompanyId());
            if (companyId <= 0) {
                throw new IllegalArgumentException("Công ty đối tác không hợp lệ.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID công ty đối tác phải là số.");
        }

        if (isEmpty(dto.getCode())) {
            throw new IllegalArgumentException("Mã nhân viên đối tác không được để trống.");
        }

        if (isEmpty(dto.getName())) {
            throw new IllegalArgumentException("Tên nhân viên đối tác không được để trống.");
        }

        if (!isEmpty(dto.getDob())) {
            try {
                parseDob(dto.getDob());
            } catch (java.time.format.DateTimeParseException e) {
                throw new IllegalArgumentException("Ngày sinh phải có định dạng dd-mm-yyyy.");
            }
        }

        if (!isEmpty(dto.getGender())
                && !dto.getGender().equals("1")
                && !dto.getGender().equals("0")
                && !dto.getGender().equalsIgnoreCase("true")
                && !dto.getGender().equalsIgnoreCase("false")) {
            throw new IllegalArgumentException("Giới tính không hợp lệ.");
        }

        if (!isEmpty(dto.getStatus())
                && !dto.getStatus().equalsIgnoreCase(CommonStatus.ACTIVE.name())
                && !dto.getStatus().equalsIgnoreCase(CommonStatus.INACTIVE.name())) {
            throw new IllegalArgumentException("Trạng thái nhân viên không hợp lệ.");
        }
    }

    private PartnerStaff mapToEntity(PartnerStaffRequestDTO dto) {
        PartnerStaff entity = new PartnerStaff();
        entity.setCompanyId(Integer.parseInt(dto.getCompanyId()));
        entity.setCode(dto.getCode().trim());
        entity.setName(dto.getName().trim());
        entity.setDob(isEmpty(dto.getDob()) ? null : parseDob(dto.getDob()));
        entity.setGender(isEmpty(dto.getGender()) ? null
                : dto.getGender().equals("1") || dto.getGender().equalsIgnoreCase("true"));
        entity.setPosition(isEmpty(dto.getPosition()) ? null : dto.getPosition().trim());
        return entity;
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private LocalDate parseDob(String value) {
        value = value.trim();
        if (value.matches("\\d{2}-\\d{2}-\\d{4}")) {
            return LocalDate.parse(value, DISPLAY_DATE_FORMAT);
        }
        return LocalDate.parse(value);
    }
}