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
    <title>Quản lý tài khoản | SWP391-G1</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/account.css?v=20261020-4">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/action-menu.css?v=3">
</head>
<body>
<div class="activity-app-shell">
    <jsp:include page="/WEB-INF/views/layout/app-navigation.jsp"/>
    <div class="activity-app-layout">
        <jsp:include page="/WEB-INF/views/layout/app-sidebar.jsp"/>
        <div class="activity-app-main">
<main class="page activity-list-page account-page">
    <header class="activity-hero">
        <div class="activity-hero-copy">
            <span class="eyebrow">SWP391-G1 <span aria-hidden="true">/</span> QUẢN TRỊ HỆ THỐNG</span>
            <h1>Quản lý tài khoản</h1>
            <p>Theo dõi tài khoản, phân quyền và trạng thái truy cập hệ thống.</p>
        </div>
        <div class="account-hero-actions">
            <a class="button account-home-button" href="${pageContext.request.contextPath}/home">Trang chủ</a>
        </div>
    </header>

    <c:if test="${not empty generalError}">
        <div class="alert alert-error activity-alert" role="alert"><c:out value="${generalError}"/></div>
    </c:if>
    <c:if test="${not empty param.error}">
        <div class="alert alert-error activity-alert" role="alert">
            <c:choose>
                <c:when test="${param.error == 'notFound'}">Không tìm thấy tài khoản.</c:when>
                <c:when test="${param.error == 'invalidStatus'}">Trạng thái không hợp lệ. Các giá trị cho phép: ACTIVE, INACTIVE, BLOCKED.</c:when>
                <c:otherwise>Không thể thực hiện thao tác. Vui lòng thử lại.</c:otherwise>
            </c:choose>
        </div>
    </c:if>
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

    <section class="card account-filter-card">
        <div class="activity-table-heading">
            <div>
                <h2>Bộ lọc tài khoản</h2>
                <p>Tìm kiếm theo tên đăng nhập, loại, vai trò hoặc trạng thái.</p>
            </div>
        </div>
        <form class="account-filters" method="get" action="${pageContext.request.contextPath}/accounts">
            <div class="field">
                <label for="search">Tìm kiếm</label>
                <input id="search" type="search" name="search" value="<c:out value='${search}'/>" placeholder="Tên đăng nhập">
            </div>
            <div class="field">
                <label for="type">Loại tài khoản</label>
                <select id="type" name="type">
                    <option value="">Tất cả</option>
                    <c:forEach items="${accountTypes}" var="t">
                        <option value="${fn:escapeXml(t)}" <c:if test="${t == filterType}">selected</c:if>><c:out value="${t}"/></option>
                    </c:forEach>
                </select>
            </div>
            <div class="field">
                <label for="role">Vai trò</label>
                <select id="role" name="role">
                    <option value="">Tất cả</option>
                    <c:forEach items="${accountRoles}" var="r">
                        <option value="${fn:escapeXml(r)}" <c:if test="${r == filterRole}">selected</c:if>><c:out value="${r}"/></option>
                    </c:forEach>
                </select>
            </div>
            <div class="field">
                <label for="status">Trạng thái</label>
                <select id="status" name="status">
                    <option value="">Tất cả</option>
                    <c:forEach items="${accountStatuses}" var="s">
                        <option value="${fn:escapeXml(s)}" <c:if test="${s == filterStatus}">selected</c:if>><c:out value="${s}"/></option>
                    </c:forEach>
                </select>
            </div>
            <div class="account-filter-actions">
                <button class="button button-primary" type="submit">Lọc danh sách</button>
                <a class="button" href="${pageContext.request.contextPath}/accounts">Đặt lại</a>
            </div>
        </form>
    </section>

    <section class="card activity-table-card account-table-card" aria-labelledby="account-table-title">
        <div class="activity-table-heading">
            <div>
                <h2 id="account-table-title">Danh sách tài khoản</h2>
                <p>Các tài khoản phù hợp với tiêu chí đã chọn</p>
            </div>
            <div class="activity-heading-actions">
                <span class="activity-count">
                    <c:choose>
                        <c:when test="${empty accounts}">0 tài khoản</c:when>
                        <c:otherwise><c:out value="${accounts.size()}"/> tài khoản</c:otherwise>
                    </c:choose>
                </span>
                <a class="btn-create-activity" href="${pageContext.request.contextPath}/accounts/create">
                    <svg width="16" height="16" viewBox="0 0 16 16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" aria-hidden="true"><path d="M8 3v10M3 8h10"/></svg>
                    Tạo tài khoản
                </a>
            </div>
        </div>
        <div class="table-wrap activity-table-wrap">
            <table class="account-table">
                <thead>
                <tr>
                    <th scope="col">ID</th>
                    <th scope="col">Tên đăng nhập</th>
                    <th scope="col">Loại</th>
                    <th scope="col">Vai trò</th>
                    <th scope="col">Trạng thái</th>
                    <th scope="col">Thao tác</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${empty accounts}">
                        <tr>
                            <td class="empty-state activity-empty-state" colspan="6">
                                <span class="empty-state-mark" aria-hidden="true">+</span>
                                <strong>Không tìm thấy tài khoản</strong>
                                <span>Thử thay đổi bộ lọc hoặc tạo tài khoản mới.</span>
                                <a class="button button-primary" href="${pageContext.request.contextPath}/accounts/create">Tạo tài khoản</a>
                            </td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${accounts}" var="a">
                            <tr>
                                <td><span class="activity-code"><c:out value="${a.id}"/></span></td>
                                <td><strong class="activity-name"><c:out value="${a.username}"/></strong></td>
                                <td><c:out value="${a.type}"/></td>
                                <td><c:out value="${a.role}"/></td>
                                <td><span class="badge account-status account-status-${fn:escapeXml(a.status)}">${st:label(a.status)}</span></td>
                                <td>
                                    <div class="actions activity-actions">
                                        <a class="activity-action-link" href="${pageContext.request.contextPath}/accounts/${a.id}">
                                            Chi tiết
                                        </a>
                                        <details class="action-menu">
                                            <summary aria-label="Thao tác khác"><span aria-hidden="true">⋯</span></summary>
                                            <div class="action-menu__items">
                                                <a class="action-menu-item action-menu-item--edit" href="${pageContext.request.contextPath}/accounts/${a.id}/edit">
                                                    Chỉnh sửa
                                                </a>
                                            </div>
                                        </details>
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
<script src="${pageContext.request.contextPath}/assets/js/action-menu.js?v=5"></script>
</body>
</html>
