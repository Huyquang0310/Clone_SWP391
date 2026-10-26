<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="activeNav" value="partner-companies" scope="request"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:choose><c:when test="${isEdit}">Cập nhật công ty đối tác</c:when><c:otherwise>Thêm công ty đối tác</c:otherwise></c:choose> | SWP391-G1</title>
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
            <h1><c:choose><c:when test="${isEdit}">Cập nhật công ty</c:when><c:otherwise>Thêm công ty đối tác</c:otherwise></c:choose></h1>
            <p>Nhập thông tin liên hệ và thông tin quản lý của công ty.</p>
        </div>
        <a class="button activity-create-button" href="${pageContext.request.contextPath}/partner-companies">
            <svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#arrow-left"/></svg>
            Danh sách công ty
        </a>
    </header>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-error activity-alert"><c:out value="${errorMessage}"/></div>
    </c:if>

    <section class="card activity-form-card">
        <div class="activity-table-heading">
            <div>
                <h2>Thông tin công ty</h2>
                <p>Các trường có dấu <span class="required-mark">*</span> là bắt buộc</p>
            </div>
        </div>
        <form method="post" action="${pageContext.request.contextPath}/partner-companies">
            <input type="hidden" name="action" value="${isEdit ? 'update' : 'create'}">
            <c:if test="${isEdit}">
                <input type="hidden" name="id" value="${partnerCompany.id}">
            </c:if>

            <div class="activity-form-content">
                <div class="activity-form-section">
                    <h3>Thông tin cơ bản</h3>
                    <p>Thông tin nhận diện và liên hệ của công ty.</p>
                </div>
                <div class="form-grid activity-form-grid">
                    <div class="field">
                        <label for="code">Mã công ty <span class="required-mark">*</span></label>
                        <input id="code" name="code" type="text" maxlength="50" required
                               value="<c:out value='${partnerCompany.code}'/>">
                    </div>
                    <div class="field">
                        <label for="name">Tên công ty <span class="required-mark">*</span></label>
                        <input id="name" name="name" type="text" maxlength="255" required
                               value="<c:out value='${partnerCompany.name}'/>">
                    </div>
                    <div class="field">
                        <label for="email">Email</label>
                        <input id="email" name="email" type="email" maxlength="255"
                               value="<c:out value='${partnerCompany.email}'/>">
                    </div>
                    <div class="field">
                        <label for="phone">Số điện thoại</label>
                        <input id="phone" name="phone" type="tel" maxlength="11" inputmode="numeric"
                               value="<c:out value='${partnerCompany.phoneNumber}'/>">
                    </div>
                    <div class="field field-wide">
                        <label for="website">Website</label>
                        <input id="website" name="website" type="url" maxlength="500" placeholder="https://example.com"
                               value="<c:out value='${partnerCompany.website}'/>">
                    </div>
                    <div class="field field-wide">
                        <label for="address">Địa chỉ</label>
                        <input id="address" name="address" type="text" maxlength="500"
                               value="<c:out value='${partnerCompany.address}'/>">
                    </div>
                    <div class="field field-wide">
                        <label for="description">Mô tả</label>
                        <textarea id="description" name="description"><c:out value="${partnerCompany.description}"/></textarea>
                    </div>
                    <c:if test="${isEdit}">
                        <div class="field">
                            <label for="status">Trạng thái</label>
                            <select id="status" name="status">
                                <option value="ACTIVE" <c:if test="${partnerCompany.status == 'ACTIVE'}">selected</c:if>>Đang hoạt động</option>
                                <option value="INACTIVE" <c:if test="${partnerCompany.status == 'INACTIVE'}">selected</c:if>>Không hoạt động</option>
                            </select>
                        </div>
                    </c:if>
                </div>
            </div>

            <div class="form-actions activity-form-actions">
                <a class="button activity-cancel-button" href="${pageContext.request.contextPath}/partner-companies">Hủy</a>
                <button class="button button-primary" type="submit">
                    <c:choose><c:when test="${isEdit}">Lưu thay đổi</c:when><c:otherwise>Thêm công ty</c:otherwise></c:choose>
                </button>
            </div>
        </form>
    </section>
</main>
        </div>
    </div>
</div>
</body>
</html>
