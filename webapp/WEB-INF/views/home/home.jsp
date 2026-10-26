<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="activeNav" value="home" scope="request"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/activity.css?v=20261020-11">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>SWP391-G1</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
</head>
<body>
<div class="activity-app-shell">
    <jsp:include page="/WEB-INF/views/layout/app-navigation.jsp"/>
    <div class="activity-app-layout">
        <jsp:include page="/WEB-INF/views/layout/app-sidebar.jsp"/>
        <div class="activity-app-main">
    <main class="page">
        <section class="card">
            <h1>SWP391-G1</h1>
            <p class="subtitle">Hệ thống quản lý hoạt động ngoại khóa</p>
            <p><a class="button button-primary" href="${pageContext.request.contextPath}/activities">Quản lý hoạt động</a></p>
            <c:if test="${canManageActivities}">
                <p><a class="button" href="${pageContext.request.contextPath}/registration-forms">Quản lý form đăng ký</a></p>
            </c:if>
            <p><a class="button" href="${pageContext.request.contextPath}/partner-companies">Quản lý công ty đối tác</a></p>
            <p><a class="button" href="${pageContext.request.contextPath}/partner-staffs">Quản lý nhân viên đối tác</a></p>
            <p><a class="button" href="${pageContext.request.contextPath}/student">Quản lý sinh viên</a></p>
            <p><a class="button" href="${pageContext.request.contextPath}/staff">Quản lý nhân viên</a></p>
            <c:if test="${canManageQuestions}">
                <p><a class="button" href="${pageContext.request.contextPath}/qa-management?filter=pending">Trả lời câu hỏi</a></p>
                <p><a class="button" href="${pageContext.request.contextPath}/qa-management">Quản lý Hỏi &amp; Đáp</a></p>
            </c:if>
            <c:if test="${canUseStudentQuestions}">
                <p><a class="button" href="${pageContext.request.contextPath}/student-qa">Hỏi &amp; Đáp sinh viên</a></p>
            </c:if>
            <%-- UI hint only: AccountServlet still enforces the ADMIN rule server-side. --%>
            <c:if test="${canManageAccounts}">
                <p><a class="button" href="${pageContext.request.contextPath}/accounts">Quản lý tài khoản</a></p>
            </c:if>
            <form method="post" action="${pageContext.request.contextPath}/logout">
                <button class="button" type="submit">Đăng xuất</button>
            </form>
        </section>
    </main>
        </div>
    </div>
</div>
</body>
</html>