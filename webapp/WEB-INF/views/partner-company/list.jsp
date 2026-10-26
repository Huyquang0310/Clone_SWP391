<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="activeNav" value="partner-companies" scope="request"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Công ty đối tác | SWP391-G1</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/partner-company.css">
</head>
<body>
<div class="activity-app-shell">
    <jsp:include page="/WEB-INF/views/layout/app-navigation.jsp"/>
    <div class="activity-app-layout">
        <jsp:include page="/WEB-INF/views/layout/app-sidebar.jsp"/>
        <div class="activity-app-main">
<main class="page activity-list-page">
    <header class="activity-hero">
        <div class="activity-hero-copy">
            <span class="eyebrow">SWP391-G1 <span aria-hidden="true">/</span> QUẢN LÝ ĐỐI TÁC</span>
            <h1>Công ty đối tác</h1>
            <p>Quản lý thông tin liên hệ và trạng thái các đơn vị đồng hành.</p>
        </div>
    </header>

    <c:if test="${not empty sessionScope.successMessage}">
        <div class="alert alert-success activity-alert"><c:out value="${sessionScope.successMessage}"/></div>
        <c:remove var="successMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage}">
        <div class="alert alert-error activity-alert"><c:out value="${sessionScope.errorMessage}"/></div>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>

    <section class="card activity-table-card" aria-labelledby="company-table-title">
        <div class="activity-table-heading">
            <div>
                <h2 id="company-table-title">Danh sách công ty</h2>
                <p>Thông tin các công ty đối tác đã được ghi nhận</p>
            </div>
            <div class="activity-heading-actions">
                <span class="activity-count">
                    <c:choose>
                        <c:when test="${empty partnerCompanies}">0 công ty</c:when>
                        <c:otherwise><c:out value="${partnerCompanies.size()}"/> công ty</c:otherwise>
                    </c:choose>
                </span>
                <a class="btn-create-activity" href="${pageContext.request.contextPath}/partner-companies?action=create">
                    <svg width="16" height="16" viewBox="0 0 16 16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" aria-hidden="true"><path d="M8 3v10M3 8h10"/></svg>
                    Thêm công ty
                </a>
            </div>
        </div>
        <div class="table-wrap activity-table-wrap">
            <table class="partner-company-table">
                <thead>
                <tr>
                    <th scope="col">Mã công ty</th>
                    <th scope="col">Tên công ty</th>
                    <th scope="col">Email</th>
                    <th scope="col">Điện thoại</th>
                    <th scope="col">Trạng thái</th>
                    <th scope="col">Thao tác</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${empty partnerCompanies}">
                        <tr>
                            <td class="empty-state activity-empty-state" colspan="6">
                                <span class="empty-state-mark" aria-hidden="true">+</span>
                                <strong>Chưa có công ty đối tác</strong>
                                <span>Thêm công ty mới để bắt đầu quản lý danh sách.</span>
                                <a class="button button-primary" href="${pageContext.request.contextPath}/partner-companies?action=create">Thêm công ty đầu tiên</a>
                            </td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="company" items="${partnerCompanies}">
                            <tr>
                                <td><span class="activity-code"><c:out value="${company.code}"/></span></td>
                                <td><strong class="activity-name"><c:out value="${company.name}"/></strong></td>
                                <td><c:out value="${company.email}" default="—"/></td>
                                <td><c:out value="${company.phoneNumber}" default="—"/></td>
                                <td>
                                    <span class="badge ${company.status == 'ACTIVE' ? 'partner-company-active' : 'partner-company-inactive'}">
                                        <c:choose>
                                            <c:when test="${company.status == 'ACTIVE'}">Đang hoạt động</c:when>
                                            <c:otherwise>Không hoạt động</c:otherwise>
                                        </c:choose>
                                    </span>
                                </td>
                                <td>
                                    <div class="actions activity-actions">
                                        <a class="activity-action-link" href="${pageContext.request.contextPath}/partner-companies?action=detail&amp;id=${company.id}">Chi tiết</a>
                                        <a class="activity-action-link" href="${pageContext.request.contextPath}/partner-companies?action=edit&amp;id=${company.id}">Sửa</a>
                                        <a class="activity-action-link activity-action-delete"
                                           href="${pageContext.request.contextPath}/partner-companies?action=delete&amp;id=${company.id}"
                                           data-confirm="Bạn có chắc muốn xóa công ty đối tác này?">Xóa</a>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </div>
    </section>
</main>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/assets/js/partner-company.js"></script>
</body>
</html>
