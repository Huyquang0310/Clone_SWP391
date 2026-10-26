<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="activeNav" value="partner-staff" scope="request"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Chi tiết nhân viên đối tác | SWP391-G1</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/partner-staff.css">
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
            <h1>Chi tiết nhân viên đối tác</h1>
            <p><span class="activity-code"><c:out value="${partnerStaff.code}"/></span> <c:out value="${partnerStaff.name}"/></p>
        </div>
        <a class="button activity-create-button" href="${pageContext.request.contextPath}/partner-staffs">
            <svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#arrow-left"/></svg>
            Danh sách nhân viên
        </a>
    </header>
    <section class="card activity-form-card activity-detail-card">
        <div class="activity-table-heading">
            <div>
                <h2>Thông tin nhân viên</h2>
                <p>Thông tin cá nhân và công ty đối tác liên quan.</p>
            </div>
            <a class="button button-primary" href="${pageContext.request.contextPath}/partner-staffs?action=edit&amp;id=${partnerStaff.id}">Chỉnh sửa</a>
        </div>
        <div class="activity-detail-content">
            <section class="activity-detail-section">
                <h3>Thông tin cơ bản</h3>
                <dl class="detail-grid activity-detail-grid">
                    <div class="detail-item"><dt>Mã nhân viên</dt><dd><c:out value="${partnerStaff.code}" default="—"/></dd></div>
                    <div class="detail-item"><dt>Họ và tên</dt><dd><c:out value="${partnerStaff.name}" default="—"/></dd></div>
                    <div class="detail-item"><dt>Ngày sinh</dt><dd><c:out value="${partnerStaff.dob}" default="Chưa cập nhật"/></dd></div>
                    <div class="detail-item"><dt>Giới tính</dt><dd>
                        <c:choose>
                            <c:when test="${partnerStaff.gender == null}">Chưa cập nhật</c:when>
                            <c:when test="${partnerStaff.gender}">Nam</c:when>
                            <c:otherwise>Nữ</c:otherwise>
                        </c:choose>
                    </dd></div>
                    <div class="detail-item"><dt>Chức vụ</dt><dd><c:out value="${partnerStaff.position}" default="Chưa cập nhật"/></dd></div>
                    <div class="detail-item"><dt>Trạng thái</dt><dd>
                        <span class="badge ${partnerStaff.status == 'ACTIVE' ? 'partner-company-active' : 'partner-company-inactive'}">
                            <c:choose>
                                <c:when test="${partnerStaff.status == 'ACTIVE'}">Đang hoạt động</c:when>
                                <c:otherwise>Không hoạt động</c:otherwise>
                            </c:choose>
                        </span>
                    </dd></div>
                </dl>
            </section>
            <section class="activity-detail-section">
                <h3>Công ty chủ quản</h3>
                <dl class="detail-grid activity-detail-grid">
                    <div class="detail-item"><dt>Mã công ty</dt><dd><c:out value="${partnerStaff.companyCode}" default="—"/></dd></div>
                    <div class="detail-item"><dt>Tên công ty</dt><dd><c:out value="${partnerStaff.companyName}" default="—"/></dd></div>
                </dl>
            </section>
        </div>
        <div class="detail-actions activity-detail-actions">
            <a class="button" href="${pageContext.request.contextPath}/partner-staffs">Đóng</a>
        </div>
    </section>
</main>
        </div>
    </div>
</div>
</body>
</html>
