<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Lỗi hệ thống</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
</head>
<body>
<main class="page">
    <section class="card">
        <h1>Không thể xử lý yêu cầu</h1>
        <p class="alert alert-error"><c:out value="${errorMessage}" default="Đã xảy ra lỗi không xác định."/></p>
        <a class="button button-primary" href="${pageContext.request.contextPath}/home">Về trang chủ</a>
    </section>
</main>
</body>
</html>
