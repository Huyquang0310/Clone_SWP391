<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="activeNav" value="accounts" scope="request"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Cập nhật tài khoản | SWP391-G1</title>
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
            <h1>Cập nhật tài khoản</h1>
            <p>Chỉnh sửa thông tin tài khoản <span class="activity-code">#<c:out value="${account.id}"/></span></p>
        </div>
        <a class="button activity-create-button" href="${pageContext.request.contextPath}/accounts">
            <svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#arrow-left"/></svg>
            Danh sách tài khoản
        </a>
    </header>

    <c:if test="${not empty generalError}">
        <div class="alert alert-error activity-alert" role="alert"><c:out value="${generalError}"/></div>
    </c:if>
    <c:if test="${not empty fieldErrors['id']}">
        <div class="alert alert-error activity-alert" role="alert"><c:out value="${fieldErrors['id']}"/></div>
    </c:if>

    <section class="card activity-form-card">
        <div class="activity-table-heading">
            <div>
                <h2>Thông tin tài khoản</h2>
                <p>Cập nhật thông tin và quyền truy cập của tài khoản.</p>
            </div>
        </div>
        <form id="accountForm" method="post" action="${pageContext.request.contextPath}/accounts/${account.id}/edit">
            <div class="activity-form-content">
                <div class="activity-form-section">
                    <h3>Thông tin đăng nhập</h3>
                    <p>Để trống mật khẩu nếu không muốn thay đổi mật khẩu hiện tại.</p>
                </div>
                <div class="form-grid activity-form-grid account-form-grid">
                    <div class="field">
                        <label for="username">Tên đăng nhập <span class="required-mark">*</span></label>
                        <input type="text" id="username" name="username" value="<c:out value='${account.username}'/>" maxlength="100" autocomplete="username" required>
                        <c:if test="${not empty fieldErrors['username']}"><span class="field-error"><c:out value="${fieldErrors['username']}"/></span></c:if>
                    </div>
                    <div class="field">
                        <label for="password">Mật khẩu mới</label>
                        <input type="password" id="password" name="password" maxlength="255" autocomplete="new-password" placeholder="Để trống để giữ mật khẩu hiện tại">
                        <span class="field-hint account-hint">Mật khẩu hiện tại sẽ được giữ nguyên nếu để trống.</span>
                        <c:if test="${not empty fieldErrors['password']}"><span class="field-error"><c:out value="${fieldErrors['password']}"/></span></c:if>
                    </div>
                    <div class="field">
                        <label for="type">Loại tài khoản <span class="required-mark">*</span></label>
                        <select id="type" name="type" required>
                            <option value="">-- Chọn loại tài khoản --</option>
                            <c:forEach items="${accountTypes}" var="t">
                                <option value="${fn:escapeXml(t)}" <c:if test="${t == account.type}">selected</c:if>><c:out value="${t}"/></option>
                            </c:forEach>
                        </select>
                        <c:if test="${not empty fieldErrors['type']}"><span class="field-error"><c:out value="${fieldErrors['type']}"/></span></c:if>
                    </div>
                    <div class="field">
                        <label for="role">Vai trò <span class="required-mark">*</span></label>
                        <select id="role" name="role" required>
                            <option value="">-- Chọn vai trò --</option>
                            <c:forEach items="${rolesByType}" var="entry">
                                <c:forEach items="${entry.value}" var="r">
                                    <option value="${fn:escapeXml(r)}" data-type="${fn:escapeXml(entry.key)}" <c:if test="${r == account.role}">selected</c:if>><c:out value="${r}"/></option>
                                </c:forEach>
                            </c:forEach>
                        </select>
                        <span class="field-hint account-hint">Vai trò được lọc theo loại tài khoản đã chọn.</span>
                        <c:if test="${not empty fieldErrors['role']}"><span class="field-error"><c:out value="${fieldErrors['role']}"/></span></c:if>
                    </div>
                    <div class="field">
                        <label for="status">Trạng thái <span class="required-mark">*</span></label>
                        <select id="status" name="status" required>
                            <option value="">-- Chọn trạng thái --</option>
                            <c:forEach items="${accountStatuses}" var="s">
                                <option value="${fn:escapeXml(s)}" <c:if test="${s == account.status}">selected</c:if>><c:out value="${s}"/></option>
                            </c:forEach>
                        </select>
                        <c:if test="${not empty fieldErrors['status']}"><span class="field-error"><c:out value="${fieldErrors['status']}"/></span></c:if>
                    </div>
                </div>
            </div>
            <div class="form-actions activity-form-actions">
                <a class="button activity-cancel-button" href="${pageContext.request.contextPath}/accounts/${account.id}">Hủy</a>
                <button class="button button-primary" type="submit">Lưu thay đổi</button>
            </div>
        </form>
    </section>
</main>
<script src="${pageContext.request.contextPath}/assets/js/account-form.js"></script>
        </div>
    </div>
</div>
</body>
</html>
