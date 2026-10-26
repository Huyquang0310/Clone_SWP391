package com.swp391.g1.service.impl;

import com.swp391.g1.dao.ISemesterDAO;
import com.swp391.g1.dao.impl.SemesterDAOImpl;
import com.swp391.g1.model.Enum.CommonStatus;
import com.swp391.g1.model.Semester;
import com.swp391.g1.service.ISemesterService;

import java.util.List;
import java.util.stream.Collectors;

public class SemesterServiceImpl implements ISemesterService {

    private final ISemesterDAO semesterDAO;

    public SemesterServiceImpl() {
        this.semesterDAO = new SemesterDAOImpl();
    }

    @Override
    public List<Semester> getAllSemesters() {
        return semesterDAO.findAll();
    }

    @Override
    public List<Semester> getActiveSemesters() {
        return semesterDAO.findAll().stream()
                .filter(semester -> semester.getStatus() == CommonStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    @Override
    public Semester getSemesterById(int id) {
        return id <= 0 ? null : semesterDAO.findById(id);
    }

    @Override
    public boolean createSemester(Semester semester) throws Exception {
        validateSemester(semester, false);
        semester.setStatus(CommonStatus.ACTIVE);
        Integer generatedId = semesterDAO.insert(semester);
        return generatedId != null && generatedId > 0;
    }

    @Override
    public boolean updateSemester(Semester semester) throws Exception {
        validateSemester(semester, true);
        return semesterDAO.update(semester);
    }

    @Override
    public boolean deleteSemester(int id) {
        return id > 0 && semesterDAO.delete(id);
    }

    private void validateSemester(Semester semester, boolean isUpdate) {
        if (semester == null) {
            throw new IllegalArgumentException("Học kỳ không được để trống.");
        }
        if (isUpdate && semester.getId() <= 0) {
            throw new IllegalArgumentException("ID học kỳ không hợp lệ khi cập nhật.");
        }
        if (isEmpty(semester.getCode())) {
            throw new IllegalArgumentException("Mã học kỳ không được để trống.");
        }
        if (isEmpty(semester.getName())) {
            throw new IllegalArgumentException("Tên học kỳ không được để trống.");
        }
        if ((semester.getTimeStart() == null) != (semester.getTimeEnd() == null)) {
            throw new IllegalArgumentException("Thời gian bắt đầu và kết thúc học kỳ phải cùng được cung cấp hoặc cùng để trống.");
        }
        if (semester.getTimeStart() != null && !semester.getTimeStart().isBefore(semester.getTimeEnd())) {
            throw new IllegalArgumentException("Thời gian bắt đầu học kỳ phải trước thời gian kết thúc.");
        }
        if (isUpdate && semester.getStatus() == null) {
            throw new IllegalArgumentException("Trạng thái học kỳ không được để trống khi cập nhật.");
        }
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
