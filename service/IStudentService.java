package com.swp391.g1.service;

import com.swp391.g1.model.Student;

import java.util.List;

public interface IStudentService {
    List<Student> getAllStudents();

    Student getStudentById(int id);

    Student getStudentByAccountId(int accountId);
}