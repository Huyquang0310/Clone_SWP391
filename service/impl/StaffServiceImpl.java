package com.swp391.g1.service.impl;

import com.swp391.g1.dao.IStaffDAO;
import com.swp391.g1.dao.impl.StaffDAOImpl;
import com.swp391.g1.model.Staff;
import com.swp391.g1.service.IStaffService;

import java.util.List;
import java.util.stream.Collectors;

public class StaffServiceImpl implements IStaffService {

    private final IStaffDAO staffDAO;

    public StaffServiceImpl() {
        this.staffDAO = new StaffDAOImpl();
    }

    @Override
    public List<Staff> getAllStaffs() {
        return staffDAO.findAll();
    }

    @Override
    public List<Staff> getActiveStaffs() {
        return staffDAO.findAll().stream()
                .filter(staff -> "ACTIVE".equals(staff.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public Staff getStaffById(int id) {
        return id <= 0 ? null : staffDAO.findById(id);
    }

    @Override
    public Staff getStaffByAccountId(int accountId) {
        return accountId <= 0 ? null : staffDAO.findByAccountId(accountId);
    }

    @Override
    public Staff getFirstStaff() {
        return staffDAO.findFirst();
    }

    @Override
    public List<Staff> getStaffsByDepartmentId(int departmentId) {
        if (departmentId <= 0) {
            return List.of();
        }
        return staffDAO.findByDepartmentId(departmentId);
    }

    @Override
    public boolean createStaff(Staff staff) throws Exception {
        validateStaff(staff, false);
        staff.setStatus("ACTIVE");
        Integer generatedId = staffDAO.insert(staff);
        return generatedId != null && generatedId > 0;
    }

    @Override
    public boolean updateStaff(Staff staff) throws Exception {
        validateStaff(staff, true);
        return staffDAO.update(staff);
    }

    @Override
    public boolean deleteStaff(int id) {
        return id > 0 && staffDAO.delete(id);
    }

    private void validateStaff(Staff staff, boolean isUpdate) {
        if (staff == null) {
            throw new IllegalArgumentException("Nhân viên không được để trống.");
        }
        if (isUpdate && staff.getId() <= 0) {
            throw new IllegalArgumentException("ID nhân viên không hợp lệ khi cập nhật.");
        }
        if (isEmpty(staff.getCode())) {
            throw new IllegalArgumentException("Mã nhân viên không được để trống.");
        }
        if (isEmpty(staff.getName())) {
            throw new IllegalArgumentException("Tên nhân viên không được để trống.");
        }
        if (!isEmpty(staff.getDob())) {
            try {
                java.time.LocalDate.parse(staff.getDob());
            } catch (java.time.format.DateTimeParseException e) {
                throw new IllegalArgumentException("Ngày sinh nhân viên phải theo định dạng yyyy-MM-dd.", e);
            }
        }
        if (!isEmpty(staff.getGender())
                && !"true".equalsIgnoreCase(staff.getGender())
                && !"false".equalsIgnoreCase(staff.getGender())
                && !"1".equals(staff.getGender())
                && !"0".equals(staff.getGender())) {
            throw new IllegalArgumentException("Giới tính nhân viên phải là true, false, 1 hoặc 0.");
        }
        if (isUpdate && isEmpty(staff.getStatus())) {
            throw new IllegalArgumentException("Trạng thái nhân viên không được để trống khi cập nhật.");
        }
        if (!isEmpty(staff.getStatus())
                && !"ACTIVE".equals(staff.getStatus())
                && !"INACTIVE".equals(staff.getStatus())) {
            throw new IllegalArgumentException("Trạng thái nhân viên phải là ACTIVE hoặc INACTIVE.");
        }
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
