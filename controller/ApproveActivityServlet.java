package com.swp391.g1.controller;

import com.swp391.g1.dto.CurrentUser;
import com.swp391.g1.service.AccessPolicy;
import com.swp391.g1.service.IExtracurricularActivityService;
import com.swp391.g1.util.AuthContext;
import com.swp391.g1.service.impl.ExtracurricularActivityServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

@WebServlet("/activity/approval")
public class ApproveActivityServlet extends HttpServlet {

    private final IExtracurricularActivityService activityService =
            new ExtracurricularActivityServiceImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = request.getParameter("action");
            CurrentUser currentUser = AuthContext.getCurrentUser(request);

            if (action == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Hành động không hợp lệ.");
                return;
            }

            boolean decisionAction = "approve".equals(action) || "reject".equals(action)
                    || "revokeApproval".equals(action) || "revokeRejection".equals(action)
                    || "bulkApprove".equals(action) || "bulkReject".equals(action);
            if (decisionAction && !AccessPolicy.canReviewActivities(currentUser)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Chỉ tài khoản ADMIN mới được duyệt, từ chối hoặc hủy duyệt hoạt động.");
                return;
            }

            if ("bulkApprove".equals(action) || "bulkReject".equals(action)) {
                handleBulkDecision(request, response, action);
                return;
            }

            int id = Integer.parseInt(request.getParameter("id"));
            if ("submit".equals(action) && (!AccessPolicy.canManageActivities(currentUser)
                    || !activityService.canAccessActivity(id, currentUser))) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Chỉ tài khoản STAFF hoặc ADMIN mới được gửi duyệt hoạt động.");
                return;
            }

            boolean success;
            String message;

            switch (action) {
                case "submit":
                    success = activityService.submitForApproval(id);
                    message = "Đã gửi hoạt động đi duyệt.";
                    break;
                case "approve":
                    success = activityService.approveActivity(id);
                    message = "Đã duyệt hoạt động.";
                    break;
                case "reject":
                    success = activityService.rejectActivity(id);
                    message = "Đã từ chối hoạt động.";
                    break;
                case "revokeApproval":
                    success = activityService.revokeApproval(id);
                    message = "Đã hủy duyệt, hoạt động quay về trạng thái Chờ duyệt.";
                    break;
                case "revokeRejection":
                    success = activityService.revokeRejection(id);
                    message = "Đã hủy từ chối, hoạt động quay về trạng thái Chờ duyệt.";
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Hành động không hợp lệ.");
                    return;
            }

            if (!success) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy hoạt động.");
                return;
            }

            request.getSession().setAttribute("successMessage", message);
            response.sendRedirect(request.getContextPath() + "/activities");
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID hoạt động không hợp lệ.");
        } catch (IllegalArgumentException e) {
            request.getSession().setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/activities");
        } catch (IllegalStateException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Không thể cập nhật trạng thái duyệt.");
        }
    }

    private void handleBulkDecision(HttpServletRequest request, HttpServletResponse response, String action)
            throws IOException {
        String[] rawIds = request.getParameterValues("id");
        if (rawIds == null || rawIds.length == 0 || rawIds.length > 100) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Hãy chọn từ 1 đến 100 hoạt động.");
            return;
        }

        Set<Integer> ids = new LinkedHashSet<>();
        for (String rawId : rawIds) {
            try {
                int id = Integer.parseInt(rawId);
                if (id <= 0) {
                    throw new NumberFormatException();
                }
                ids.add(id);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID hoạt động không hợp lệ.");
                return;
            }
        }

        int updated = 0;
        for (int id : ids) {
            try {
                boolean success = "bulkApprove".equals(action)
                        ? activityService.approveActivity(id)
                        : activityService.rejectActivity(id);
                if (success) {
                    updated++;
                }
            } catch (IllegalArgumentException e) {
                // Other selected rows can still be processed if one row changed concurrently.
            }
        }

        int skipped = ids.size() - updated;
        String decision = "bulkApprove".equals(action) ? "duyệt" : "từ chối";
        String message = "Đã " + decision + " " + updated + "/" + ids.size() + " hoạt động.";
        if (skipped > 0) {
            message += " Bỏ qua " + skipped + " hoạt động không còn ở trạng thái chờ duyệt.";
        }
        request.getSession().setAttribute(updated == ids.size() ? "successMessage" : "errorMessage", message);
        response.sendRedirect(request.getContextPath() + "/activities");
    }
}
