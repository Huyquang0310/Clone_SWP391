package com.swp391.g1.dao;

import com.swp391.g1.model.Student;

public interface IStudentDAO extends IGenericDAO<Student, Integer> {
    Student findByAccountId(int accountId);
}