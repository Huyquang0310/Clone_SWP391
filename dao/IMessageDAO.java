package com.swp391.g1.dao;

import java.util.List;

import com.swp391.g1.model.Message;

public interface IMessageDAO {
    List<Message> getConversation(int studentId, int staffId);

    List<Message> getLatestMessagePerStudent(int staffId);

    List<Message> getStudentMessages(int studentId);

    boolean send(Message message);

    void markAsRead(int studentId, int staffId);

    int countUnread(int staffId);
}
