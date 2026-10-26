<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="activeNav" value="registration-forms" scope="request"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Gửi đăng ký | SWP391-G1</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/activity.css?v=20261020-11">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/registration.css?v=20261021-10">
</head>
<body>
<div class="activity-app-shell">
    <jsp:include page="/WEB-INF/views/layout/app-navigation.jsp"/>
    <div class="activity-app-layout">
        <jsp:include page="/WEB-INF/views/layout/app-sidebar.jsp"/>
        <div class="activity-app-main">
            <main class="page activity-list-page activity-form-page student-registration-form-page">
                <header class="activity-hero">
                    <div class="activity-hero-copy">
                        <span class="eyebrow">SWP391-G1 <span aria-hidden="true">/</span> ĐĂNG KÝ HOẠT ĐỘNG</span>
                        <h1>Đăng ký tham gia</h1>
                        <p>Kiểm tra thông tin hoạt động và gửi đăng ký của bạn.</p>
                    </div>
                    <a class="button activity-create-button" href="${pageContext.request.contextPath}/registration-forms?action=student">
                        <svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#arrow-left"/></svg>
                        Danh sách hoạt động
                    </a>
                </header>

                <c:if test="${not empty error}">
                    <div class="alert alert-error activity-alert"><c:out value="${error}"/></div>
                </c:if>

                <section class="card activity-form-card student-registration-detail">
                    <div class="activity-table-heading">
                        <div>
                            <h2><c:out value="${form.title}"/></h2>
                            <p>Thông tin đăng ký hoạt động</p>
                        </div>
                        <span class="badge status-pill status-green">Đang mở đăng ký</span>
                    </div>
                    <div class="activity-form-content">
                        <h3 class="student-registration-activity-name"><c:out value="${form.activityName}"/></h3>
                        <p class="student-registration-detail-description"><c:out value="${form.description}"/></p>
                        <p class="student-registration-deadline">
                            <strong>Số chỗ:</strong>
                            <c:choose>
                                <c:when test="${not empty form.maxParticipants}">
                                    Còn <c:out value="${form.maxParticipants - form.reservedSeatCount}"/> / <c:out value="${form.maxParticipants}"/>
                                </c:when>
                                <c:otherwise>Không giới hạn</c:otherwise>
                            </c:choose>
                        </p>
                        <c:if test="${not empty savedProfile}">
                            <p>Thông tin đã lưu trong hồ sơ được điền sẵn; bạn có thể cập nhật trước khi gửi.</p>
                        </c:if>
                        <form method="post" action="${pageContext.request.contextPath}/registration-forms">
                            <input type="hidden" name="action" value="register">
                            <input type="hidden" name="id" value="${form.id}">
                            <div class="form-grid activity-form-grid">
                                <div class="field field-wide">
                                    <h3>Thông tin sinh viên</h3>
                                    <p>Kiểm tra và bổ sung thông tin dưới đây trước khi gửi đăng ký.</p>
                                </div>
                                <div class="field">
                                    <label for="studentCode">Mã sinh viên <span class="required-mark">*</span></label>
                                    <input id="studentCode" name="studentCode" type="text" maxlength="50"
                                           value="<c:out value='${personalInfo.studentCode}'/>" required>
                                </div>
                                <div class="field">
                                    <label for="studentName">Họ và tên <span class="required-mark">*</span></label>
                                    <input id="studentName" name="studentName" type="text" maxlength="255"
                                           value="<c:out value='${personalInfo.studentName}'/>" required>
                                </div>
                                <div class="field">
                                    <label for="classCode">Lớp <span class="required-mark">*</span></label>
                                    <input id="classCode" name="classCode" type="text" maxlength="50"
                                           value="<c:out value='${personalInfo.classCode}'/>" required>
                                </div>
                                <div class="field">
                                    <label for="email">Email <span class="required-mark">*</span></label>
                                    <input id="email" name="email" type="email" maxlength="255"
                                           value="<c:out value='${personalInfo.email}'/>" required>
                                </div>
                                <c:forEach items="${form.fields}" var="field">
                                    <div class="field field-wide">
                                        <label for="field_${field.fieldKey}">
                                            <c:out value="${field.label}"/>
                                            <c:if test="${field.required}"><span class="required-mark">*</span></c:if>
                                        </label>
                                        <c:choose>
                                            <c:when test="${field.fieldType eq 'TEXTAREA'}">
                                                <textarea id="field_${field.fieldKey}" name="field_${field.fieldKey}" rows="4"
                                                          <c:if test="${field.required}">required</c:if>><c:out value="${field.value}"/></textarea>
                                            </c:when>
                                            <c:when test="${field.fieldType eq 'SELECT'}">
                                                <select id="field_${field.fieldKey}" name="field_${field.fieldKey}"
                                                        <c:if test="${field.required}">required</c:if>>
                                                    <option value="">-- Chọn --</option>
                                                    <c:forTokens items="${field.options}" delims="&#10;" var="option">
                                                        <option value="<c:out value='${option}'/>"
                                                                <c:if test="${field.value eq option}">selected</c:if>><c:out value="${option}"/></option>
                                                    </c:forTokens>
                                                </select>
                                            </c:when>
                                            <c:when test="${field.fieldType eq 'CHECKBOX'}">
                                                <input id="field_${field.fieldKey}" name="field_${field.fieldKey}" type="checkbox"
                                                       value="true" <c:if test="${field.value eq 'true'}">checked</c:if>
                                                       <c:if test="${field.required}">required</c:if>>
                                            </c:when>
                                            <c:when test="${field.fieldType eq 'NUMBER'}">
                                                <input id="field_${field.fieldKey}" name="field_${field.fieldKey}" type="number"
                                                       value="<c:out value='${field.value}'/>"
                                                       <c:if test="${field.required}">required</c:if>>
                                            </c:when>
                                            <c:when test="${field.fieldType eq 'DATE'}">
                                                <input id="field_${field.fieldKey}" name="field_${field.fieldKey}" type="date"
                                                       value="<c:out value='${field.value}'/>"
                                                       <c:if test="${field.required}">required</c:if>>
                                            </c:when>
                                            <c:when test="${field.fieldType eq 'EMAIL'}">
                                                <input id="field_${field.fieldKey}" name="field_${field.fieldKey}" type="email"
                                                       value="<c:out value='${field.value}'/>"
                                                       <c:if test="${field.required}">required</c:if>>
                                            </c:when>
                                            <c:when test="${field.fieldType eq 'PHONE'}">
                                                <input id="field_${field.fieldKey}" name="field_${field.fieldKey}" type="tel"
                                                       value="<c:out value='${field.value}'/>"
                                                       <c:if test="${field.required}">required</c:if>>
                                            </c:when>
                                            <c:otherwise>
                                                <input id="field_${field.fieldKey}" name="field_${field.fieldKey}" type="text"
                                                       value="<c:out value='${field.value}'/>"
                                                       <c:if test="${field.required}">required</c:if>>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </c:forEach>
                                <div class="field field-wide">
                                    <label for="note">Ghi chú thêm <span class="registration-optional">(không bắt buộc)</span></label>
                                    <textarea id="note" name="note" rows="5" maxlength="2000"
                                              placeholder="Thông tin bạn muốn ban tổ chức biết..."></textarea>
                                </div>
                            </div>
                            <div class="form-actions activity-form-actions student-registration-actions">
                                <a class="button activity-cancel-button" href="${pageContext.request.contextPath}/registration-forms?action=student">Quay lại</a>
                                <button class="button button-primary" type="submit">Gửi đăng ký</button>
                            </div>
                        </form>
                    </div>
                </section>
            </main>
        </div>
    </div>
</div>
</body>
</html>
