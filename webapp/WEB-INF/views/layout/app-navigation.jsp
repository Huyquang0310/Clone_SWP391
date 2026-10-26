<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<header class="activity-app-header">
    <a class="activity-brand" href="${pageContext.request.contextPath}/home">
        <img class="activity-brand-logo" src="${pageContext.request.contextPath}/assets/images/logo-negative.svg" alt="" width="48" height="48">
        <span class="activity-brand-copy">
            <strong>G1 UNIVERSITY / EAMS</strong>
            <span>Hệ thống quản lý hoạt động ngoại khóa</span>
        </span>
    </a>
    <div class="activity-header-account">
        <span class="activity-header-user">${empty sessionScope['com.swp391.g1.auth.CURRENT_USER'] ? 'Tài khoản' : sessionScope['com.swp391.g1.auth.CURRENT_USER'].username}</span>
        <form method="post" action="${pageContext.request.contextPath}/logout">
            <button class="activity-header-action" type="submit">Đăng xuất</button>
        </form>
    </div>
</header>
