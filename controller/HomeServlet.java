package com.swp391.g1.controller;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.swp391.g1.service.AccessPolicy;
import com.swp391.g1.util.AuthContext;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        var currentUser = AuthContext.getCurrentUser(request);

        // UI hints — server-side enforcement is still in each servlet
        request.setAttribute("canManageAccounts", AccessPolicy.canManageAccounts(currentUser));
        request.setAttribute("canManageActivities", AccessPolicy.canManageActivities(currentUser));
        request.setAttribute("canManageQuestions", AccessPolicy.canManageQuestions(currentUser));
        request.setAttribute("canUseStudentQuestions", AccessPolicy.canUseStudentQuestions(currentUser));
        request.setAttribute("canUseSupportInbox", AccessPolicy.canUseSupportInbox(currentUser));

        String type = currentUser != null ? currentUser.getType() : "";
        // ADMIN = Organizer: có QA management + Organizer inbox
        request.setAttribute("isOrganizer", "ADMIN".equals(type));
        // STAFF: có một số tính năng riêng
        request.setAttribute("isStaff", "STAFF".equals(type));
        // STUDENT: có Student inbox
        request.setAttribute("isStudent", "STUDENT".equals(type));

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/home/home.jsp");
        dispatcher.forward(request, response);
    }
}
