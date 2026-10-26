<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Không thể xử lý yêu cầu</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
</head>
<body>
<main class="page">
    <section class="card">
        <h1>Không thể xử lý yêu cầu</h1>
        <p class="alert alert-error">
            <c:choose>
                <c:when test="${requestScope['jakarta.servlet.error.status_code'] == 400}">
                    <c:out value="${requestScope['jakarta.servlet.error.message']}"
                           default="Yêu cầu không hợp lệ. Vui lòng kiểm tra lại thông tin."/>
                </c:when>
                <c:when test="${requestScope['jakarta.servlet.error.status_code'] == 403}">
                    <c:out value="${requestScope['jakarta.servlet.error.message']}"
                           default="Tài khoản của bạn không được phép thực hiện thao tác này."/>
                </c:when>
                <c:when test="${requestScope['jakarta.servlet.error.status_code'] == 404}">
                    <c:out value="${requestScope['jakarta.servlet.error.message']}"
                           default="Không tìm thấy nội dung bạn yêu cầu."/>
                </c:when>
                <c:otherwise>
                    Máy chủ gặp lỗi khi xử lý yêu cầu. Vui lòng thử lại sau.
                </c:otherwise>
            </c:choose>
        </p>
        <a class="button button-primary" href="${pageContext.request.contextPath}/activities">Về danh sách hoạt động</a>
    </section>
</main>
</body>
</html>
