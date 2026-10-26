package com.swp391.g1.dao;

import java.util.List;

import com.swp391.g1.model.Question;

public interface IQuestionDAO {
    List<Question> getAllQuestions();

    List<Question> getPendingQuestions();

    List<Question> getPublishedQuestions();

    List<Question> getQuestionsByStudent(int studentId);

    List<Question> getVisibleQuestionsForStudent(int studentId);

    boolean insert(Question question);

    Question getById(int id);

    boolean updateStatus(int questionId, String status);
}
