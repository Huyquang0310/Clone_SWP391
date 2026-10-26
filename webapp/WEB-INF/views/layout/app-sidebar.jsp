<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.swp391.g1.dto.CurrentUser" %>
<%@ page import="com.swp391.g1.util.AuthContext" %>
<%
    CurrentUser navUser = AuthContext.getCurrentUser(request);
    String navType = navUser == null ? "" : navUser.getType();
    boolean navAdmin = "ADMIN".equals(navType);
    boolean navStaff = "STAFF".equals(navType);
    boolean navStudent = "STUDENT".equals(navType);
    Object navActiveAttr = request.getAttribute("activeNav");
    String navActive = navActiveAttr == null ? "" : navActiveAttr.toString();
    String ctx = request.getContextPath();
%>
<aside class="activity-side-nav" aria-label="Điều hướng hệ thống">
    <nav class="activity-nav-menu">
        <a class="<%= "home".equals(navActive) ? "active" : "" %>" href="<%= ctx %>/home"
           <%= "home".equals(navActive) ? "aria-current=\"page\"" : "" %>>
            <span class="activity-nav-icon" aria-hidden="true">⌂</span>Trang chủ
        </a>
        <span class="activity-nav-group">Quản lý</span>
        <a class="<%= "activities".equals(navActive) ? "active" : "" %>" href="<%= ctx %>/activities"
           <%= "activities".equals(navActive) ? "aria-current=\"page\"" : "" %>>
            <span class="activity-nav-icon" aria-hidden="true">◷</span>Hoạt động ngoại khóa
        </a>
        <% if (navAdmin || navStaff) { %>
        <a class="<%= "registration-forms".equals(navActive) ? "active" : "" %>" href="<%= ctx %>/registration-forms"
           <%= "registration-forms".equals(navActive) ? "aria-current=\"page\"" : "" %>>
            <span class="activity-nav-icon" aria-hidden="true">✓</span>Đăng ký tham gia
        </a>
        <% } %>
        <a class="<%= "partner-invitations".equals(navActive) ? "active" : "" %>" href="<%= ctx %>/partner-invitations"
                <%= "partner-invitations".equals(navActive) ? "aria-current=\"page\"" : "" %>>
            <span class="activity-nav-icon" aria-hidden="true">✉</span>Lời mời hợp tác
        </a>
        <a class="<%= "students".equals(navActive) ? "active" : "" %>" href="<%= ctx %>/student"
           <%= "students".equals(navActive) ? "aria-current=\"page\"" : "" %>>
            <span class="activity-nav-icon" aria-hidden="true">♙</span>Sinh viên
        </a>
        <a class="<%= "staff".equals(navActive) ? "active" : "" %>" href="<%= ctx %>/staff"
           <%= "staff".equals(navActive) ? "aria-current=\"page\"" : "" %>>
            <span class="activity-nav-icon" aria-hidden="true">♧</span>Nhân viên
        </a>
        <a class="<%= "partner-companies".equals(navActive) ? "active" : "" %>" href="<%= ctx %>/partner-companies"
           <%= "partner-companies".equals(navActive) ? "aria-current=\"page\"" : "" %>>
            <span class="activity-nav-icon" aria-hidden="true">▦</span>Công ty đối tác
        </a>
        <a class="<%= "partner-staff".equals(navActive) ? "active" : "" %>" href="<%= ctx %>/partner-staffs"
           <%= "partner-staff".equals(navActive) ? "aria-current=\"page\"" : "" %>>
            <span class="activity-nav-icon" aria-hidden="true">♧</span>Nhân viên đối tác
        </a>
        <% if (navAdmin) { %>
        <a class="<%= "accounts".equals(navActive) ? "active" : "" %>" href="<%= ctx %>/accounts"
           <%= "accounts".equals(navActive) ? "aria-current=\"page\"" : "" %>>
            <span class="activity-nav-icon" aria-hidden="true">⚙</span>Tài khoản
        </a>
        <% } %>
        <% if (navAdmin || navStaff) { %>
        <span class="activity-nav-group">Hỗ trợ</span>
        <a href="<%= ctx %>/qa-management">
            <span class="activity-nav-icon" aria-hidden="true">?</span>Quản lý Hỏi &amp; Đáp
        </a>
        <a href="<%= ctx %>/organizer-inbox">
            <span class="activity-nav-icon" aria-hidden="true">✉</span>Hộp thư hỗ trợ
        </a>
        <% } %>
        <% if (navStudent) { %>
        <span class="activity-nav-group">Hỗ trợ sinh viên</span>
        <a href="<%= ctx %>/student-qa">
            <span class="activity-nav-icon" aria-hidden="true">?</span>Hỏi &amp; Đáp
        </a>
        <a href="<%= ctx %>/student-inbox">
            <span class="activity-nav-icon" aria-hidden="true">✉</span>Hộp thư của tôi
        </a>
        <% } %>
    </nav>
</aside>
