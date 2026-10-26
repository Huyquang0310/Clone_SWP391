package com.swp391.g1.service.impl;

import com.swp391.g1.dao.IDepartmentDAO;
import com.swp391.g1.dao.impl.DepartmentDAOImpl;
import com.swp391.g1.model.Department;
import com.swp391.g1.model.Enum.CommonStatus;
import com.swp391.g1.service.IDepartmentService;

import java.util.List;
import java.util.stream.Collectors;

public class DepartmentServiceImpl implements IDepartmentService {

    private final IDepartmentDAO departmentDAO;

    public DepartmentServiceImpl() {
        this.departmentDAO = new DepartmentDAOImpl();
    }

    @Override
    public List<Department> getAllDepartments() {
        return departmentDAO.findAll();
    }

    @Override
    public List<Department> getActiveDepartments() {
        return departmentDAO.findAll().stream()
                .filter(department -> department.getStatus() == CommonStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    @Override
    public Department getDepartmentById(int id) {
        return id <= 0 ? null : departmentDAO.findById(id);
    }

    @Override
    public boolean createDepartment(Department department) throws Exception {
        validateDepartment(department, false);
        department.setStatus(CommonStatus.ACTIVE);
        Integer generatedId = departmentDAO.insert(department);
        return generatedId != null && generatedId > 0;
    }

    @Override
    public boolean updateDepartment(Department department) throws Exception {
        validateDepartment(department, true);
        return departmentDAO.update(department);
    }

    @Override
    public boolean deleteDepartment(int id) {
        return id > 0 && departmentDAO.delete(id);
    }

    private void validateDepartment(Department department, boolean isUpdate) {
        if (department == null) {
            throw new IllegalArgumentException("Phòng ban không được để trống.");
        }
        if (isUpdate && department.getId() <= 0) {
            throw new IllegalArgumentException("ID phòng ban không hợp lệ khi cập nhật.");
        }
        if (isEmpty(department.getCode())) {
            throw new IllegalArgumentException("Mã phòng ban không được để trống.");
        }
        if (isEmpty(department.getName())) {
            throw new IllegalArgumentException("Tên phòng ban không được để trống.");
        }
        if (isUpdate && department.getStatus() == null) {
            throw new IllegalArgumentException("Trạng thái phòng ban không được để trống khi cập nhật.");
        }
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
