<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="st" uri="http://swp391.g1/status" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="activeNav" value="accounts" scope="request"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Chi tiết tài khoản | SWP391-G1</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/account.css">
</head>
<body>
<div class="activity-app-shell">
    <jsp:include page="/WEB-INF/views/layout/app-navigation.jsp"/>
    <div class="activity-app-layout">
        <jsp:include page="/WEB-INF/views/layout/app-sidebar.jsp"/>
        <div class="activity-app-main">
<main class="page activity-list-page activity-form-page account-page">
    <header class="activity-hero">
        <div class="activity-hero-copy">
            <span class="eyebrow">SWP391-G1 <span aria-hidden="true">/</span> QUẢN TRỊ HỆ THỐNG</span>
            <h1>Chi tiết tài khoản</h1>
            <p><span class="activity-code"><c:out value="${account.username}"/></span></p>
        </div>
        <a class="button activity-create-button" href="${pageContext.request.contextPath}/accounts">
            <svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#arrow-left"/></svg>
            Danh sách tài khoản
        </a>
    </header>

    <c:if test="${not empty param.msg}">
        <div class="alert alert-success activity-alert" role="status">
            <c:choose>
                <c:when test="${param.msg == 'created'}">Tạo tài khoản thành công.</c:when>
                <c:when test="${param.msg == 'updated'}">Cập nhật tài khoản thành công.</c:when>
                <c:when test="${param.msg == 'statusChanged'}">Cập nhật trạng thái tài khoản thành công.</c:when>
                <c:otherwise>Thao tác đã hoàn tất.</c:otherwise>
            </c:choose>
        </div>
    </c:if>
    <c:if test="${param.error == 'invalidStatus'}">
        <div class="alert alert-error activity-alert" role="alert">Trạng thái không hợp lệ. Các giá trị cho phép: ACTIVE, INACTIVE, BLOCKED.</div>
    </c:if>

    <section class="card activity-form-card activity-detail-card">
        <div class="activity-table-heading">
            <div>
                <h2>Thông tin tài khoản</h2>
                <p>Thông tin định danh và quyền truy cập hệ thống.</p>
            </div>
            <a class="button button-primary" href="${pageContext.request.contextPath}/accounts/${account.id}/edit">Chỉnh sửa</a>
        </div>
        <div class="activity-detail-content">
            <section class="activity-detail-section">
                <h3>Thông tin truy cập</h3>
                <dl class="detail-grid activity-detail-grid">
                    <div class="detail-item"><dt>ID tài khoản</dt><dd><c:out value="${account.id}"/></dd></div>
                    <div class="detail-item"><dt>Tên đăng nhập</dt><dd><c:out value="${account.username}"/></dd></div>
                    <div class="detail-item"><dt>Loại tài khoản</dt><dd><c:out value="${account.type}"/></dd></div>
                    <div class="detail-item"><dt>Vai trò</dt><dd><c:out value="${account.role}"/></dd></div>
                    <div class="detail-item"><dt>Trạng thái</dt><dd><span class="badge account-status account-status-${fn:escapeXml(account.status)}">${st:label(account.status)}</span></dd></div>
                </dl>
            </section>

            <section class="activity-detail-section">
                <h3>Hồ sơ liên kết</h3>
                <c:choose>
                    <c:when test="${empty profiles}">
                        <p class="account-no-profile">Tài khoản này chưa được liên kết với hồ sơ nào.</p>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${profiles}" var="p">
                            <dl class="detail-grid activity-detail-grid account-profile-grid">
                                <div class="detail-item"><dt>Loại hồ sơ</dt><dd><c:out value="${p.profileType}"/></dd></div>
                                <div class="detail-item"><dt>Mã hồ sơ</dt><dd><c:out value="${p.code}" default="—"/></dd></div>
                                <div class="detail-item"><dt>Họ tên</dt><dd><c:out value="${p.name}" default="—"/></dd></div>
                                <div class="detail-item"><dt>Trạng thái hồ sơ</dt><dd><c:out value="${p.status}" default="—"/></dd></div>
                                <c:if test="${not empty p.email}">
                                    <div class="detail-item"><dt>Email</dt><dd><c:out value="${p.email}"/></dd></div>
                                </c:if>
                                <c:if test="${not empty p.position}">
                                    <div class="detail-item"><dt>Chức vụ</dt><dd><c:out value="${p.position}"/></dd></div>
                                </c:if>
                            </dl>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </section>

            <section class="activity-detail-section">
                <h3>Thay đổi trạng thái</h3>
                <form class="account-status-form" method="post" action="${pageContext.request.contextPath}/accounts/${account.id}/status">
                    <div class="field">
                        <label for="status">Trạng thái tài khoản</label>
                        <select id="status" name="status">
                            <c:forEach items="${accountStatuses}" var="s">
                                <option value="${fn:escapeXml(s)}" <c:if test="${s == account.status}">selected</c:if>><c:out value="${s}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                    <button class="button button-primary" type="submit">Cập nhật trạng thái</button>
                </form>
            </section>
        </div>
        <div class="detail-actions activity-detail-actions">
            <a class="button" href="${pageContext.request.contextPath}/accounts">Quay lại danh sách</a>
        </div>
    </section>
</main>
        </div>
    </div>
</div>
</body>
</html>
