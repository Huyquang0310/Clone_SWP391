<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="activeNav" value="partner-companies" scope="request"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Chi tiết công ty đối tác | SWP391-G1</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/partner-company.css">
</head>
<body>
<div class="activity-app-shell">
    <jsp:include page="/WEB-INF/views/layout/app-navigation.jsp"/>
    <div class="activity-app-layout">
        <jsp:include page="/WEB-INF/views/layout/app-sidebar.jsp"/>
        <div class="activity-app-main">
<main class="page activity-list-page activity-form-page">
    <header class="activity-hero">
        <div class="activity-hero-copy">
            <span class="eyebrow">SWP391-G1 <span aria-hidden="true">/</span> QUẢN LÝ ĐỐI TÁC</span>
            <h1>Chi tiết công ty đối tác</h1>
            <p>Thông tin liên hệ và trạng thái của công ty.</p>
        </div>
        <a class="button activity-create-button" href="${pageContext.request.contextPath}/partner-companies">Danh sách công ty</a>
    </header>
    <section class="card">
        <dl class="detail-grid">
            <div class="detail-item"><dt>Mã công ty</dt><dd><c:out value="${partnerCompany.code}" default="—"/></dd></div>
            <div class="detail-item"><dt>Tên công ty</dt><dd><c:out value="${partnerCompany.name}" default="—"/></dd></div>
            <div class="detail-item"><dt>Email</dt><dd><c:out value="${partnerCompany.email}" default="—"/></dd></div>
            <div class="detail-item"><dt>Số điện thoại</dt><dd><c:out value="${partnerCompany.phoneNumber}" default="—"/></dd></div>
            <div class="detail-item"><dt>Website</dt><dd><c:out value="${partnerCompany.website}" default="—"/></dd></div>
            <div class="detail-item"><dt>Trạng thái</dt><dd>
                <c:choose>
                    <c:when test="${partnerCompany.status == 'ACTIVE'}">Đang hoạt động</c:when>
                    <c:otherwise>Không hoạt động</c:otherwise>
                </c:choose>
            </dd></div>
            <div class="detail-item field-wide"><dt>Địa chỉ</dt><dd><c:out value="${partnerCompany.address}" default="—"/></dd></div>
            <div class="detail-item field-wide"><dt>Mô tả</dt><dd><c:out value="${partnerCompany.description}" default="—"/></dd></div>
        </dl>
        <div class="detail-actions">
            <a class="button button-primary" href="${pageContext.request.contextPath}/partner-companies?action=edit&amp;id=${partnerCompany.id}">Sửa thông tin</a>
            <a class="button" href="${pageContext.request.contextPath}/partner-companies">Quay lại danh sách</a>
        </div>
    </section>
</main>
        </div>
    </div>
</div>
</body>
</html>
