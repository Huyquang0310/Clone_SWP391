<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="activeNav" value="registration-forms" scope="request"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:choose><c:when test="${form.id > 0}">Chỉnh sửa đợt đăng ký</c:when><c:otherwise>Tạo đợt đăng ký</c:otherwise></c:choose> | SWP391-G1</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/activity.css?v=20261021-11">
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
                        <span class="eyebrow">SWP391-G1 <span aria-hidden="true">/</span> QUẢN LÝ FORM ĐĂNG KÝ</span>
                        <h1><c:choose><c:when test="${form.id > 0}">Chỉnh sửa đợt đăng ký</c:when><c:otherwise>Tạo đợt đăng ký</c:otherwise></c:choose></h1>
                        <p>Tạo form cho sinh viên đăng ký tham gia hoạt động ngoại khóa.</p>
                    </div>
                    <a class="button activity-create-button" href="${pageContext.request.contextPath}/registration-forms">
                        <svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#arrow-left"/></svg>
                        Danh sách form
                    </a>
                </header>

                <c:if test="${not empty error}">
                    <div class="alert alert-error activity-alert"><c:out value="${error}"/></div>
                </c:if>

                <section class="card activity-form-card">
                    <div class="activity-table-heading">
                        <div>
                            <h2>Thông tin đợt đăng ký</h2>
                            <p>Form chỉ hiển thị sinh viên khi được mở, đúng thời hạn và hoạt động đã được duyệt.</p>
                        </div>
                    </div>
                    <form method="post" action="${pageContext.request.contextPath}/registration-forms">
                        <input type="hidden" name="action" value="save">
                        <input type="hidden" name="id" value="${form.id}">

                        <div class="activity-form-content">
                            <div class="activity-form-section">
                                <h3>Thông tin cơ bản</h3>
                                <p>Chọn hoạt động và cung cấp nội dung hướng dẫn đăng ký.</p>
                            </div>
                            <div class="form-grid activity-form-grid">
                                <div class="field field-wide">
                                    <label for="activityId">Hoạt động <span class="required-mark">*</span></label>
                                    <select id="activityId" name="activityId" required
                                            <c:if test="${form.registrationCount > 0}">disabled</c:if>>
                                        <option value="">-- Chọn hoạt động --</option>
                                        <c:forEach items="${activities}" var="a">
                                            <option value="${a.id}" <c:if test="${form.activityId == a.id}">selected</c:if>>
                                                <c:out value="${a.code}"/> - <c:out value="${a.name}"/>
                                            </option>
                                        </c:forEach>
                                    </select>
                                    <c:if test="${empty activities}">
                                        <small class="field-hint">
                                            <c:choose>
                                                <c:when test="${hasEligibleApprovedActivities}">Tất cả hoạt động đã duyệt và chưa diễn ra đều đã có đợt đăng ký.</c:when>
                                                <c:otherwise>Chưa có hoạt động nào vừa được duyệt vừa ở trạng thái chưa diễn ra để tạo form.</c:otherwise>
                                            </c:choose>
                                        </small>
                                    </c:if>
                                </div>
                                <div class="field field-wide">
                                    <label for="title">Tiêu đề form <span class="required-mark">*</span></label>
                                    <input id="title" name="title" type="text" maxlength="200" required
                                           <c:if test="${form.registrationCount > 0}">disabled</c:if>
                                           value="<c:out value='${form.title}'/>"
                                           placeholder="Ví dụ: Đăng ký tham gia workshop">
                                </div>
                                <div class="field field-wide">
                                    <label for="description">Mô tả / hướng dẫn</label>
                                    <textarea id="description" name="description" rows="5" maxlength="4000"
                                              <c:if test="${form.registrationCount > 0}">disabled</c:if>
                                              placeholder="Nhập thông tin sinh viên cần biết trước khi đăng ký..."><c:out value="${form.description}"/></textarea>
                                </div>
                            </div>

                            <div class="activity-form-section">
                                <h3>Thời gian và trạng thái</h3>
                                <p>Form bản nháp có thời gian mở sẽ tự động mở đúng hạn; form đang mở tự động đóng khi hết hạn. Để trống nếu không giới hạn thời gian.</p>
                            </div>
                            <div class="form-grid activity-form-grid">
                                <div class="field">
                                    <label for="openAt">Thời gian mở đăng ký</label>
                                    <input id="openAt" name="openAt" type="datetime-local" min="${minimumRegistrationTime}"
                                           value="${form.openAt}" <c:if test="${form.registrationCount > 0}">disabled</c:if>>
                                </div>
                                <div class="field">
                                    <label for="closeAt">Thời gian đóng đăng ký</label>
                                    <input id="closeAt" name="closeAt" type="datetime-local" min="${minimumRegistrationTime}"
                                           value="${form.closeAt}" <c:if test="${form.registrationCount > 0}">disabled</c:if>>
                                </div>
                                <div class="field">
                                    <label for="status">Trạng thái</label>
                                    <select id="status" name="status"
                                            <c:if test="${form.registrationCount > 0}">disabled</c:if>>
                                        <option value="DRAFT" <c:if test="${form.status eq 'DRAFT'}">selected</c:if>>Bản nháp</option>
                                        <option value="OPENING" <c:if test="${form.status eq 'OPENING'}">selected</c:if>>Đang mở đăng ký</option>
                                        <option value="CLOSED" <c:if test="${form.status eq 'CLOSED'}">selected</c:if>>Đã đóng đăng ký</option>
                                    </select>
                                </div>
                            </div>
                        </div>

                        <div class="form-actions activity-form-actions">
                            <a class="button activity-cancel-button" href="${pageContext.request.contextPath}/registration-forms">Hủy</a>
                            <c:choose>
                                <c:when test="${form.registrationCount > 0}">
                                    <span>Không thể chỉnh sửa form đã có sinh viên đăng ký.</span>
                                </c:when>
                                <c:otherwise>
                                    <button class="button button-primary" type="submit">
                                        <c:choose><c:when test="${form.id > 0}">Lưu thay đổi</c:when><c:otherwise>Tạo form</c:otherwise></c:choose>
                                    </button>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </form>
                </section>
            </main>
        </div>
    </div>
</div>
</body>
</html>
