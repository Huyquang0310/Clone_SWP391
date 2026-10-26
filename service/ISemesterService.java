package com.swp391.g1.service;

import com.swp391.g1.model.Semester;

import java.util.List;

public interface ISemesterService {
    List<Semester> getAllSemesters();

    List<Semester> getActiveSemesters();

    Semester getSemesterById(int id);

    boolean createSemester(Semester semester) throws Exception;

    boolean updateSemester(Semester semester) throws Exception;

    boolean deleteSemester(int id);
}
