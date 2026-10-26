package com.swp391.g1.controller;

import com.swp391.g1.dao.IAnswerDAO;
import com.swp391.g1.dao.IQuestionDAO;
import com.swp391.g1.dao.impl.AnswerDAOImpl;
import com.swp391.g1.dao.impl.QuestionDAOImpl;
import com.swp391.g1.dto.CurrentUser;
import com.swp391.g1.model.Answer;
import com.swp391.g1.model.Question;
import com.swp391.g1.model.Staff;
import com.swp391.g1.service.AccessPolicy;
import com.swp391.g1.service.IStaffService;
import com.swp391.g1.service.impl.StaffServiceImpl;
import com.swp391.g1.util.AuthContext;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * AnswerQuestionServlet - Row 17 in SonCN's assignment
 * Staff/Organizer views pending questions and posts answers.
 * Answers can be saved as DRAFT or immediately PUBLISHED to the public Q&A channel.
 *
 * URL patterns:
 *   GET  /qa-management          -> list all questions with status
 *   GET  /qa-management?action=view&id={qId} -> view question detail + existing answers
 *   POST /qa-management?action=answer        -> submit a new answer
 *   POST /qa-management?action=publish       -> publish (approve) a DRAFT answer
 *   POST /qa-management?action=delete        -> delete an answer
 */
@WebServlet("/qa-management")
public class AnswerQuestionServlet extends HttpServlet {

    private final IQuestionDAO questionDAO = new QuestionDAOImpl();
    private final IAnswerDAO answerDAO = new AnswerDAOImpl();
    private final IStaffService staffService = new StaffServiceImpl();

    /** Resolve the staff identity used by Answer's staff_id foreign key. */
    private int resolveStaffId(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        CurrentUser user = AuthContext.getCurrentUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return -1;
        }
        // Chỉ STAFF mới được truy cập QA management
        // ADMIN (= Organizer) và STAFF đều có thể trả lời câu hỏi
        if (!AccessPolicy.canManageQuestions(user)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Chỉ ADMIN/STAFF mới được truy cập trang này.");
            return -1;
        }
        Staff staff = staffService.getStaffByAccountId(user.getId());
        // Database policy only permits STAFF accounts in dbo.staff. ADMIN is an
        // organizer account, so use the configured organizer/staff identity in
        // the same way as OrganizerInboxServlet.
        if (staff == null && "ADMIN".equals(user.getType())) {
            staff = staffService.getFirstStaff();
        }
        if (staff == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Tài khoản STAFF chưa được liên kết với hồ sơ nhân viên.");
            return -1;
        }
        return staff.getId();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int staffId = resolveStaffId(request, response);
        if (staffId == -1) return;

        String action = request.getParameter("action");

        if ("view".equals(action)) {
            // View a single question with all its answers
            String idParam = request.getParameter("id");
            if (idParam == null) {
                response.sendRedirect(request.getContextPath() + "/qa-management");
                return;
            }
            int questionId;
            try {
                questionId = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Mã câu hỏi không hợp lệ.");
                return;
            }
            Question question = questionDAO.getById(questionId);
            if (question == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy câu hỏi.");
                return;
            }
            List<Answer> answers = answerDAO.getByQuestionId(questionId);

            request.setAttribute("question", question);
            request.setAttribute("answers", answers);
            request.setAttribute("currentStaffId", staffId);

            forward(request, response, "/WEB-INF/views/qa/answer-question.jsp");
        } else {
            // Default: list all questions
            String filter = request.getParameter("filter");
            List<Question> questions;
            if ("pending".equals(filter)) {
                questions = questionDAO.getPendingQuestions();
            } else {
                questions = questionDAO.getAllQuestions();
            }

            request.setAttribute("questions", questions);
            request.setAttribute("filter", filter == null ? "all" : filter);
            forward(request, response, "/WEB-INF/views/qa/qa-management.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        int staffId = resolveStaffId(request, response);
        if (staffId == -1) return;

        String action = request.getParameter("action");

        switch (action == null ? "" : action) {
            case "answer"  -> handleAnswer(request, response, staffId);
            case "publish" -> handlePublish(request, response);
            case "delete"  -> handleDelete(request, response);
            default -> response.sendRedirect(request.getContextPath() + "/qa-management");
        }
    }

    private void handleAnswer(HttpServletRequest request, HttpServletResponse response, int staffId)
            throws IOException {
        Integer questionId = parsePositiveInt(request.getParameter("questionId"));
        String content = normalize(request.getParameter("content"));
        if (questionId == null || questionDAO.getById(questionId) == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Câu hỏi không hợp lệ.");
            return;
        }
        if (content.isEmpty() || content.length() > 4000) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Nội dung trả lời phải có từ 1 đến 4.000 ký tự.");
            return;
        }
        String publishNow = request.getParameter("publishNow"); // "1" if publish immediately

        Answer answer = new Answer();
        answer.setQuestionId(questionId);
        answer.setStaffId(staffId);
        answer.setContent(content);

        boolean saved = answerDAO.insert(answer);

        if (saved && "1".equals(publishNow)) {
            // Get the latest answer for this question by this staff and publish it
            List<Answer> answers = answerDAO.getByQuestionId(questionId);
            if (!answers.isEmpty()) {
                Answer latest = answers.get(answers.size() - 1);
                answerDAO.publish(latest.getId());
                questionDAO.updateStatus(questionId, "ANSWERED");
            }
        } else if (saved) {
            // Check if there are now published answers -> update question status
            List<Answer> published = answerDAO.getPublishedByQuestionId(questionId);
            if (!published.isEmpty()) {
                questionDAO.updateStatus(questionId, "ANSWERED");
            }
        }

        response.sendRedirect(request.getContextPath() + "/qa-management?action=view&id=" + questionId
                + "&msg=" + (saved ? "success" : "error"));
    }

    private void handlePublish(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Integer answerId = parsePositiveInt(request.getParameter("answerId"));
        Integer questionId = parsePositiveInt(request.getParameter("questionId"));
        Answer answer = answerId == null ? null : answerDAO.getById(answerId);
        if (answer == null || questionId == null || answer.getQuestionId() != questionId) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Câu trả lời không hợp lệ.");
            return;
        }

        boolean ok = answerDAO.publish(answerId);
        if (ok) {
            questionDAO.updateStatus(questionId, "ANSWERED");
        }

        response.sendRedirect(request.getContextPath() + "/qa-management?action=view&id=" + questionId
                + "&msg=" + (ok ? "published" : "error"));
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Integer answerId = parsePositiveInt(request.getParameter("answerId"));
        Integer questionId = parsePositiveInt(request.getParameter("questionId"));
        Answer answer = answerId == null ? null : answerDAO.getById(answerId);
        if (answer == null || questionId == null || answer.getQuestionId() != questionId) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Câu trả lời không hợp lệ.");
            return;
        }

        answerDAO.delete(answerId);

        // If no published answers remain, revert question status to PENDING
        List<Answer> published = answerDAO.getPublishedByQuestionId(questionId);
        if (published.isEmpty()) {
            questionDAO.updateStatus(questionId, "PENDING");
        }

        response.sendRedirect(request.getContextPath() + "/qa-management?action=view&id=" + questionId
                + "&msg=deleted");
    }

    private void forward(HttpServletRequest req, HttpServletResponse res, String path)
            throws ServletException, IOException {
        RequestDispatcher rd = req.getRequestDispatcher(path);
        rd.forward(req, res);
    }

    private static Integer parsePositiveInt(String value) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : null;
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
