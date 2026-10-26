package com.swp391.g1.controller;

import com.swp391.g1.dao.IMessageDAO;
import com.swp391.g1.dao.impl.MessageDAOImpl;
import com.swp391.g1.dto.CurrentUser;
import com.swp391.g1.model.Message;
import com.swp391.g1.model.Staff;
import com.swp391.g1.model.Student;
import com.swp391.g1.service.AccessPolicy;
import com.swp391.g1.service.IStaffService;
import com.swp391.g1.service.IStudentService;
import com.swp391.g1.service.impl.StaffServiceImpl;
import com.swp391.g1.service.impl.StudentServiceImpl;
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
 * OrganizerInboxServlet - Row 19 in SonCN's assignment
 * Staff/Organizer views all direct messages from students and replies.
 *
 * URL patterns:
 *   GET  /organizer-inbox                           -> dashboard: list all student conversations
 *   GET  /organizer-inbox?action=chat&studentId={}  -> open chat with specific student
 *   POST /organizer-inbox?action=reply              -> send reply to student
 */
@WebServlet("/organizer-inbox")
public class OrganizerInboxServlet extends HttpServlet {

    private final IMessageDAO messageDAO = new MessageDAOImpl();
    private final IStudentService studentService = new StudentServiceImpl();
    private final IStaffService staffService = new StaffServiceImpl();

    /** Lấy staff_id từ session. Trả về -1 nếu không hợp lệ. */
    private int resolveStaffId(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        CurrentUser user = AuthContext.getCurrentUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return -1;
        }
        // ADMIN và STAFF đều có inbox; truy vấn phía dưới luôn lọc theo staffId
        // đã resolve nên một STAFF không thể đọc hội thoại của STAFF khác.
        if (!AccessPolicy.canUseSupportInbox(user)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Chỉ ADMIN/STAFF được truy cập hộp thư hỗ trợ.");
            return -1;
        }
        Staff staff = staffService.getStaffByAccountId(user.getId());
        // ADMIN có thể không có record trong bảng staff -> dùng staff đầu tiên làm fallback
        if (staff == null && "ADMIN".equals(user.getType())) {
            staff = staffService.getFirstStaff();
        }
        if (staff == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Tài khoản này chưa được liên kết với nhân viên. Vui lòng liên hệ admin.");
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

        if ("chat".equals(action)) {
            // Open conversation with a specific student
            String studentIdParam = request.getParameter("studentId");
            if (studentIdParam == null) {
                response.sendRedirect(request.getContextPath() + "/organizer-inbox");
                return;
            }
            int studentId = Integer.parseInt(studentIdParam);
            Student student = studentService.getStudentById(studentId);
            List<Message> messages = messageDAO.getConversation(studentId, staffId);

            // Mark student's messages as read
            messageDAO.markAsRead(studentId, staffId);

            request.setAttribute("student", student);
            request.setAttribute("messages", messages);
            request.setAttribute("currentStaffId", staffId);

            forward(request, response, "/WEB-INF/views/inbox/organizer-chat.jsp");
        } else {
            // Dashboard: latest message per student conversation
            List<Message> conversations = messageDAO.getLatestMessagePerStudent(staffId);
            int unreadCount = messageDAO.countUnread(staffId);

            request.setAttribute("conversations", conversations);
            request.setAttribute("unreadCount", unreadCount);
            request.setAttribute("currentStaffId", staffId);

            forward(request, response, "/WEB-INF/views/inbox/organizer-inbox.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        int staffId = resolveStaffId(request, response);
        if (staffId == -1) return;

        String action = request.getParameter("action");

        if ("reply".equals(action)) {
            int studentId = Integer.parseInt(request.getParameter("studentId"));
            String content = request.getParameter("content");

            if (content != null && !content.trim().isEmpty()) {
                Message msg = new Message();
                msg.setStudentId(studentId);
                msg.setStaffId(staffId);
                msg.setSenderType("STAFF");
                msg.setContent(content.trim());
                messageDAO.send(msg);
            }

            response.sendRedirect(request.getContextPath() + "/organizer-inbox?action=chat&studentId=" + studentId);
        } else {
            response.sendRedirect(request.getContextPath() + "/organizer-inbox");
        }
    }

    private void forward(HttpServletRequest req, HttpServletResponse res, String path)
            throws ServletException, IOException {
        RequestDispatcher rd = req.getRequestDispatcher(path);
        rd.forward(req, res);
    }
}
