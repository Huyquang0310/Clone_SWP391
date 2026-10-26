package com.swp391.g1.controller;

import com.swp391.g1.dto.CurrentUser;
import com.swp391.g1.dto.request.ExtracurricularActivityRequestDTO;
import com.swp391.g1.dto.response.ExtracurricularActivityResponseDTO;
import com.swp391.g1.dto.response.PartnerStaffResponseDTO;
import com.swp391.g1.model.Department;
import com.swp391.g1.model.Staff;
import com.swp391.g1.service.*;
import com.swp391.g1.service.impl.*;
import com.swp391.g1.util.AuthContext;
import com.swp391.g1.util.DateTimeUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@WebServlet(name = "ExtracurricularActivityServlet",
        urlPatterns = {"/activities", "/activities/staffs-by-department"})
public class ExtracurricularActivityServlet extends HttpServlet {

    private static final DateTimeFormatter FILTER_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private final IExtracurricularActivityService activityService;
    private final IActivityTypeService activityTypeService;
    private final IPartnerCompanyService partnerCompanyService;
    private final IPartnerStaffService partnerStaffService;
    private final ISemesterService semesterService;
    private final IDepartmentService departmentService;
    private final IStaffService staffService;
    private final IActivityStaffService activityStaffService;
    private final IActivityPartnerStaffService activityPartnerStaffService;
    private final IActivityCollaborationInvitationService invitationService;
    private final IRegistrationFormService registrationFormService;

    public ExtracurricularActivityServlet() {
        this.activityPartnerStaffService = new ActivityPartnerStaffServiceImpl();
        this.invitationService = new ActivityCollaborationInvitationServiceImpl();
        this.registrationFormService = new RegistrationFormServiceImpl();
        this.activityService = new ExtracurricularActivityServiceImpl();
        this.activityTypeService = new ActivityTypeServiceImpl();
        this.partnerCompanyService = new PartnerCompanyServiceImpl();
        this.partnerStaffService = new PartnerStaffServiceImpl();
        this.semesterService = new SemesterServiceImpl();
        this.departmentService = new DepartmentServiceImpl();
        this.staffService = new StaffServiceImpl();
        this.activityStaffService = new ActivityStaffServiceImpl();
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        action = action == null ? "" : action.trim();

        boolean isGetRequest = "GET".equalsIgnoreCase(request.getMethod())
                || "HEAD".equalsIgnoreCase(request.getMethod());
        boolean requiresManagementPermission =
                (isGetRequest
                        && ("create".equals(action) || "edit".equals(action)))
                || ("POST".equalsIgnoreCase(request.getMethod())
                        && ("create".equals(action) || "update".equals(action) || "delete".equals(action)))
                || ("POST".equalsIgnoreCase(request.getMethod())
                        && ("inviteCollaborator".equals(action) || "cancelInvitation".equals(action)
                                || "terminatePartner".equals(action)));
        if (requiresManagementPermission
                && !AccessPolicy.canManageActivities(AuthContext.getCurrentUser(request))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Chỉ tài khoản STAFF hoặc ADMIN mới được thêm, sửa, xóa hoạt động.");
            return;
        }

        super.service(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if ("/activities/staffs-by-department".equals(request.getServletPath())) {
            try {
                getStaffsByDepartmentJson(request, response);
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            return;
        }

        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = "list";
        }

        try {
            switch (action) {
                case "create":
                    showCreateForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "detail":
                    showDetail(request, response);
                    break;
                case "getStaffsByCompany":
                    getStaffsByCompanyJson(request, response);
                    break;
                case "getStaffsByDepartment":
                    getStaffsByDepartmentJson(request, response);
                    break;
                case "list":
                default:
                    listActivities(request, response);
                    break;
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("errorMessage", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = "list";
        }

        try {
            switch (action) {
                case "create":
                    createActivity(request, response);
                    break;
                case "delete":
                    deleteActivity(request, response);
                    break;
                case "update":
                    updateActivity(request, response);
                    break;
                case "assignStaff":
                    assignStaff(request, response);
                    break;
                case "unassignStaff":
                    unassignStaff(request, response);
                    break;
                case "addDepartment":
                    addDepartment(request, response);
                    break;
                case "removeDepartment":
                    removeDepartment(request, response);
                    break;
                case "assignPartnerStaff":
                    assignPartnerStaff(request, response);
                    break;
                case "inviteCollaborator":
                    handleInvitation(request, response, "invite");
                    break;
                case "respondInvitation":
                    handleInvitation(request, response, "respond");
                    break;
                case "cancelInvitation":
                    handleInvitation(request, response, "cancel");
                    break;
                case "revokeInvitation":
                    handleInvitation(request, response, "revoke");
                    break;
                case "terminatePartner":
                    handleInvitation(request, response, "terminate");
                    break;
                case "unassignPartnerStaff":
                    unassignPartnerStaff(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/activities");
                    break;
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("errorMessage", "Lỗi xử lý dữ liệu: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        }
    }

    // =========================================================================
    // GET ACTION HANDLERS
    // =========================================================================

    private void listActivities(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = trimToEmpty(request.getParameter("keyword"));
        int semesterId = parseId(request.getParameter("semesterId"));
        int activityTypeId = parseId(request.getParameter("activityTypeId"));
        int departmentId = parseId(request.getParameter("departmentId"));
        String activityStatus = parseEnumName(request.getParameter("activityStatus"),
                com.swp391.g1.model.Enum.ActivityStatus.values());
        String approvalStatus = parseEnumName(request.getParameter("approvalStatus"),
                com.swp391.g1.model.Enum.ApprovalStatus.values());
        LocalDate startDate = parseDate(request.getParameter("startDate"));
        LocalDate endDate = parseDate(request.getParameter("endDate"));
        String normalizedKeyword = normalizeText(keyword);

        Set<Integer> visibleActivityIds =
                invitationService.getVisibleActivityIds(AuthContext.getCurrentUser(request));
        List<ExtracurricularActivityResponseDTO> allActivities = new ArrayList<>();
        CurrentUser currentUser = AuthContext.getCurrentUser(request);
        boolean studentView = currentUser != null && "STUDENT".equals(currentUser.getType());
        Map<Integer, com.swp391.g1.model.RegistrationForm> activityFormsByActivity = new HashMap<>();
        for (ExtracurricularActivityResponseDTO activity :
                activityService.getActivitiesVisibleTo(currentUser)) {
            if (studentView && (!activity.isPublic()
                    || activity.getApprovalStatus() != com.swp391.g1.model.Enum.ApprovalStatus.APPROVED)) {
                continue;
            }
            if (visibleActivityIds != null && !visibleActivityIds.contains(activity.getId())) {
                continue;
            }
            if (!normalizedKeyword.isEmpty()
                    && !normalizeText(activity.getCode()).contains(normalizedKeyword)
                    && !normalizeText(activity.getName()).contains(normalizedKeyword)) {
                continue;
            }
            if (semesterId > 0 && activity.getSemesterId() != semesterId) {
                continue;
            }
            if (activityTypeId > 0 && activity.getActivityTypeId() != activityTypeId) {
                continue;
            }
            if (departmentId > 0 && activity.getResponsibleDepartmentId() != departmentId) {
                continue;
            }
            if (!activityStatus.isEmpty() && !activityStatus.equals(String.valueOf(activity.getActivityStatus()))) {
                continue;
            }
            if (!approvalStatus.isEmpty() && !approvalStatus.equals(String.valueOf(activity.getApprovalStatus()))) {
                continue;
            }
            if (startDate != null && (activity.getStartTime() == null
                    || activity.getStartTime().toLocalDate().isBefore(startDate))) {
                continue;
            }
            if (endDate != null && (activity.getEndTime() == null
                    || activity.getEndTime().toLocalDate().isAfter(endDate))) {
                continue;
            }
            allActivities.add(activity);
            if (studentView && activity.isRegistrationFormRequired()) {
                com.swp391.g1.model.RegistrationForm registrationForm = registrationFormService.getByActivityId(activity.getId());
                if (registrationForm != null) activityFormsByActivity.put(activity.getId(), registrationForm);
            }
        }

        request.setAttribute("isStudent", studentView);
        request.setAttribute("activityFormsByActivity", activityFormsByActivity);
        boolean filtered = !normalizedKeyword.isEmpty() || semesterId > 0 || activityTypeId > 0
                || departmentId > 0 || !activityStatus.isEmpty() || !approvalStatus.isEmpty()
                || startDate != null || endDate != null;
        request.setAttribute("keyword", keyword);
        request.setAttribute("selectedActivityStatus", activityStatus);
        request.setAttribute("selectedApprovalStatus", approvalStatus);
        request.setAttribute("selectedSemesterId", semesterId);
        request.setAttribute("selectedActivityTypeId", activityTypeId);
        request.setAttribute("selectedDepartmentId", departmentId);
        request.setAttribute("startDate", formatDate(startDate));
        request.setAttribute("endDate", formatDate(endDate));
        request.setAttribute("filtered", filtered);
        request.setAttribute("activeFilterCount",
                (normalizedKeyword.isEmpty() ? 0 : 1) + (semesterId > 0 ? 1 : 0)
                        + (activityTypeId > 0 ? 1 : 0) + (departmentId > 0 ? 1 : 0)
                        + (activityStatus.isEmpty() ? 0 : 1) + (approvalStatus.isEmpty() ? 0 : 1)
                        + (startDate != null ? 1 : 0) + (endDate != null ? 1 : 0));
        request.setAttribute("filterQuery", buildFilterQuery(keyword, semesterId, activityTypeId,
                departmentId, activityStatus, approvalStatus, startDate, endDate));
        request.setAttribute("activityStatuses", com.swp391.g1.model.Enum.ActivityStatus.values());
        request.setAttribute("approvalStatuses", com.swp391.g1.model.Enum.ApprovalStatus.values());
        request.setAttribute("semesters", semesterService.getAllSemesters());
        request.setAttribute("activityTypes", activityTypeService.getActiveActivityTypes());
        request.setAttribute("departments", departmentService.getAllDepartments());

        int pageSize = parsePageSize(request.getParameter("pageSize"));
        int totalActivityCount = allActivities.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalActivityCount / pageSize));
        int currentPage = parsePageNumber(request.getParameter("page"), totalPages);
        int fromIndex = Math.min((currentPage - 1) * pageSize, totalActivityCount);
        int toIndex = Math.min(fromIndex + pageSize, totalActivityCount);

        List<ExtracurricularActivityResponseDTO> pageActivities =
                new ArrayList<>(allActivities.subList(fromIndex, toIndex));
        LocalDateTime now = DateTimeUtil.now();
        Set<Integer> activitiesBeforeStart = new LinkedHashSet<>();
        for (ExtracurricularActivityResponseDTO activity : pageActivities) {
            if (isUpcomingBeforeStart(activity, now)) {
                activitiesBeforeStart.add(activity.getId());
            }
        }
        request.setAttribute("activities", pageActivities);
        request.setAttribute("activitiesBeforeStart", activitiesBeforeStart);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalActivityCount", totalActivityCount);
        request.setAttribute("firstActivityNumber", totalActivityCount == 0 ? 0 : fromIndex + 1);
        request.setAttribute("lastActivityNumber", toIndex);
        setActivityManagementPermission(request);
        request.getRequestDispatcher("/WEB-INF/views/activity/list.jsp").forward(request, response);
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeText(String value) {
        if (value == null) {
            return "";
        }
        return Normalizer.normalize(value.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace('đ', 'd').replace('Đ', 'D')
                .toLowerCase(Locale.ROOT);
    }

    private LocalDate parseDate(String value) {
        try {
            return value == null || value.isBlank()
                    ? null : LocalDate.parse(value.trim(), FILTER_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private String formatDate(LocalDate date) {
        return date == null ? "" : date.format(FILTER_DATE_FORMAT);
    }

    private String parseEnumName(String value, java.lang.Enum<?>[] allowed) {
        String candidate = trimToEmpty(value);
        for (java.lang.Enum<?> option : allowed) {
            if (option.name().equals(candidate)) {
                return candidate;
            }
        }
        return "";
    }

    private String buildFilterQuery(String keyword, int semesterId, int activityTypeId,
            int departmentId, String activityStatus, String approvalStatus,
            LocalDate startDate, LocalDate endDate) {
        StringBuilder query = new StringBuilder();
        appendParam(query, "keyword", keyword);
        appendParam(query, "semesterId", semesterId > 0 ? String.valueOf(semesterId) : "");
        appendParam(query, "activityTypeId", activityTypeId > 0 ? String.valueOf(activityTypeId) : "");
        appendParam(query, "departmentId", departmentId > 0 ? String.valueOf(departmentId) : "");
        appendParam(query, "activityStatus", activityStatus);
        appendParam(query, "approvalStatus", approvalStatus);
        appendParam(query, "startDate", formatDate(startDate));
        appendParam(query, "endDate", formatDate(endDate));
        return query.toString();
    }

    private void appendParam(StringBuilder query, String name, String value) {
        if (!value.isEmpty()) {
            query.append('&').append(name).append('=')
                    .append(URLEncoder.encode(value, StandardCharsets.UTF_8));
        }
    }

    private int parsePageSize(String value) {
        try {
            int pageSize = Integer.parseInt(value);
            return Set.of(5, 10, 20, 25).contains(pageSize) ? pageSize : 5;
        } catch (NumberFormatException e) {
            return 5;
        }
    }

    private int parsePageNumber(String value, int totalPages) {
        try {
            int page = Integer.parseInt(value);
            return Math.max(1, Math.min(page, totalPages));
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ExtracurricularActivityRequestDTO formData =
                (ExtracurricularActivityRequestDTO) request.getAttribute("formData");
        loadDropdownData(request, parseId(formData == null ? null : formData.getResponsibleDepartmentId()));
        request.getRequestDispatcher("/WEB-INF/views/activity/create.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.trim().isEmpty()) {
            int id = Integer.parseInt(idStr);
            ExtracurricularActivityResponseDTO activity = activityService.getActivityById(id);
            CurrentUser currentUser = AuthContext.getCurrentUser(request);
            if (activity != null && AccessPolicy.hasType(currentUser, "STAFF")
                    && !activityService.canAccessActivity(id, currentUser)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Bạn không có quyền sửa hoạt động này.");
                return;
            }
            if (activity != null && !isUpcomingBeforeStart(activity, DateTimeUtil.now())) {
                request.getSession().setAttribute("errorMessage",
                        "Chỉ có thể sửa hoạt động sắp diễn ra trước thời điểm bắt đầu.");
                response.sendRedirect(request.getContextPath() + "/activities");
                return;
            }
            if (activity != null) {
                request.setAttribute("activity", activity);
                ExtracurricularActivityRequestDTO formData =
                        (ExtracurricularActivityRequestDTO) request.getAttribute("formData");
                int departmentId = formData == null
                        ? activity.getResponsibleDepartmentId()
                        : parseId(formData.getResponsibleDepartmentId());
                loadDropdownData(request, departmentId);

                request.getRequestDispatcher("/WEB-INF/views/activity/edit.jsp").forward(request, response);
                return;
            }
        }
        response.sendRedirect(request.getContextPath() + "/activities");
    }

    private void showDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.trim().isEmpty()) {
            int id = Integer.parseInt(idStr);
            ExtracurricularActivityResponseDTO activity = activityService.getActivityById(id);
            CurrentUser currentUser = AuthContext.getCurrentUser(request);
            if (activity != null && AccessPolicy.hasType(currentUser, "STAFF")
                    && !activityService.canAccessActivity(id, currentUser)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền xem hoạt động này.");
                return;
            }
            Set<Integer> visibleActivityIds =
                    invitationService.getVisibleActivityIds(AuthContext.getCurrentUser(request));
            if (activity != null && visibleActivityIds != null && !visibleActivityIds.contains(id)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền xem hoạt động này.");
                return;
            }
            if (activity != null) {
                request.setAttribute("activity", activity);
                if (AccessPolicy.canManageActivities(currentUser)) {
                    request.setAttribute("registrationForm", registrationFormService.getByActivityId(id));
                }
                request.setAttribute("activityBeforeStart",
                        isUpcomingBeforeStart(activity, DateTimeUtil.now()));
                setActivityManagementPermission(request);
                if (AccessPolicy.canManageActivities(AuthContext.getCurrentUser(request))) {
                    request.setAttribute("partnerCompanies", partnerCompanyService.getActivePartnerCompanies());
                }
                boolean canAssignStaff = activityStaffService.canAssignStaff(id, currentUser);
                request.setAttribute("canAddStaff", canAssignStaff);
                request.setAttribute("canRemoveStaff", canAssignStaff);
                request.setAttribute("invitations", invitationService.getInvitations(id, currentUser));
                request.setAttribute("approvedPartners", invitationService.getApprovedPartners(id));
                request.setAttribute("assignedStaffs", activityStaffService.getStaffsByActivityId(id));
                request.setAttribute("coDepartments", activityStaffService.getDepartmentsByActivityId(id));
                if (canAssignStaff) {
                    request.setAttribute("availableDepartments", activityStaffService.getAvailableDepartmentsForActivity(id));
                    request.setAttribute("availableStaffs", activityStaffService.getAvailableStaffsForActivity(id));
                    request.setAttribute("staffDepartmentLabels", activityStaffService.getDepartmentLabelsByStaff(id));
                }
                boolean canManagePartnerStaff = activityPartnerStaffService.canManagePartnerStaff(id, currentUser);
                request.setAttribute("canManagePartnerStaff", canManagePartnerStaff);
                request.setAttribute("assignedPartnerStaffs", activityPartnerStaffService.getPartnerStaffsByActivityId(id, currentUser));
                if (canManagePartnerStaff) {
                    request.setAttribute("availablePartnerStaffs",
                            activityPartnerStaffService.getAvailablePartnerStaffsForActivity(id, currentUser));
                }
                request.getRequestDispatcher("/WEB-INF/views/activity/detail.jsp").forward(request, response);
                return;
            }
        }
        response.sendRedirect(request.getContextPath() + "/activities");
    }

    private boolean isUpcomingBeforeStart(ExtracurricularActivityResponseDTO activity, LocalDateTime now) {
        return activity != null
                && activity.getActivityStatus() == com.swp391.g1.model.Enum.ActivityStatus.UPCOMING
                && activity.getStartTime() != null
                && now.isBefore(activity.getStartTime());
    }

    private void deleteActivity(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String idStr = request.getParameter("id");
        HttpSession session = request.getSession();
        if (idStr != null && !idStr.trim().isEmpty()) {
            int id = Integer.parseInt(idStr);
            try {
                if (!activityService.canAccessActivity(id, AuthContext.getCurrentUser(request))) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN,
                            "Bạn không có quyền xóa hoạt động này.");
                    return;
                }
                boolean success = activityService.deleteActivity(id);
                if (success) {
                    session.setAttribute("successMessage", "Xóa hoạt động thành công!");
                } else {
                    session.setAttribute("errorMessage", "Xóa hoạt động thất bại!");
                }
            } catch (IllegalArgumentException e) {
                session.setAttribute("errorMessage", e.getMessage());
            }
        }
        response.sendRedirect(request.getContextPath() + "/activities");
    }

    private void getStaffsByCompanyJson(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String companyIdStr = request.getParameter("companyId");
        StringBuilder json = new StringBuilder("[");

        if (companyIdStr != null && !companyIdStr.trim().isEmpty()) {
            try {
                int companyId = Integer.parseInt(companyIdStr);
                List<PartnerStaffResponseDTO> staffs = partnerStaffService.getStaffsByCompanyId(companyId);

                staffs = staffs.stream().filter(st -> "ACTIVE".equalsIgnoreCase(st.getStatus())).toList();
                for (int i = 0; i < staffs.size(); i++) {
                    PartnerStaffResponseDTO staff = staffs.get(i);
                    json.append("{")
                            .append("\"id\":").append(staff.getId()).append(",")
                            .append("\"name\":\"").append(escapeJson(staff.getName())).append("\",")
                            .append("\"position\":\"").append(escapeJson(staff.getPosition() != null ? staff.getPosition() : "")).append("\"")
                            .append("}");
                    if (i < staffs.size() - 1) {
                        json.append(",");
                    }
                }
            } catch (NumberFormatException ignored) {}
        }
        json.append("]");

        try (PrintWriter out = response.getWriter()) {
            out.print(json.toString());
            out.flush();
        }
    }

    private void getStaffsByDepartmentJson(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String departmentIdValue = request.getParameter("departmentId");
        int departmentId = parseId(departmentIdValue);
        if (departmentId <= 0) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Đơn vị phụ trách không hợp lệ.");
            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        StringBuilder json = new StringBuilder("[");
        // Staff are filtered through staff_assignment on this department, so it is their department
        Department department = departmentService.getDepartmentById(departmentId);
        String departmentCode = department == null || department.getCode() == null ? "" : department.getCode();
        String departmentName = department == null || department.getName() == null ? "" : department.getName();
        List<Staff> staffs = staffService.getStaffsByDepartmentId(departmentId);
        for (int i = 0; i < staffs.size(); i++) {
            Staff staff = staffs.get(i);
            json.append("{")
                    .append("\"id\":").append(staff.getId()).append(",")
                    .append("\"departmentCode\":\"").append(escapeJson(departmentCode)).append("\",")
                    .append("\"departmentName\":\"").append(escapeJson(departmentName)).append("\",")
                    .append("\"code\":\"").append(escapeJson(staff.getCode())).append("\",")
                    .append("\"name\":\"").append(escapeJson(staff.getName())).append("\"")
                    .append("}");
            if (i < staffs.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");

        try (PrintWriter out = response.getWriter()) {
            out.print(json);
            out.flush();
        }
    }

    // =========================================================================
    // POST ACTION HANDLERS
    // =========================================================================

    private void createActivity(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ExtracurricularActivityRequestDTO dto = buildRequestDTO(request);
        HttpSession session = request.getSession();

        try {
            boolean success = activityService.createActivity(dto);
            if (success) {
                session.setAttribute("successMessage", "Tạo mới hoạt động thành công!");
                response.sendRedirect(request.getContextPath() + "/activities");
            } else {
                request.setAttribute("errorMessage", "Không thể tạo hoạt động. Vui lòng thử lại.");
                request.setAttribute("formData", dto);
                showCreateForm(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("formData", dto);
            showCreateForm(request, response);
        }
    }

    private void updateActivity(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ExtracurricularActivityRequestDTO dto = buildRequestDTO(request);
        dto.setId(request.getParameter("id"));
        HttpSession session = request.getSession();

        int activityId = parseId(dto.getId());
        if (!activityService.canAccessActivity(activityId, AuthContext.getCurrentUser(request))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Bạn không có quyền sửa hoạt động này.");
            return;
        }

        try {
            boolean success = activityService.updateActivity(dto);
            if (success) {
                session.setAttribute("successMessage", "Cập nhật hoạt động thành công! Hoạt động đã duyệt mà có thay đổi nội dung sẽ chuyển về Bản nháp và cần Gửi duyệt lại.");
                response.sendRedirect(request.getContextPath() + "/activities");
            } else {
                request.setAttribute("errorMessage", "Cập nhật không thành công. Vui lòng kiểm tra lại.");
                request.setAttribute("formData", dto);
                showEditForm(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("formData", dto);
            showEditForm(request, response);
        }
    }

    private void assignStaff(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        CurrentUser currentUser = AuthContext.getCurrentUser(request);
        HttpSession session = request.getSession();
        int activityId = 0;
        try {
            activityId = Integer.parseInt(request.getParameter("activityId"));
            if (!activityStaffService.canManageStaffAssignments(activityId, currentUser)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Bạn không có quyền thêm nhân sự vào hoạt động này.");
                return;
            }
            if (!activityStaffService.canAssignStaff(activityId, currentUser)) {
                session.setAttribute("errorMessage",
                        "Không thể thêm nhân sự vì chỉ được phân công trước thời điểm hoạt động bắt đầu.");
                response.sendRedirect(request.getContextPath()
                        + "/activities?action=detail&id=" + activityId + "#tab-staff");
                return;
            }
            List<Integer> staffIds = parseSelectedIds(request.getParameterValues("staffIds"));
            int assigned = 0;
            String firstError = null;
            for (int staffId : staffIds) {
                try {
                    if (activityStaffService.assignStaff(activityId, staffId, currentUser)) {
                        assigned++;
                    } else if (firstError == null) {
                        firstError = "Không thể phân công nhân sự có mã ID " + staffId + ".";
                    }
                } catch (Exception e) {
                    if (firstError == null) {
                        firstError = e.getMessage();
                    }
                }
            }
            setAssignmentResult(session, assigned, staffIds.size(), "nhân sự", "phân công", firstError);
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Dữ liệu phân công không hợp lệ.");
        } catch (IllegalArgumentException e) {
            session.setAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            session.setAttribute("errorMessage", "Đã xảy ra lỗi khi phân công nhân sự: " + e.getMessage());
        }

        if (activityId > 0) {
            response.sendRedirect(request.getContextPath() + "/activities?action=detail&id=" + activityId + "#tab-staff");
        } else {
            response.sendRedirect(request.getContextPath() + "/activities");
        }
    }

    private void addDepartment(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        CurrentUser currentUser = AuthContext.getCurrentUser(request);
        HttpSession session = request.getSession();
        int activityId = 0;
        try {
            activityId = Integer.parseInt(request.getParameter("activityId"));
            if (!activityStaffService.canManageStaffAssignments(activityId, currentUser)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Bạn không có quyền thêm đơn vị đồng phụ trách vào hoạt động này.");
                return;
            }
            if (!activityStaffService.canAssignStaff(activityId, currentUser)) {
                session.setAttribute("errorMessage",
                        "Không thể thêm đơn vị đồng phụ trách vì chỉ được thay đổi trước thời điểm hoạt động bắt đầu.");
                redirectToStaffTab(request, response, activityId);
                return;
            }
            List<Integer> departmentIds = parseSelectedIds(request.getParameterValues("departmentIds"));
            int added = 0;
            String firstError = null;
            for (int departmentId : departmentIds) {
                try {
                    if (activityStaffService.addDepartment(activityId, departmentId, currentUser)) {
                        added++;
                    } else if (firstError == null) {
                        firstError = "Không thể thêm đơn vị có mã ID " + departmentId + ".";
                    }
                } catch (Exception e) {
                    if (firstError == null) {
                        firstError = e.getMessage();
                    }
                }
            }
            setAssignmentResult(session, added, departmentIds.size(), "đơn vị", "thêm", firstError);
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Dữ liệu đơn vị đồng phụ trách không hợp lệ.");
        } catch (Exception e) {
            session.setAttribute("errorMessage", "Đã xảy ra lỗi khi thêm đơn vị đồng phụ trách: " + e.getMessage());
        }
        redirectToStaffTab(request, response, activityId);
    }

    private void removeDepartment(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        CurrentUser currentUser = AuthContext.getCurrentUser(request);
        HttpSession session = request.getSession();
        int activityId = 0;
        try {
            activityId = Integer.parseInt(request.getParameter("activityId"));
            if (!activityStaffService.canManageStaffAssignments(activityId, currentUser)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Bạn không có quyền gỡ đơn vị đồng phụ trách khỏi hoạt động này.");
                return;
            }
            if (!activityStaffService.canAssignStaff(activityId, currentUser)) {
                session.setAttribute("errorMessage",
                        "Không thể gỡ đơn vị đồng phụ trách vì chỉ được thay đổi trước thời điểm hoạt động bắt đầu.");
                redirectToStaffTab(request, response, activityId);
                return;
            }
            int departmentId = Integer.parseInt(request.getParameter("departmentId"));
            if (activityStaffService.removeDepartment(activityId, departmentId, currentUser)) {
                session.setAttribute("successMessage",
                        "Đã gỡ đơn vị đồng phụ trách. Nhân sự chỉ thuộc đơn vị này cũng đã được gỡ khỏi hoạt động.");
            } else {
                session.setAttribute("errorMessage", "Không thể gỡ đơn vị đồng phụ trách.");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Dữ liệu không hợp lệ.");
        } catch (Exception e) {
            session.setAttribute("errorMessage", "Đã xảy ra lỗi khi gỡ đơn vị đồng phụ trách: " + e.getMessage());
        }
        redirectToStaffTab(request, response, activityId);
    }

    private void redirectToStaffTab(HttpServletRequest request, HttpServletResponse response, int activityId)
            throws IOException {
        if (activityId > 0) {
            response.sendRedirect(request.getContextPath() + "/activities?action=detail&id=" + activityId + "#tab-staff");
        } else {
            response.sendRedirect(request.getContextPath() + "/activities");
        }
    }

    private void unassignStaff(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        CurrentUser currentUser = AuthContext.getCurrentUser(request);
        HttpSession session = request.getSession();
        int activityId = 0;
        try {
            activityId = Integer.parseInt(request.getParameter("activityId"));
            if (!activityStaffService.canManageStaffAssignments(activityId, currentUser)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Bạn không có quyền hủy phân công nhân sự khỏi hoạt động này.");
                return;
            }
            if (!activityStaffService.canAssignStaff(activityId, currentUser)) {
                session.setAttribute("errorMessage",
                        "Không thể hủy phân công nhân sự vì chỉ được thay đổi trước thời điểm hoạt động bắt đầu.");
                response.sendRedirect(request.getContextPath()
                        + "/activities?action=detail&id=" + activityId + "#tab-staff");
                return;
            }
            int staffId = Integer.parseInt(request.getParameter("staffId"));
            boolean success = activityStaffService.removeStaff(activityId, staffId, currentUser);
            if (success) {
                session.setAttribute("successMessage", "Đã hủy phân công nhân sự khỏi hoạt động.");
            } else {
                session.setAttribute("errorMessage", "Không thể hủy phân công nhân sự.");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Dữ liệu không hợp lệ.");
        } catch (Exception e) {
            session.setAttribute("errorMessage", "Đã xảy ra lỗi khi hủy phân công: " + e.getMessage());
        }

        if (activityId > 0) {
            response.sendRedirect(request.getContextPath() + "/activities?action=detail&id=" + activityId + "#tab-staff");
        } else {
            response.sendRedirect(request.getContextPath() + "/activities");
        }
    }

    private void assignPartnerStaff(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        handlePartnerStaffChange(request, response, true);
    }

    private void handleInvitation(HttpServletRequest request, HttpServletResponse response, String operation)
            throws IOException {
        CurrentUser currentUser = AuthContext.getCurrentUser(request);
        HttpSession session = request.getSession();
        int activityId = parseId(request.getParameter("activityId"));
        try {
            switch (operation) {
                case "invite":
                    invitationService.invite(activityId, request.getParameter("partnerCompanyId"),
                            request.getParameter("partnerStaffId"), request.getParameter("message"), currentUser);
                    session.setAttribute("successMessage", "Đã gửi lời mời đối tác tham gia hoạt động.");
                    break;
                case "respond":
                    boolean accept = "accept".equals(request.getParameter("decision"));
                    invitationService.respond(parseId(request.getParameter("invitationId")), accept, currentUser);
                    session.setAttribute("successMessage",
                            accept ? "Đã chấp nhận lời mời." : "Đã từ chối lời mời.");
                    break;
                case "revoke":
                    invitationService.revoke(parseId(request.getParameter("invitationId")), currentUser);
                    session.setAttribute("successMessage", "Đã hủy phản hồi lời mời. Lời mời quay lại trạng thái chờ phản hồi.");
                    break;
                case "terminate":
                    invitationService.requestTermination(activityId,
                            parseId(request.getParameter("partnerCompanyLinkId")),
                            request.getParameter("message"), currentUser);
                    session.setAttribute("successMessage",
                            "Đã gửi lời mời ngừng hợp tác. Hợp tác chỉ kết thúc sau khi đối tác chấp nhận.");
                    break;
                default:
                    invitationService.cancel(parseId(request.getParameter("invitationId")), currentUser);
                    session.setAttribute("successMessage", "Đã hủy lời mời.");
                    break;
            }
        } catch (SecurityException | IllegalArgumentException e) {
            session.setAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            session.setAttribute("errorMessage", "Không thể xử lý lời mời: " + e.getMessage());
        }
        if ("partner-invitations".equals(request.getParameter("returnTo"))) {
            response.sendRedirect(request.getContextPath() + "/partner-invitations");
        } else if (activityId > 0) {
            response.sendRedirect(request.getContextPath()
                    + "/activities?action=detail&id=" + activityId + "#tab-partner");
        } else {
            response.sendRedirect(request.getContextPath() + "/activities");
        }
    }
    private void unassignPartnerStaff(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        handlePartnerStaffChange(request, response, false);
    }

    private void handlePartnerStaffChange(HttpServletRequest request, HttpServletResponse response, boolean assign)
            throws IOException {
        CurrentUser currentUser = AuthContext.getCurrentUser(request);
        HttpSession session = request.getSession();
        int activityId = 0;
        try {
            activityId = Integer.parseInt(request.getParameter("activityId"));
            if (!activityPartnerStaffService.canManagePartnerStaff(activityId, currentUser)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Bạn không có quyền quản lý nhân sự đối tác của hoạt động này.");
                return;
            }
            if (!activityPartnerStaffService.canAssignPartnerStaff(activityId, currentUser)) {
                session.setAttribute("errorMessage", assign
                        ? "Không thể phân công nhân sự đối tác vì chỉ được thay đổi trước thời điểm hoạt động bắt đầu."
                        : "Không thể gỡ nhân sự đối tác vì chỉ được thay đổi trước thời điểm hoạt động bắt đầu.");
                if ("partner-invitations".equals(request.getParameter("returnTo"))) {
                    response.sendRedirect(request.getContextPath() + "/partner-invitations");
                } else {
                    response.sendRedirect(request.getContextPath()
                            + "/activities?action=detail&id=" + activityId + "#tab-partner");
                }
                return;
            }
            String[] selectedPartnerIds = assign
                    ? request.getParameterValues("partnerStaffIds")
                    : new String[] {request.getParameter("partnerStaffId")};
            List<Integer> partnerStaffIds = parseSelectedIds(selectedPartnerIds);
            int completed = 0;
            String firstError = null;
            for (int partnerStaffId : partnerStaffIds) {
                try {
                    boolean success = assign
                            ? activityPartnerStaffService.assignPartnerStaff(activityId, partnerStaffId, currentUser)
                            : activityPartnerStaffService.removePartnerStaff(activityId, partnerStaffId, currentUser);
                    if (success) {
                        completed++;
                    } else if (firstError == null) {
                        firstError = "Không thể thực hiện thao tác với nhân sự đối tác có mã ID "
                                + partnerStaffId + ".";
                    }
                } catch (Exception e) {
                    if (firstError == null) {
                        firstError = e.getMessage();
                    }
                }
            }
            setAssignmentResult(session, completed, partnerStaffIds.size(), "nhân sự đối tác",
                    assign ? "phân công" : "gỡ phân công", firstError);
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Dữ liệu không hợp lệ.");
        } catch (IllegalArgumentException e) {
            session.setAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            session.setAttribute("errorMessage", "Đã xảy ra lỗi: " + e.getMessage());
        }

        if ("partner-invitations".equals(request.getParameter("returnTo"))) {
            response.sendRedirect(request.getContextPath() + "/partner-invitations");
        } else if (activityId > 0) {
            response.sendRedirect(request.getContextPath()
                    + "/activities?action=detail&id=" + activityId + "#tab-partner");
        } else {
            response.sendRedirect(request.getContextPath() + "/activities");
        }
    }

    private List<Integer> parseSelectedIds(String[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("Vui lòng chọn ít nhất một nhân sự.");
        }
        Set<Integer> ids = new LinkedHashSet<>();
        for (String value : values) {
            try {
                int id = Integer.parseInt(value);
                if (id <= 0) {
                    throw new NumberFormatException();
                }
                ids.add(id);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Dữ liệu nhân sự được chọn không hợp lệ.");
            }
        }
        return new ArrayList<>(ids);
    }

    private void setAssignmentResult(HttpSession session, int completed, int requested,
            String staffLabel, String actionLabel, String firstError) {
        if (completed == requested) {
            session.setAttribute("successMessage",
                    "Đã " + actionLabel + " " + completed + " " + staffLabel + ".");
        } else if (completed > 0) {
            session.setAttribute("errorMessage",
                    "Đã " + actionLabel + " " + completed + "/" + requested + " " + staffLabel
                            + ". Một số lựa chọn không thành công: " + firstError);
        } else {
            session.setAttribute("errorMessage",
                    "Không thể " + actionLabel + " " + staffLabel + ": "
                            + (firstError == null ? "Vui lòng chọn nhân sự." : firstError));
        }
    }

    // =========================================================================
    // HELPER METHODS
    // =========================================================================

    private void loadDropdownData(HttpServletRequest request, int departmentId) {
        request.setAttribute("semesters", semesterService.getAllSemesters());
        request.setAttribute("activityTypes", activityTypeService.getActiveActivityTypes());
        request.setAttribute("departments", departmentService.getAllDepartments());
        request.setAttribute("staffs", staffService.getStaffsByDepartmentId(departmentId));
    }

    private int parseId(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void setActivityManagementPermission(HttpServletRequest request) {
        CurrentUser currentUser = AuthContext.getCurrentUser(request);
        request.setAttribute("canManageActivities",
                AccessPolicy.canManageActivities(currentUser));
        request.setAttribute("canApproveActivities",
                AccessPolicy.canReviewActivities(currentUser));
    }

    private ExtracurricularActivityRequestDTO buildRequestDTO(HttpServletRequest request) {
        ExtracurricularActivityRequestDTO dto = new ExtracurricularActivityRequestDTO();
        dto.setCode(request.getParameter("code"));
        dto.setName(request.getParameter("name"));
        dto.setSemesterId(request.getParameter("semesterId"));
        dto.setActivityTypeId(request.getParameter("activityTypeId"));
        dto.setResponsibleDepartmentId(request.getParameter("responsibleDepartmentId"));
        dto.setResponsibleStaffId(request.getParameter("responsibleStaffId"));
        dto.setNature(request.getParameter("nature"));
        dto.setSummaryRequired(request.getParameter("summaryRequired") != null ? "true" : "false");
        dto.setMaxParticipants(request.getParameter("maxParticipants"));
        dto.setAddress(request.getParameter("address"));             // Đổi location -> address
        dto.setStartTime(request.getParameter("startTime"));         // Đổi startDateTime -> startTime
        dto.setEndTime(request.getParameter("endTime"));             // Đổi endDateTime -> endTime
        dto.setDescription(request.getParameter("description"));
        dto.setRegistrationFormRequired(request.getParameter("registrationFormRequired") != null ? "true" : "false");
        return dto;
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}