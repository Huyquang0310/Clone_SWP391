<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Đăng nhập | SWP391-G1</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/auth.css">
</head>
<body>
<main class="login-page">
    <a class="login-brand" href="${pageContext.request.contextPath}/login" aria-label="SWP391-G1">
        <span class="login-brand-mark" aria-hidden="true">S</span>
        <span>SWP391-G1</span>
    </a>
    <section class="card login-card">
        <span class="eyebrow login-eyebrow">CỔNG THÔNG TIN</span>
        <h1>Chào mừng trở lại</h1>
        <p class="login-intro">Đăng nhập để tiếp tục sử dụng hệ thống quản lý hoạt động ngoại khóa.</p>

        <c:if test="${param.msg == 'loggedOut'}">
            <div class="alert alert-success" role="status">Bạn đã đăng xuất thành công.</div>
        </c:if>
        <c:if test="${not empty loginError}">
            <div class="alert alert-error" role="alert"><c:out value="${loginError}"/></div>
        </c:if>

        <%-- Keep the username after an error, but never retain the password. --%>
        <form class="login-form" method="post" action="${pageContext.request.contextPath}/login">
            <div class="field">
                <label for="username">Tên đăng nhập</label>
                <input id="username" type="text" name="username" value="${fn:escapeXml(username)}"
                       autocomplete="username" autofocus required>
            </div>
            <div class="field">
                <label for="password">Mật khẩu</label>
                <input id="password" type="password" name="password"
                       autocomplete="current-password" required>
            </div>
            <button class="button button-primary login-submit" type="submit">Đăng nhập</button>
        </form>
        <p class="login-footer">Hệ thống quản lý hoạt động ngoại khóa</p>
    </section>
</main>
</body>
</html>
